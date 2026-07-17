/*
=========================================================
 Empresa      : EnergiAI Solutions S.A.S.
 Sistema      : Plataforma Inteligente de Gestión Energética
 Base de Datos: energia_inteligente
 Versión      : 1.0
 Fecha        : 2026-07-16

 Descripción:
 Base de datos para registrar consumos energéticos de
 viviendas y pequeños establecimientos con fines de
 análisis de eficiencia energética.
=========================================================
*/

CREATE DATABASE IF NOT EXISTS energia_inteligente;
USE energia_inteligente;

DROP TABLE IF EXISTS registro_consumo;

CREATE TABLE registro_consumo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fecha_registro DATE NOT NULL,
    tipo_inmueble ENUM('Casa','Apartamento','Local') NOT NULL,
    consumo_kwh DECIMAL(8,2) NOT NULL,
    uso_horario_pico BOOLEAN NOT NULL,
    cantidad_equipos INT NOT NULL,
    frecuencia_uso ENUM('Ocasional','Frecuente','Fijo') NOT NULL,
    horas_alto_consumo INT NOT NULL,
    categoria ENUM('Eficiente','Moderado','Ineficiente') NOT NULL
);

INSERT INTO registro_consumo
(fecha_registro,tipo_inmueble,consumo_kwh,uso_horario_pico,cantidad_equipos,frecuencia_uso,horas_alto_consumo,categoria)
VALUES
('2026-01-08','Casa',118,FALSE,4,'Ocasional',3,'Eficiente'),
('2026-01-21','Apartamento',135,FALSE,5,'Ocasional',4,'Eficiente'),
('2026-02-05','Casa',158,FALSE,5,'Frecuente',5,'Eficiente'),
('2026-02-18','Casa',176,FALSE,6,'Frecuente',5,'Eficiente'),
('2026-03-03','Apartamento',194,FALSE,6,'Frecuente',6,'Eficiente'),
('2026-03-20','Casa',212,FALSE,7,'Frecuente',6,'Eficiente'),
('2026-04-10','Local',235,FALSE,7,'Frecuente',6,'Eficiente'),
('2026-04-25','Casa',265,TRUE,8,'Frecuente',7,'Moderado'),
('2026-05-07','Apartamento',288,TRUE,8,'Fijo',8,'Moderado'),
('2026-05-22','Casa',315,TRUE,9,'Fijo',8,'Moderado'),
('2026-06-04','Local',338,TRUE,9,'Fijo',8,'Moderado'),
('2026-06-19','Casa',362,TRUE,10,'Fijo',9,'Moderado'),
('2026-07-02','Apartamento',389,TRUE,10,'Fijo',9,'Moderado'),
('2026-07-15','Casa',425,TRUE,10,'Fijo',9,'Ineficiente'),
('2026-08-01','Casa',462,TRUE,11,'Fijo',10,'Ineficiente'),
('2026-08-18','Local',505,TRUE,12,'Fijo',10,'Ineficiente'),
('2026-09-05','Casa',560,TRUE,13,'Fijo',11,'Ineficiente'),
('2026-09-20','Local',625,TRUE,15,'Fijo',12,'Ineficiente'),
('2026-10-08','Local',702,TRUE,17,'Fijo',13,'Ineficiente'),
('2026-10-26','Local',815,TRUE,20,'Fijo',14,'Ineficiente');

SELECT * FROM registro_consumo;
