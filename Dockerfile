# 1️) BUILD STAGE
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom first (better caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build jar
RUN mvn clean package -DskipTests



# 2️) RUN STAGE
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose backend port
EXPOSE 5000

# Run with docker profile
ENTRYPOINT ["java","-jar","/app/app.jar","--spring.profiles.active=docker"]