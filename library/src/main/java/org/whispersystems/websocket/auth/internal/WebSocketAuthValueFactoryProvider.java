package org.whispersystems.websocket.auth.internal;

import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.internal.inject.AbstractContainerRequestValueFactory;
import org.glassfish.jersey.server.internal.inject.AbstractValueFactoryProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;
import org.whispersystems.websocket.servlet.WebSocketServletRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.security.Principal;

import io.dropwizard.auth.Auth;

@Singleton
public class WebSocketAuthValueFactoryProvider extends AbstractValueFactoryProvider {

  @Inject
  public WebSocketAuthValueFactoryProvider(MultivaluedParameterExtractorProvider mpep,
                                           ServiceLocator injector)
  {
    super(mpep, injector, Parameter.Source.UNKNOWN);
  }

  @Override
  public AbstractContainerRequestValueFactory<?> createValueFactory(final Parameter parameter) {
    if (parameter.getAnnotation(Auth.class) == null) {
      return null;
    }

    return new AbstractContainerRequestValueFactory() {
      public Object provide() {
        Principal principal = getContainerRequest().getSecurityContext().getUserPrincipal();

        if (principal == null) {
          throw new IllegalStateException("Cannot inject a custom principal into unauthenticated request");
        }

        if (!(principal instanceof WebSocketServletRequest.ContextPrincipal)) {
          throw new IllegalArgumentException("Cannot inject a non-WebSocket AuthPrincipal into request");
        }

        return ((WebSocketServletRequest.ContextPrincipal)principal).getContext().getAuthenticated(parameter.getRawType());
      }
    };
  }

  @Singleton
  private static class AuthInjectionResolver extends ParamInjectionResolver<Auth> {
    public AuthInjectionResolver() {
      super(WebSocketAuthValueFactoryProvider.class);
    }
  }

  public static class Binder extends AbstractBinder {


    public Binder() {
    }

    @Override
    protected void configure() {
      bind(WebSocketAuthValueFactoryProvider.class).to(ValueFactoryProvider.class).in(Singleton.class);
      bind(AuthInjectionResolver.class).to(new TypeLiteral<InjectionResolver<Auth>>() {
      }).in(Singleton.class);
    }
  }
}