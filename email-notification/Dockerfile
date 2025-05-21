FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY ../. .
RUN chmod +x mvnw
RUN ./mvnw clean install -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/email-notification/target/email-notification.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]