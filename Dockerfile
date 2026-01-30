FROM ubuntu:latest
LABEL authors="baranasubasinghe"

ENTRYPOINT ["top", "-b"]

# 1. Use Java 21 (matching pom.xml)
FROM eclipse-temurin:21-jdk-alpine

# 2. Set the working directory
WORKDIR /app

# 3. Copy the project files
COPY . .

# 4. Give permission to run Maven
RUN chmod +x mvnw

# 5. Build the application
RUN ./mvnw clean package -DskipTests

# 6. Run the jar file
# my pom.xml says artifactId is 'portal', so the jar is 'portal-...'
CMD ["java", "-jar", "target/portal-0.0.1-SNAPSHOT.jar"]