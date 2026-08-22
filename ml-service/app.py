"""
Microservicio de prediccion energetica (EnergiAI).

Carga el bundle entrenado por el equipo de Data Science (modelo_energia.joblib)
y expone la logica de `evaluar_vivienda` del notebook como un endpoint HTTP,
para que el backend Java pueda consumirla.
"""

import joblib
import pandas as pd
from fastapi import FastAPI
from pydantic import BaseModel, Field

BUNDLE_PATH = "modelo_energia.joblib"

bundle = joblib.load(BUNDLE_PATH)
modelo = bundle["modelo"]
encoder = bundle["encoder"]
CATEGORICAL_COLS = bundle["categorical_cols"]
NUMERIC_COLS = bundle["numeric_cols"]
FEATURE_COLUMNS = bundle["feature_columns"]
TARIFA_KWH = bundle["tarifa_kwh"]
BENCHMARK_CONSUMO_HABITANTE = bundle["benchmark_consumo_habitante"]
EQUIPOS_PROMEDIO_TIPO = {str(k): float(v) for k, v in bundle["equipos_promedio_tipo"].items()}

ORDEN_CATEGORIAS = ["Eficiente", "Moderado", "Ineficiente"]


def _normalizar(d):
    """Convierte llaves/valores de numpy a tipos nativos de Python."""
    if not d:
        return {}
    salida = {}
    for k, v in d.items():
        salida[str(k)] = _normalizar(v) if isinstance(v, dict) else float(v)
    return salida


# Estadisticas del dataset. Se leen del bundle cuando existen (v1.1 en adelante);
# si el bundle es anterior, se usan los valores calculados sobre el mismo dataset
# y la misma etiqueta (categoria_indice) con la que se entreno el modelo.
# Asi el servicio arranca igual con cualquier version del bundle.
BENCHMARK_POR_TIPO = _normalizar(bundle.get("benchmark_por_tipo")) or {
    "Casa": {"Eficiente": 50.13, "Moderado": 76.82, "Ineficiente": 157.69},
    "Departamento": {"Eficiente": 50.33, "Moderado": 81.46, "Ineficiente": 157.91},
    "Local": {"Eficiente": 72.26, "Moderado": 125.10, "Ineficiente": 345.69},
}

DISTRIBUCION_CATEGORIAS = _normalizar(bundle.get("distribucion_categorias")) or {
    "Eficiente": 33.0, "Moderado": 33.0, "Ineficiente": 34.0,
}

DISTRIBUCION_POR_TIPO = _normalizar(bundle.get("distribucion_por_tipo")) or {
    "Casa": {"Eficiente": 31.87, "Moderado": 34.18, "Ineficiente": 33.96},
    "Departamento": {"Eficiente": 37.56, "Moderado": 28.31, "Ineficiente": 34.13},
    "Local": {"Eficiente": 28.04, "Moderado": 38.07, "Ineficiente": 33.89},
}

# Estas dos solo existen a partir del bundle v1.1; si no estan, se omiten del resultado.
DISTRIBUCION_POR_PAIS = _normalizar(bundle.get("distribucion_por_pais"))
DISTRIBUCION_POR_LOCALIDAD = _normalizar(bundle.get("distribucion_por_localidad"))

# Copiado del notebook (celda de recomendaciones); no viaja dentro del bundle.
RECOMENDACIONES_POR_CATEGORIA = {
    "Eficiente": [
        "El inmueble mantiene un consumo eficiente; se recomienda continuar con los hábitos actuales de uso.",
        "Realizar mantenimiento preventivo periódico a los equipos (limpieza de filtros, revisión de instalaciones) para conservar su eficiencia.",
        "Usar temporizadores o enchufes inteligentes para mantener el control del consumo en el tiempo.",
    ],
    "Moderado": [
        "Revisar los equipos de mayor consumo y evaluar su reemplazo por versiones de bajo consumo (etiqueta A o tecnología inverter).",
        "Desconectar equipos en modo de espera (standby), ya que consumen energía sin estar en uso activo.",
        "Aprovechar la luz natural y la ventilación para reducir el uso de iluminación y climatización artificial.",
        "Distribuir el uso de electrodomésticos de forma que no coincidan varios equipos de alta potencia al mismo tiempo.",
    ],
    "Ineficiente": [
        "Realizar una auditoría energética del inmueble para identificar los equipos con mayor consumo.",
        "Sustituir progresivamente electrodomésticos antiguos por modelos de alta eficiencia energética.",
        "Reducir las horas de uso de equipos de climatización y calentadores eléctricos.",
        "Instalar iluminación LED en todo el inmueble.",
        "Evaluar el aislamiento térmico del inmueble para reducir la necesidad de climatización artificial.",
    ],
}

