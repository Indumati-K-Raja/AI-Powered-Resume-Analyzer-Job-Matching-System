# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY resumeanalyzer-backend/pom.xml .
COPY resumeanalyzer-backend/src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-focal
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Force production profile
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-jar", "app.jar"]
