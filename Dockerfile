# Step 1: Build the Spring Boot app
FROM maven:3.9.8-eclipse-temurin-23 AS build
WORKDIR /app

# Copy Maven project files
COPY pom.xml .
COPY src ./src

# Build the jar
RUN mvn clean package -DskipTests

# Step 2: Run the Spring Boot app
FROM eclipse-temurin:23-jdk
WORKDIR /app

# Copy only the built jar from the build stage
COPY --from=build /app/target/*SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
