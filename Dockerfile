# BUILD STAGE
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Add dockerize
ENV DOCKERIZE_VERSION v0.6.1
RUN curl -O -L https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
   && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
   && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz


# PACKAGE STAGE
FROM openjdk:17
COPY --from=build /app/target/medilabo-0.0.1-SNAPSHOT.jar medilabo.jar
EXPOSE 8080

# Add the entry point
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]