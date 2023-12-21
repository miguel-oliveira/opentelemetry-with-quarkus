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
public @interface WithSpan {

  @Nonbinding String name() default "";

  @Nonbinding String outputAttributeName() default "output_value";

  @Nonbinding SpanKind kind() default SpanKind.INTERNAL;
}
