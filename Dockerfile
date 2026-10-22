FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn package -DskipTests

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]