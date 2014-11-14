package org.whispersystems.websocket.resources;

import com.google.common.base.Optional;
import org.eclipse.jetty.websocket.api.CloseStatus;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.whispersystems.websocket.WebSocketResourceProvider;
import org.whispersystems.websocket.auth.AuthenticationException;
import org.whispersystems.websocket.auth.WebSocketAuthenticator;
import org.whispersystems.websocket.messages.protobuf.ProtobufWebSocketMessageFactory;
import org.whispersystems.websocket.setup.WebSocketConnectListener;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class WebSocketResourceProviderTest {

  @Test
  public void testOnConnect() throws AuthenticationException, IOException {
    HttpServlet                    contextHandler = mock(HttpServlet.class);
    WebSocketAuthenticator<String> authenticator  = mock(WebSocketAuthenticator.class);
    WebSocketResourceProvider provider       = new WebSocketResourceProvider(contextHandler,
                                                                             Optional.of((WebSocketAuthenticator)authenticator),
                                                                             new ProtobufWebSocketMessageFactory(),
                                                                             Optional.<WebSocketConnectListener>absent());

    Session        session = mock(Session.class       );
    UpgradeRequest request = mock(UpgradeRequest.class);

    when(session.getUpgradeRequest()).thenReturn(request);
    when(authenticator.authenticate(request)).thenReturn(Optional.of("fooz"));

    provider.onWebSocketConnect(session);

    verify(authenticator).authenticate(eq(request));

    verify(session, never()).close(anyInt(), anyString());
    verify(session, never()).close();
    verify(session, never()).close(any(CloseStatus.class));
  }

//  @Test
//  public void testOnConnectMissingCredentials() throws AuthenticationException, IOException {
//    HttpServlet                    servlet       = mock(HttpServlet.class           );
//    WebSocketAuthenticator<String> authenticator = mock(WebSocketAuthenticator.class);
//    WebSocketResourceProvider      provider      = new WebSocketResourceProvider(servlet, authenticator, new ProtobufWebSocketMessageFactory());
//
//    Session        session = mock(Session.class       );
//    UpgradeRequest request = mock(UpgradeRequest.class);
//
//    when(session.getUpgradeRequest()).thenReturn(request);
//    when(authenticator.authenticate(request)).thenReturn(Optional.<String>absent());
//
//    provider.onWebSocketConnect(session);
//
//    verify(authenticator).authenticate(eq(request));
//
//    verify(session).close(eq(4001), anyString());
//  }

  @Test
  public void testRouteMessage() throws Exception {
    HttpServlet                    servlet       = mock(HttpServlet.class           );
    WebSocketAuthenticator<String> authenticator = mock(WebSocketAuthenticator.class);
    WebSocketResourceProvider      provider      = new WebSocketResourceProvider(servlet, Optional.of((WebSocketAuthenticator)authenticator), new ProtobufWebSocketMessageFactory(), Optional.<WebSocketConnectListener>absent());

    Session        session        = mock(Session.class       );
    RemoteEndpoint remoteEndpoint = mock(RemoteEndpoint.class);
    UpgradeRequest request        = mock(UpgradeRequest.class);

    when(session.getUpgradeRequest()).thenReturn(request);
    when(session.getRemote()).thenReturn(remoteEndpoint);
    when(authenticator.authenticate(request)).thenReturn(Optional.of("foo"));

    provider.onWebSocketConnect(session);

    verify(session, never()).close(anyInt(), anyString());
    verify(session, never()).close();
    verify(session, never()).close(any(CloseStatus.class));

    byte[] message = new ProtobufWebSocketMessageFactory().createRequest(Optional.of(111L), "GET", "/bar", Optional.of("hello world!".getBytes())).toByteArray();

    provider.onWebSocketBinary(message, 0, message.length);

    ArgumentCaptor<HttpServletRequest> requestCaptor = ArgumentCaptor.forClass(HttpServletRequest.class);

    verify(servlet).service(requestCaptor.capture(), any(HttpServletResponse.class));

    HttpServletRequest bundledRequest = requestCaptor.getValue();

    byte[] expected = new byte[bundledRequest.getInputStream().available()];
    int    read     = bundledRequest.getInputStream().read(expected);

    assertThat(read).isEqualTo(expected.length);
    assertThat(new String(expected)).isEqualTo("hello world!");
  }

}
