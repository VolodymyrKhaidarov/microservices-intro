FROM  maven:3.9.6-eclipse-temurin-17-alpine AS maven
WORKDIR /song-service
COPY pom.xml .
COPY src ./src
RUN mvn clean package
FROM openjdk:17-jdk-alpine
WORKDIR /song-service
COPY --from=maven /song-service/target/song-service-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "song-service-0.0.1-SNAPSHOT.jar"]