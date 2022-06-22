FROM openjdk:17-jdk-slim
FROM maven:3.6.0-jdk-11-slim

CMD ["mvn", "package"]

EXPOSE 80
COPY shop-0.0.1-SNAPSHOT.jar /shop.jar

CMD ["java", "-jar", "/shop.jar"]