app = FastAPI(
    title="EnergiAI ML Service",
    description="Expone el modelo Random Forest de clasificacion energetica entrenado por Data Science.",
    version="1.0",
)


class PrediccionRequest(BaseModel):
    pais: str
    localidad: str
    tipo_inmueble: str
    numero_habitantes: int = Field(gt=0)
    cantidad_equipos: int = Field(gt=0)
    frecuencia_uso: str
    horas_alto_consumo: int = Field(ge=0, le=24)
    uso_horario_pico: bool
    temperatura_ambiente: float
    consumo_kwh: float = Field(gt=0)


def generar_recomendaciones(datos: dict, categoria: str) -> list[str]:
    recomendaciones = list(RECOMENDACIONES_POR_CATEGORIA.get(categoria, []))

    if datos.get("uso_horario_pico"):
        recomendaciones.append(
            "Se detectó uso en horario pico: trasladar equipos de alto consumo (lavadora, secadora, plancha) "
            "fuera de este horario puede reducir el costo de la factura."
        )

    if datos.get("horas_alto_consumo", 0) >= 10:
        recomendaciones.append(
            "Las horas de alto consumo reportadas son elevadas; limitar el uso simultáneo de equipos de alta "
            "potencia ayuda a reducir el consumo mensual."
        )

    tipo = datos.get("tipo_inmueble")
    promedio_tipo = EQUIPOS_PROMEDIO_TIPO.get(tipo)
    if promedio_tipo is not None and datos.get("cantidad_equipos", 0) > promedio_tipo * 1.3:
        recomendaciones.append(
            f"La cantidad de equipos reportada ({datos['cantidad_equipos']}) supera el promedio observado "
            f"para inmuebles tipo {tipo} ({promedio_tipo:.1f}); se recomienda evaluar cuáles son realmente necesarios."
        )

    return recomendaciones


def estimar_ahorro(consumo_kwh, numero_habitantes, categoria_actual, categoria_objetivo="Eficiente"):
    consumo_habitante_actual = consumo_kwh / numero_habitantes
    consumo_habitante_objetivo = BENCHMARK_CONSUMO_HABITANTE[categoria_objetivo]

    costo_actual_mensual = round(consumo_kwh * TARIFA_KWH, 2)
    costo_actual_anual = round(costo_actual_mensual * 12, 2)

    resultado = {
        "categoria_actual": categoria_actual,
        "categoria_objetivo": categoria_objetivo,
        "costo_actual_mensual": costo_actual_mensual,
        "costo_actual_anual": costo_actual_anual,
    }

    ya_en_objetivo = ORDEN_CATEGORIAS.index(categoria_actual) <= ORDEN_CATEGORIAS.index(categoria_objetivo)
    if ya_en_objetivo or consumo_habitante_actual <= consumo_habitante_objetivo:
        resultado.update({
            "ahorro_kwh_mensual": 0.0,
            "ahorro_kwh_anual": 0.0,
            "ahorro_monetario_mensual": 0.0,
            "ahorro_monetario_anual": 0.0,
            "porcentaje_reduccion_estimado": 0.0,
            "mensaje": "El inmueble ya se encuentra en la categoría objetivo o en una mejor.",
        })
        return resultado

    ahorro_habitante = consumo_habitante_actual - consumo_habitante_objetivo
    ahorro_kwh_mensual = round(ahorro_habitante * numero_habitantes, 2)
    ahorro_kwh_anual = round(ahorro_kwh_mensual * 12, 2)
    ahorro_monetario_mensual = round(ahorro_kwh_mensual * TARIFA_KWH, 2)
    ahorro_monetario_anual = round(ahorro_monetario_mensual * 12, 2)
    porcentaje_reduccion = round((ahorro_kwh_mensual / consumo_kwh) * 100, 1)

    consumo_proyectado_mensual = round(consumo_kwh - ahorro_kwh_mensual, 2)
    costo_proyectado_mensual = round(consumo_proyectado_mensual * TARIFA_KWH, 2)

    resultado.update({
        "consumo_proyectado_mensual_kwh": consumo_proyectado_mensual,
        "costo_proyectado_mensual": costo_proyectado_mensual,
        "costo_proyectado_anual": round(costo_proyectado_mensual * 12, 2),
        "ahorro_kwh_mensual": ahorro_kwh_mensual,
        "ahorro_kwh_anual": ahorro_kwh_anual,
        "ahorro_monetario_mensual": ahorro_monetario_mensual,
        "ahorro_monetario_anual": ahorro_monetario_anual,
        "porcentaje_reduccion_estimado": porcentaje_reduccion,
    })
    return resultado


