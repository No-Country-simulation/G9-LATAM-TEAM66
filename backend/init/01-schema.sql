/*
 =========================================================
 Empresa      : EnergiAI Solutions S.A.S.
 Sistema      : Plataforma Inteligente de Gestión Energética
 Base de Datos: energia_inteligente
 Versión      : 1.1
 Fecha        : 2026-08-11
 
 Descripción:
 Base de datos para registrar consumos energéticos de
 viviendas y pequeños establecimientos con fines de
 análisis de eficiencia energética.
 =========================================================
 */
CREATE DATABASE IF NOT EXISTS energia_inteligente;
USE energia_inteligente;
CREATE TABLE IF NOT EXISTS registro_consumo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_registro DATE NOT NULL,
    pais VARCHAR(25),
    localidad VARCHAR(30),
    temperatura_ambiente INT,
    tipo_inmueble ENUM('Casa', 'Departamento', 'Local') NOT NULL,
    numero_habitantes INT,
    cantidad_equipos INT NOT NULL,
    frecuencia_uso ENUM('Alta', 'Media', 'Baja') NOT NULL,
    horas_alto_consumo INT NOT NULL,
    uso_horario_pico BOOLEAN NOT NULL,
    consumo_kwh DECIMAL(8, 2) NOT NULL,
    categoria ENUM('Eficiente', 'Moderado', 'Ineficiente') NOT NULL,
    costo_estimado DECIMAL(12, 2)
);
/*-- Habilitar la importación local
 SET GLOBAL local_infile = 1;
 -- Importar los 10,000 registros omitiendo la primera fila de encabezados
 LOAD DATA INFILE '/docker-entrypoint-initdb.d/datos.csv' INTO TABLE registro_consumo FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES (
 fecha_registro,
 pais,
 localidad,
 temperatura_ambiente,
 tipo_inmueble,
 numero_habitantes,
 cantidad_equipos,
 frecuencia_uso,
 horas_alto_consumo,
 uso_horario_pico,
 consumo_kwh,
 categoria,
 costo_estimado
 );*/