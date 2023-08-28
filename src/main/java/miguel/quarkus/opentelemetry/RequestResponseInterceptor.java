package miguel.quarkus.opentelemetry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@Provider
@RequiredArgsConstructor
public class RequestResponseInterceptor implements ContainerRequestFilter, ContainerResponseFilter {

  private static final String HTTP_REQUEST_HEADERS = "http.request.headers";
  private static final String HTTP_REQUEST_BODY = "http.request.body";
  private static final String HTTP_RESPONSE_HEADERS = "http.response.headers";
  private static final String HTTP_RESPONSE_BODY = "http.response.body";
  private static final String TRACE_ID_RESPONSE_HEADER = "Trace-Id";

  private final Span span;

  private final ObjectMapper objectMapper;

  @Override
  public void filter(final ContainerRequestContext requestContext) throws IOException {

    exportHeaders(HTTP_REQUEST_HEADERS, requestContext.getHeaders());

    if (requestContext.hasEntity()) {
      exportRequestBody(requestContext);
    }
  }

  private void exportHeaders(final String tagName, final MultivaluedMap<String, String> headers) {
    Optional.ofNullable(headers)
        .map(this::headersToString)
        .ifPresent(headersString -> span.setAttribute(tagName, headersString));
  }

  private String headersToString(final MultivaluedMap<String, String> headers) {
    return headers
        .entrySet()
        .stream()
        .map(entry -> entry.getKey() + "=" + entry.getValue())
        .collect(Collectors.joining(","));
  }

  private void exportRequestBody(final ContainerRequestContext requestContext) throws IOException {
    try (final InputStream inputStream = requestContext.getEntityStream()) {
      final byte[] bytes = inputStream.readAllBytes();
      requestContext.setEntityStream(new ByteArrayInputStream(bytes));
      span.setAttribute(HTTP_REQUEST_BODY, new String(bytes, StandardCharsets.UTF_8));
    }
  }

  @Override
  public void filter(
      final ContainerRequestContext requestContext,
      final ContainerResponseContext responseContext
  ) throws JsonProcessingException {

    addTraceIdToResponseHeaders(responseContext);

    exportHeaders(HTTP_RESPONSE_HEADERS, responseContext.getStringHeaders());

    if (responseContext.hasEntity()) {
      exportResponseBody(responseContext);
    }
  }

  private void exportResponseBody(
      final ContainerResponseContext responseContext
  ) throws JsonProcessingException {
    final Object responseBody = responseContext.getEntity();
    span.setAttribute(HTTP_RESPONSE_BODY, objectMapper.writeValueAsString(responseBody));
  }

  private void addTraceIdToResponseHeaders(final ContainerResponseContext responseContext) {
    Optional.ofNullable(responseContext.getHeaders())
        .ifPresent(headers -> headers.putSingle(TRACE_ID_RESPONSE_HEADER, getTraceId()));
  }

  private String getTraceId() {
    return Optional.ofNullable(span)
        .map(Span::getSpanContext)
        .map(SpanContext::getTraceId)
        .orElse("");
  }

}
