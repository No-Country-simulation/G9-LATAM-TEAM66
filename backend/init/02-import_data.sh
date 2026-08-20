#!/bin/bash
set -e

echo "=========================================="
echo ">> Iniciando carga masiva de datos.csv..."
echo "=========================================="

mysql -u root -p"root" --local-infile=1 "energia_inteligente" <<EOSQL
SET GLOBAL local_infile = 1;

LOAD DATA LOCAL INFILE '/docker-entrypoint-initdb.d/datos.csv'
INTO TABLE registro_consumo
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(fecha_registro, pais, localidad, temperatura_ambiente, tipo_inmueble, numero_habitantes, cantidad_equipos, frecuencia_uso, horas_alto_consumo, uso_horario_pico, consumo_kwh, categoria, costo_estimado);
EOSQL

echo "=========================================="
echo ">> Carga de 10,000 registros finalizada."
echo "=========================================="