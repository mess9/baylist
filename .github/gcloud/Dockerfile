FROM maven:3.9.9-amazoncorretto-21-alpine AS build-env
WORKDIR /app
COPY ./pom.xml ./
COPY ./src ./src
RUN mvn package -DskipTests

FROM openjdk:21-slim
COPY --from=build-env /app/target/*.jar /app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["java", "-jar", "/app.jar"]
LABEL authors="mess9"