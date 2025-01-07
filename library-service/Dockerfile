FROM maven:3.9.8-amazoncorretto-21-al2023 AS build

WORKDIR /build

COPY pom.xml .
COPY library-service/pom.xml library-service/pom.xml

COPY library-service/src library-service/src
COPY library-service/wiremock library-service/wiremock

RUN mvn clean install -DskipTests -Dcheckstyle.skip

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /build/library-service/target/*.jar library-service.jar

EXPOSE 8080 8443

CMD ["java", "-jar", "library-service.jar"]