def comparar_con_categoria(consumo_kwh, numero_habitantes, tipo_inmueble, categoria):
    """
    Compara el consumo por habitante del inmueble contra el promedio de inmuebles
    del mismo tipo y la misma categoria.
    """
    consumo_por_habitante = round(consumo_kwh / numero_habitantes, 2)
    promedio = BENCHMARK_POR_TIPO.get(tipo_inmueble, {}).get(categoria)

    if promedio is None:
        return None

    diferencia = round(((consumo_por_habitante - promedio) / promedio) * 100, 1)

    return {
        "consumo_por_habitante": consumo_por_habitante,
        "promedio_tipo_categoria": promedio,
        "tipo_inmueble": tipo_inmueble,
        "categoria": categoria,
        "diferencia_porcentual": diferencia,
        "por_encima_del_promedio": diferencia > 0,
    }


def contexto_dataset(categoria, tipo_inmueble, pais, localidad):
    """
    Ubica al inmueble dentro del dataset: que porcentaje de inmuebles comparables
    cae en la misma categoria (en general, por tipo, por pais y por localidad).
    """
    distrib_tipo = DISTRIBUCION_POR_TIPO.get(tipo_inmueble, {})
    distrib_pais = DISTRIBUCION_POR_PAIS.get(pais, {})
    distrib_localidad = DISTRIBUCION_POR_LOCALIDAD.get(localidad, {})

    return {
        "porcentaje_general": DISTRIBUCION_CATEGORIAS.get(categoria),
        "porcentaje_tu_tipo_inmueble": distrib_tipo.get(categoria),
        "porcentaje_tu_pais": distrib_pais.get(categoria),
        "porcentaje_tu_localidad": distrib_localidad.get(categoria),
        "distribucion_tu_tipo_inmueble": distrib_tipo,
        "distribucion_tu_pais": distrib_pais,
        "distribucion_tu_localidad": distrib_localidad,
    }


@app.get("/health")
def health():
    return {
        "status": "ok",
        "modelo_version": bundle.get("version"),
        "fecha_entrenamiento": bundle.get("fecha_entrenamiento"),
        "clases": bundle.get("clases"),
    }


@app.post("/predecir")
def predecir(request: PrediccionRequest):
    datos = request.model_dump()

    fila = pd.DataFrame([datos])
    fila["uso_horario_pico"] = fila["uso_horario_pico"].astype(int)

    cat_encoded = encoder.transform(fila[CATEGORICAL_COLS])
    cat_df = pd.DataFrame(
        cat_encoded,
        columns=encoder.get_feature_names_out(CATEGORICAL_COLS),
        index=fila.index,
    )

    X = pd.concat([fila[NUMERIC_COLS], cat_df], axis=1)
    X = X.reindex(columns=FEATURE_COLUMNS, fill_value=0)

    categoria = str(modelo.predict(X)[0])
    probabilidades = {
        str(clase): float(round(p, 3))
        for clase, p in zip(modelo.classes_, modelo.predict_proba(X)[0])
    }

    return {
        "categoria": categoria,
        "probabilidad": probabilidades[categoria],
        "probabilidades": probabilidades,
        "estimacion_financiera": estimar_ahorro(
            consumo_kwh=datos["consumo_kwh"],
            numero_habitantes=datos["numero_habitantes"],
            categoria_actual=categoria,
        ),
        "comparacion": comparar_con_categoria(
            consumo_kwh=datos["consumo_kwh"],
            numero_habitantes=datos["numero_habitantes"],
            tipo_inmueble=datos["tipo_inmueble"],
            categoria=categoria,
        ),
        "contexto_dataset": contexto_dataset(
            categoria=categoria,
            tipo_inmueble=datos["tipo_inmueble"],
            pais=datos["pais"],
            localidad=datos["localidad"],
        ),
        "recomendaciones": generar_recomendaciones(datos, categoria),
    }
