# BUILD STAGE
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# PACKAGE STAGE
FROM openjdk:17
COPY --from=build /app/target/front-0.0.1-SNAPSHOT.jar front.jar
EXPOSE 8080
CMD ["java","-jar","front.jar"]