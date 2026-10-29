# Build the Spring Boot jar
FROM eclipse-temurin:27-jdk AS build
WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY src src

RUN chmod +x mvnw \
  && ./mvnw -DskipTests package \
  && mv target/todoapp-0.0.1-SNAPSHOT.jar /app/app.jar

# Run with a smaller JRE image
FROM eclipse-temurin:27-jre
WORKDIR /app

COPY --from=build /app/app.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
