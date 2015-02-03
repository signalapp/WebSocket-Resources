package org.whispersystems.websocket;


import com.google.common.base.Optional;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.junit.Test;
import org.whispersystems.websocket.auth.AuthenticationException;
import org.whispersystems.websocket.auth.WebSocketAuthenticator;
import org.whispersystems.websocket.setup.WebSocketEnvironment;

import javax.servlet.ServletException;

import java.io.IOException;

import io.dropwizard.jersey.setup.JerseyEnvironment;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class WebSocketResourceProviderFactoryTest {

  @Test
  public void testUnauthorized() throws ServletException, AuthenticationException, IOException {
    JerseyEnvironment      jerseyEnvironment = mock(JerseyEnvironment.class     );
    WebSocketEnvironment   environment       = mock(WebSocketEnvironment.class  );
    WebSocketAuthenticator authenticator     = mock(WebSocketAuthenticator.class);
    UpgradeRequest         request           = mock(UpgradeRequest.class        );
    UpgradeResponse        response          = mock(UpgradeResponse.class       );

    when(environment.getAuthenticator()).thenReturn(authenticator);
    when(authenticator.authenticate(eq(request))).thenReturn(Optional.absent());
    when(environment.jersey()).thenReturn(jerseyEnvironment);

    WebSocketResourceProviderFactory factory    = new WebSocketResourceProviderFactory(environment);
    Object                           connection = factory.createWebSocket(request, response);

    assertNull(connection);
    verify(response).sendForbidden(eq("Unauthorized"));
    verify(authenticator).authenticate(eq(request));
  }

  @Test
  public void testNoAuthorization() throws ServletException, AuthenticationException {
    JerseyEnvironment    jerseyEnvironment = mock(JerseyEnvironment.class   );
    WebSocketEnvironment environment       = mock(WebSocketEnvironment.class);
    UpgradeRequest       request           = mock(UpgradeRequest.class      );
    UpgradeResponse      response          = mock(UpgradeResponse.class     );
    Session              session           = mock(Session.class             );

    when(environment.getAuthenticator()).thenReturn(null);
    when(environment.jersey()).thenReturn(jerseyEnvironment);

    WebSocketResourceProviderFactory factory    = new WebSocketResourceProviderFactory(environment);
    Object                           connection = factory.createWebSocket(request, response);

    assertNotNull(connection);
    verifyNoMoreInteractions(response);

    ((WebSocketResourceProvider)connection).onWebSocketConnect(session);

    assertFalse(((WebSocketResourceProvider) connection).getContext().getAuthenticated(Object.class).isPresent());
  }

  @Test
  public void testValidAuthorization() throws AuthenticationException, ServletException {
    JerseyEnvironment      jerseyEnvironment = mock(JerseyEnvironment.class     );
    WebSocketEnvironment   environment       = mock(WebSocketEnvironment.class  );
    WebSocketAuthenticator authenticator     = mock(WebSocketAuthenticator.class);
    UpgradeRequest         request           = mock(UpgradeRequest.class        );
    UpgradeResponse        response          = mock(UpgradeResponse.class       );
    Session                session           = mock(Session.class               );
    Account                account           = new Account();

    when(environment.getAuthenticator()).thenReturn(authenticator);
    when(authenticator.authenticate(eq(request))).thenReturn(Optional.of(account));
    when(environment.jersey()).thenReturn(jerseyEnvironment);

    WebSocketResourceProviderFactory factory    = new WebSocketResourceProviderFactory(environment);
    Object                           connection = factory.createWebSocket(request, response);

    assertNotNull(connection);
    verifyNoMoreInteractions(response);
    verify(authenticator).authenticate(eq(request));

    ((WebSocketResourceProvider)connection).onWebSocketConnect(session);

    assertTrue(((WebSocketResourceProvider) connection).getContext().getAuthenticated(Account.class).isPresent());
    assertEquals(((WebSocketResourceProvider)connection).getContext().getAuthenticated(Account.class).get(), account);
  }

  private static class Account {}


}
