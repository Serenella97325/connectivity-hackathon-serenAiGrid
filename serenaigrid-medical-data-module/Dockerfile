# Usa l'immagine base di OpenJDK
FROM openjdk:17-jdk-slim

# Imposta la directory di lavoro all'interno del container
WORKDIR /app

# Copia il file JAR dell'applicazione nella directory di lavoro
COPY target/serenaigrid-medical-data-module-0.0.1-SNAPSHOT.jar serenaigrid-medical-data-module.jar

# Esponi la porta su cui il modulo Spring Boot sarà in ascolto
EXPOSE 8080

# Esegui il file JAR
ENTRYPOINT ["java", "-jar", "serenaigrid-medical-data-module.jar"]
