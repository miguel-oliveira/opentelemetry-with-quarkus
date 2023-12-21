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
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.AllArgsConstructor;

@Interceptor
@WithSpan
@AllArgsConstructor
public class WithSpanInterceptor {

  private final Tracer tracer;
  private final ObjectMapper objectMapper;

  @AroundInvoke
  public Object addSpan(final InvocationContext context) throws Exception {
    final Map<String, String> parameterMap = buildParameterMap(context);
    final Object result = context.proceed();
    addSpan(context, parameterMap, result);
    return result;
  }

  private Map<String, String> buildParameterMap(final InvocationContext context)
      throws JsonProcessingException {

    final Map<String, String> parameterMap = new LinkedHashMap<>();
    final Parameter[] parameters = context.getMethod().getParameters();
    final Object[] parameterValues = context.getParameters();

    for (int i = 0; i < parameters.length; i++) {
      parameterMap.put(
          parameters[i].getName(),
          objectMapper.writeValueAsString(parameterValues[i])
      );
    }

    return parameterMap;
  }

  @SuppressWarnings("unused")
  private void addSpan(
      final InvocationContext context,
      final Map<String, String> parameters,
      final Object result
  ) throws JsonProcessingException {

    final WithSpan annotation = context.getMethod().getAnnotation(WithSpan.class);
    final Span span = buildSpan(annotation.name(), annotation.kind());

    try (final Scope scope = span.makeCurrent()) {
      parameters.forEach(span::setAttribute);
      span.setAttribute(
          annotation.outputAttributeName(),
          objectMapper.writeValueAsString(result)
      );
    } finally {
      span.end();
    }

  }

  private Span buildSpan(final String name, final SpanKind kind) {
    final SpanBuilder spanBuilder = tracer.spanBuilder(name);
    spanBuilder.setParent(Context.current());
    spanBuilder.setSpanKind(kind);
    return spanBuilder.startSpan();
  }

}
