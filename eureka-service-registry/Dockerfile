FROM  maven:3.9.6-eclipse-temurin-17-alpine AS maven
WORKDIR /eureka-service-registry
COPY pom.xml .
COPY src ./src
RUN mvn clean package
FROM openjdk:17-jdk-alpine
WORKDIR /eureka-service-registry
COPY --from=maven /eureka-service-registry/target/eureka-service-registry-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "eureka-service-registry-0.0.1-SNAPSHOT.jar"]