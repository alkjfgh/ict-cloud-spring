# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the application's jar to the container
COPY build/libs/ict-cloud-spring-0.0.1-SNAPSHOT.jar /app/app.jar
COPY src/main/webapp/WEB-INF/ /app/WEB-INF/

# Make port 443 available to the world outside this container
EXPOSE 443

# Run the jar file
ENTRYPOINT ["java","-jar","/app/app.jar"]
