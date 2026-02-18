FROM maven:3.8.7-eclipse-temurin-8-alpine as build

WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY src src

RUN mvn clean package -DskipTests \
    -Dmaven.wagon.http.ssl.insecure=true \
    -Dmaven.wagon.http.ssl.allowall=true \
    -Dmaven.wagon.http.ssl.ignore.validity.dates=true

FROM eclipse-temurin:8-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]