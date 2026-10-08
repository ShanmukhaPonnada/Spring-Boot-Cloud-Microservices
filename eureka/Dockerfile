FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set working directory inside the container
WORKDIR /app

# Copy pom.xml and download dependencies first (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of your source code
COPY src ./src

# Build the JAR file
RUN mvn clean package -DskipTests

# ==========================
# Stage 2 — Run the app
# ==========================
FROM eclipse-temurin:21-jre-jammy

# Set working directory
WORKDIR /app

# Copy the built JAR from previous stage
COPY --from=build /app/target/*.jar app.jar


# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]