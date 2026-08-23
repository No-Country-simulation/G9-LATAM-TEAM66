# EnergiAI

**MVP de análisis inteligente del consumo energético.** Clasifica el perfil de eficiencia de un
inmueble con un modelo de machine learning, estima el costo mensual y genera recomendaciones
de ahorro personalizadas.

Proyecto desarrollado por el **G9 · LATAM · TEAM 66** para No Country.

### 🔗 Aplicación en línea

| | |
|---|---|
| **Aplicación** | **http://159.54.159.238/** |
| Documentación de la API | http://159.54.159.238/swagger-ui/index.html |

Desplegada en una VM de Oracle Cloud (OCI).

---

## Qué hace

El usuario ingresa los datos de consumo de su inmueble y la aplicación devuelve:

| Resultado | Ejemplo |
|---|---|
| **Categoría de eficiencia** | `Ineficiente` |
| **Confianza del modelo** | 95.7% |
| **Costo estimado** | $457.50 / mes |
| **Ahorro potencial** | $298.12 / mes (65.2% menos) |
| **Comparación con su categoría** | 152.5 kWh/hab vs. 157.7 kWh promedio |
| **Recomendaciones** | Personalizadas según horario pico, equipos y horas de uso |

La clasificación la hace un **Random Forest** entrenado sobre 10.000 registros
(88% de exactitud), no reglas fijas.

---

## Arquitectura

```
   Navegador
       │
       ▼
   ┌────────────────┐
   │  nginx  :80    │  sirve la página y reenvía la API (mismo origen, sin CORS)
   └───────┬────────┘
           │
           ▼
   ┌────────────────┐        ┌──────────────────┐
   │ Backend  :8080 │───────▶│ ml-service :8000 │  modelo Random Forest
   │  Spring Boot   │        │  Python/FastAPI  │
   └───────┬────────┘        └──────────────────┘
           │
           ▼
   ┌────────────────┐
   │  MySQL  :3306  │  historial de consultas
   └────────────────┘
```

**Por qué un microservicio aparte para el modelo:** el modelo se entrena en Python y se
serializa como `.joblib` (formato de scikit-learn). Java no puede leer ese archivo, así que
`ml-service` lo carga y lo expone por HTTP para que el backend lo consulte.

---

## Stack

| Capa | Tecnología |
|---|---|
| Frontend | HTML + CSS + JavaScript (sin framework) |
| Backend | Java 25 · Spring Boot 4.1 · Spring Data JPA |
| ML | Python 3.12 · FastAPI · scikit-learn 1.6.1 |
| Base de datos | MySQL 8.4 |
| Servidor web | nginx (alpine) |
| Infraestructura | Docker Compose · Oracle Cloud (OCI) |

---

## Cómo ejecutarlo

**Requisito:** Docker Desktop instalado y corriendo.

```bash
git clone https://github.com/No-Country-simulation/G9-LATAM-TEAM66.git
cd G9-LATAM-TEAM66
docker compose up --build -d
```

> La primera vez tarda varios minutos: compila el backend con Maven e instala
> scikit-learn y pandas. Las siguientes veces es rápido por la caché de Docker.

Cuando termine:

| Qué | Dirección |
|---|---|
| **La aplicación** | http://localhost |
| Documentación de la API (Swagger) | http://localhost/swagger-ui/index.html |
| Estado del modelo | http://localhost:8000/health |

Para detener todo:

```bash
docker compose down
```

---

## La API

### `POST /analisis-energetico`

**Petición**

```json
{
  "pais": "México",
  "localidad": "Monterrey",
  "temperatura_ambiente": 30.5,
  "tipo_inmueble": "Casa",
  "numero_habitantes": 4,
  "consumo_kwh": 610.0,
  "uso_horario_pico": true,
  "cantidad_equipos": 22,
  "frecuencia_uso": "Alta",
  "horas_alto_consumo": 13
}
```

**Respuesta**

```json
{
  "id_registro": 9,
  "categoria": "Ineficiente",
  "probabilidad": 0.957,
  "consumo_kwh": 610.0,
  "costo_estimado": 457.5,
  "ahorro_estimado_mensual": 298.12,
  "porcentaje_reduccion_estimado": 65.2,
  "comparacion": {
    "consumo_por_habitante": 152.5,
    "promedio_tipo_categoria": 157.69,
    "diferencia_porcentual": -3.3,
    "por_encima_del_promedio": false
  },
  "contexto_dataset": {
    "porcentaje_general": 34.0,
    "porcentaje_tu_tipo_inmueble": 33.96,
    "porcentaje_tu_pais": 35.38,
    "porcentaje_tu_localidad": 39.07
  },
  "recomendaciones": ["..."]
}
```

### Valores aceptados

