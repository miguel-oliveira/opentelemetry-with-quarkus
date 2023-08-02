package miguel.quarkus.opentelemetry;

import io.opentelemetry.contrib.awsxray.AwsXrayIdGenerator;
import io.opentelemetry.sdk.trace.IdGenerator;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;


@Singleton
public class AwsOpenTelemetryIdGeneratorProducer {

  @Produces
  @Singleton
  public IdGenerator idGenerator() {
    return AwsXrayIdGenerator.getInstance();
  }
}