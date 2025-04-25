# Use the official OpenJDK 21 image
FROM openjdk:21-jdk-slim

# Add a label (optional)
LABEL maintainer="osama392maher@gmail.com"

# Copy the built JAR file to the container
COPY target/*.jar app.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app.jar"]
