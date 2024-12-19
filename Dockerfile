FROM openjdk:11-jdk-slim
COPY target/transfer-service-assessment.jar transfer-service-assessment.jar
ENTRYPOINT ["java", "-jar", "transfer-service-assessment.jar"]