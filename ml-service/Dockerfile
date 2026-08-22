FROM python:3.12-slim

WORKDIR /app

# Instalar dependencias primero para aprovechar la cache de Docker
COPY ml-service/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Modelo entrenado por Data Science (vive en la raiz del repositorio)
COPY modelo_energia.joblib .

# Servicio
COPY ml-service/app.py .

EXPOSE 8000

CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8000"]
