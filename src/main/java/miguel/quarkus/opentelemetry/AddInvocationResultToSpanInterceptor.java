package miguel.quarkus.opentelemetry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import lombok.AllArgsConstructor;

@Interceptor
@AddInvocationResultToSpan(spanName = "default", spanAttributeName = "default", spanKind = SpanKind.INTERNAL)
@AllArgsConstructor
public class AddInvocationResultToSpanInterceptor {

  private final Tracer tracer;
  private final ObjectMapper objectMapper;

  @AroundInvoke
  public Object addInvocationResultToSpan(final InvocationContext context) throws Exception {
    final Object invocationResult = context.proceed();
    final AddInvocationResultToSpan annotation =
        context.getMethod().getAnnotation(AddInvocationResultToSpan.class);
    addInvocationResultToSpan(annotation, invocationResult);
    return invocationResult;
  }

  @SuppressWarnings("unused")
  private void addInvocationResultToSpan(
      final AddInvocationResultToSpan annotation,
      final Object attribute
  ) throws JsonProcessingException {
    final Span span = buildSpan(annotation);
    try (final Scope scope = span.makeCurrent()) {
      span.setAttribute(annotation.spanAttributeName(), objectMapper.writeValueAsString(attribute));
    } finally {
      span.end();
    }
  }

  private Span buildSpan(final AddInvocationResultToSpan annotation) {
    final SpanBuilder spanBuilder = tracer.spanBuilder(annotation.spanName());
    spanBuilder.setParent(Context.current());
    spanBuilder.setSpanKind(annotation.spanKind());
    return spanBuilder.startSpan();
  }

}