| Campo | Valores |
|---|---|
| `pais` | `Colombia` · `México` · `Perú` |
| `localidad` | 15 ciudades (5 por país) |
| `tipo_inmueble` | `Casa` · `Departamento` · `Local` |
| `frecuencia_uso` | `Alta` · `Media` · `Baja` |
| `horas_alto_consumo` | 0 a 24 |

Todos los campos son obligatorios. Si falta alguno, la API responde `400` indicando cuál.

---

## Estructura del proyecto

```
G9-LATAM-TEAM66/
├── backend/                 API REST en Spring Boot
│   ├── src/main/java/…      controller · service · dto · model · repository · client
│   ├── init/                schema.sql (se ejecuta al crear la base)
│   └── Dockerfile
├── ml-service/              Microservicio del modelo
│   ├── app.py               FastAPI: POST /predecir · GET /health
│   ├── requirements.txt
│   └── Dockerfile
├── data-science/            Notebook de entrenamiento y análisis
├── database/                Dataset (10.000 registros)
├── frontend/
│   └── energiai.html        Interfaz de usuario
├── web/
│   └── nginx.conf           Configuración del servidor web
├── modelo_energia.joblib    Modelo entrenado (bundle v1.1)
└── docker-compose.yml
```

---

## El modelo

Entrenado con `RandomForestClassifier` sobre 10.000 registros sintéticos de Colombia,
México y Perú. **Exactitud: 88.35%** sobre el conjunto de prueba.

Las variables de mayor peso son el número de habitantes y el consumo en kWh, seguidas de
las horas de alto consumo y el uso en horario pico.

El archivo `modelo_energia.joblib` empaqueta todo lo necesario para usarlo fuera del notebook:

| Contenido | Para qué |
|---|---|
| `modelo` · `encoder` | Predicción |
| `benchmark_consumo_habitante` · `benchmark_por_tipo` | Comparaciones y estimación de ahorro |
| `distribucion_por_tipo` · `_pais` · `_localidad` | Contexto dentro del dataset |
| `equipos_promedio_tipo` · `tarifa_kwh` | Recomendaciones y costos |

Para regenerarlo, ejecutar el notebook de `data-science/` completo.

> **Al reentrenar:** las versiones de `ml-service/requirements.txt` deben coincidir con las
> usadas en el entrenamiento (hoy `scikit-learn==1.6.1`), o el modelo carga con advertencias
> de incompatibilidad.

---

## Base de datos

Tabla `registro_consumo`: guarda cada análisis realizado.

```sql
id · fecha_registro · pais · localidad · temperatura_ambiente
tipo_inmueble · numero_habitantes · cantidad_equipos
frecuencia_uso · horas_alto_consumo · uso_horario_pico
consumo_kwh · categoria · costo_estimado
```

El esquema se crea solo al levantar el contenedor de MySQL por primera vez
(`backend/init/01-schema.sql`).

---

## Pruebas

```bash
cd backend
mvn test
```

Sin Maven instalado, usando Docker:

```bash
docker run --rm -v "${PWD}/backend:/app" -w /app eclipse-temurin:25-jdk \
  sh -c "apt-get update -qq && apt-get install -y -qq maven >/dev/null 2>&1 && mvn -Dtest='AnalisisEnergetico*Test' -DfailIfNoSpecifiedTests=false test"
```

> `mvn test` completo incluye `BackendApplicationTests.contextLoads`, que necesita MySQL
> disponible. Fuera de Docker falla por eso; no es un error del código.

---

## Despliegue en OCI

La aplicación corre en una VM de Oracle Cloud con el mismo `docker compose`, en
**http://159.54.159.238/**

**Puertos a habilitar** en la Security List de la subred:

| Puerto | Uso |
|---|---|
| 80 | Acceso público a la aplicación |
| 22 | SSH (administración) |

Y en el firewall de la VM:

```bash
sudo iptables -I INPUT 6 -m state --state NEW -p tcp --dport 80 -j ACCEPT
sudo netfilter-persistent save
```

> **Cuidado con la memoria:** compilar el backend dentro de la VM (`docker compose up --build`)
> puede agotar la RAM en instancias pequeñas y dejar la máquina sin responder. Conviene
> añadir swap antes, o construir las imágenes fuera y desplegarlas ya compiladas.

---

## Equipo

| Integrante | Rol |
|---|---|
| Diana Lorena Gutiérrez Ortiz | Project Manager |
| Abel Leiva Cervantes | Frontend Developer |
| Héctor Alonso Esquivel Rodríguez | Backend Developer |
| Marco Muñoz | Backend Developer |
| Raúl Iván Ramírez Espinosa | Backend Developer |
| Catalina Muñoz | Data Scientist |
| Gabriel Espinoza | Data Scientist |
| Jair Cucho | Data Analyst |
