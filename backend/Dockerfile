# Etapa 1: Compilación con JDK 25 y Maven
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copiar archivos para compilar
COPY pom.xml .
COPY src ./src

# Instalar Maven dentro del contenedor y construir el .jar
RUN apt-get update && apt-get install -y maven && mvn clean package -DskipTests

# Etapa 2: Imagen final ligera para ejecución
FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]