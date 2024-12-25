FROM eclipse-temurin:23.0.1_11-jdk-ubi9-minimal
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]