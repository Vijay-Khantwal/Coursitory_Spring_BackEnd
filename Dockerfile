# Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project files to the container
COPY pom.xml .

# Download all dependencies to the local repository
RUN mvn dependency:go-offline -B

# Copy the source code to the container
COPY src ./src

# Build the Spring Boot application
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/spring-coursitory-backend-0.0.1-SNAPSHOT.jar .

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/spring-coursitory-backend-0.0.1-SNAPSHOT.jar"]
