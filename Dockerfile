FROM maven:3.8-openjdk-8-slim AS build

WORKDIR /app
COPY pom.xml .
COPY src src

RUN mvn clean package
FROM eclipse-temurin:8-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]