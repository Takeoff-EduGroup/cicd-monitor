FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY deploy.sh /app/deploy.sh
RUN chmod +x /app/deploy.sh
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]