FROM  maven:3.9.6-eclipse-temurin-17-alpine AS maven
WORKDIR /gateway-service
COPY pom.xml .
COPY src ./src
RUN mvn clean package
FROM openjdk:17-jdk-alpine
WORKDIR /gateway-service
COPY --from=maven /gateway-service/target/gateway-service-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "gateway-service-0.0.1-SNAPSHOT.jar"]