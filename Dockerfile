FROM openjdk:17-oracle

ARG APPLICATION_NAME

# Application name ENV variable is only applied at build time,
# any change to this variable during container configuration will always be ignored
ENV APPLICATION_NAME=${APPLICATION_NAME}

COPY . .

RUN ./mvnw clean package

ENTRYPOINT ["java", "-jar", "target/quarkus-app/quarkus-run.jar"]