FROM openjdk:8-jdk-slim
FROM maven:3.6.0-jdk-11-slim

EXPOSE 80
COPY shop-0.0.1-SNAPSHOT.jar /shop.jar

CMD ["java", "-jar", "/shop.jar"]