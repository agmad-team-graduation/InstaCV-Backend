# Use Maven to build the project
FROM maven:3.8.4-openjdk-21-slim AS build

WORKDIR /app

# Copy the source code and pom.xml
COPY . .

# Build the application (this will create the target/*.jar file)
RUN mvn clean package -DskipTests

# Use the official OpenJDK 21 image for the final stage
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app.jar"]
