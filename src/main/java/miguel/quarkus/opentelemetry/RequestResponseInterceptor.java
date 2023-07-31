package miguel.quarkus.opentelemetry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.api.trace.Span;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Provider
public class RequestResponseInterceptor implements ContainerRequestFilter, ContainerResponseFilter {

  private static final String HTTP_REQUEST_BODY = "http.request.body";
  private static final String HTTP_RESPONSE_BODY = "http.response.body";

  private final boolean exportRequestBody;

  private final boolean exportResponseBody;

  private final Span span;

  private final ObjectMapper objectMapper;

  public RequestResponseInterceptor(
      @ConfigProperty(name = "quarkus.otel.export.http-request-body", defaultValue = "false") final boolean exportRequestBody,
      @ConfigProperty(name = "quarkus.otel.export.http-response-body", defaultValue = "false") final boolean exportResponseBody,
      final Span span,
      final ObjectMapper objectMapper) {
    this.exportRequestBody = exportRequestBody;
    this.exportResponseBody = exportResponseBody;
    this.span = span;
    this.objectMapper = objectMapper;
  }

  @Override
  public void filter(final ContainerRequestContext requestContext) throws IOException {
    if (exportRequestBody) {
      exportRequestBody(requestContext);
    }
  }

  private void exportRequestBody(final ContainerRequestContext requestContext) throws IOException {
    final InputStream requestBody = requestContext.getEntityStream();
    if (requestBody != null && requestBody.available() > 0) {
      span.setAttribute(
          HTTP_REQUEST_BODY,
          objectMapper.readValue(requestBody, String.class)
      );
    }
  }

  @Override
  public void filter(final ContainerRequestContext requestContext,
      final ContainerResponseContext responseContext) throws IOException {
    if (exportResponseBody) {
      exportResponseBody(responseContext);
    }
  }

  private void exportResponseBody(final ContainerResponseContext responseContext)
      throws JsonProcessingException {
    final Object responseBody = responseContext.getEntity();
    if (responseBody != null) {
      span.setAttribute(
          HTTP_RESPONSE_BODY,
          objectMapper.writeValueAsString(responseContext.getEntity())
      );
    }
  }
}
