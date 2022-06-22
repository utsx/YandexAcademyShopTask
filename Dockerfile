FROM openjdk:17-jdk-slim
FROM maven:3.6.0-jdk-11-slim

RUN ./mvnw package -DskipTests

EXPOSE 80
COPY target/shop-0.0.1-SNAPSHOT.jar /shop.jar

CMD ["java", "-jar", "/shop.jar"]