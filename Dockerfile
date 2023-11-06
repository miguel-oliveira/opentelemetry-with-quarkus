FROM openjdk:17-oracle

COPY . .

RUN ./mvnw clean package

ENTRYPOINT ["java", "-jar", "target/quarkus-app/quarkus-run.jar"]