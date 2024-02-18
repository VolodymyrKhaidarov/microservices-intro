FROM  maven:3.9.6-eclipse-temurin-17-alpine AS maven
WORKDIR /storage-service
COPY pom.xml .
COPY src ./src
RUN mvn clean package
FROM openjdk:17-jdk-alpine
WORKDIR /storage-service
COPY --from=maven /storage-service/target/storage-service-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "storage-service-0.0.1-SNAPSHOT.jar"]