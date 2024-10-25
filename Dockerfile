FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/LibraryWithSecurity-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080 8443

CMD ["java", "-jar", "app.jar"]
