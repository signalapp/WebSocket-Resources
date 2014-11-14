package org.whispersystems.websocket.auth;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
public @interface WebSocketAuth {
  /**
   * If {@code true}, the request will not be processed in the absence of a valid principal. If
   * {@code false}, {@code null} will be passed in as a principal. Defaults to {@code true}.
   */
  boolean required() default true;
}
