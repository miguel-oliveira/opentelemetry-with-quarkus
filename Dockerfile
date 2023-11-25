FROM openjdk:17-oracle

COPY ./target .

ENTRYPOINT ["java", "-jar", "quarkus-app/quarkus-run.jar"]