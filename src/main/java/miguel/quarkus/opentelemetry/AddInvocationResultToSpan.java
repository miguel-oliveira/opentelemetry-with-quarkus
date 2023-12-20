package miguel.quarkus.opentelemetry;

import io.opentelemetry.api.trace.SpanKind;
import jakarta.enterprise.util.Nonbinding;
import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AddInvocationResultToSpan {

  @Nonbinding String spanName();

  @Nonbinding String spanAttributeName();

  @Nonbinding SpanKind spanKind();
}
