FROM openjdk:17-jdk-slim
FROM postgres:14.3

CMD ["mvn", "clean"]
CMD ["mvn", "package"]

COPY target/shop-0.0.1-SNAPSHOT.jar /shop.jar

CMD ["java", "-jar", "/shop.jar"]