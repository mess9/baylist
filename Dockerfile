FROM openjdk:21-jre-slim
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
LABEL authors="mess9"