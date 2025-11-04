FROM openjdk:21-jdk-slim
LABEL authors="Roberto Sodini"

COPY target/authify-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

