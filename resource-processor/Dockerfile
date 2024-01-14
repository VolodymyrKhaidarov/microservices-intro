FROM  maven:3.9.6-eclipse-temurin-17-alpine AS maven
WORKDIR /resource-processor
COPY pom.xml .
COPY src ./src
RUN mvn clean package
FROM openjdk:17-jdk-alpine
WORKDIR /resource-processor
COPY --from=maven /resource-processor/target/resource-processor-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "resource-processor-0.0.1-SNAPSHOT.jar"]