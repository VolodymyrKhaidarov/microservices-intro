FROM  maven:3.9.6-eclipse-temurin-17-alpine AS maven
WORKDIR /resource-service
COPY pom.xml .
COPY src ./src
RUN mvn clean package
FROM openjdk:17-jdk-alpine
WORKDIR /resource-service
COPY --from=maven /resource-service/target/resource-service-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "resource-service-0.0.1-SNAPSHOT.jar"]