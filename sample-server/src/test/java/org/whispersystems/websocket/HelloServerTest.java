package org.whispersystems.websocket;

import org.eclipse.jetty.websocket.api.UpgradeException;
import org.junit.ClassRule;
import org.junit.Test;
import org.whispersystems.websocket.messages.WebSocketResponseMessage;
import org.whispersystems.websocket.sample.Server;
import org.whispersystems.websocket.sample.ServerConfiguration;

import java.net.URI;
import java.util.concurrent.ExecutionException;

import io.dropwizard.testing.junit.DropwizardAppRule;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class HelloServerTest {

  @ClassRule
  public static final DropwizardAppRule<ServerConfiguration> RULE = new DropwizardAppRule<>(Server.class, new ServerConfiguration());

  @Test
  public void testAuthenticatedQueries() throws Exception {
    SynchronousClient client = new SynchronousClient(new URI("ws://localhost:" + RULE.getLocalPort() + "/websocket/?login=moxie&password=insecure"));
    client.waitForConnected(5000);

    long                     requestId = client.sendRequest("GET", "/hello/named");
    WebSocketResponseMessage response  = client.readResponse(requestId, 5000);

    assertEquals(response.getStatus(), 200);
    assertTrue(response.getBody().isPresent());
    assertEquals(new String(response.getBody().get()), "Hello moxie");

    long                     optionalRequestId = client.sendRequest("GET", "/hello/optional");
    WebSocketResponseMessage optionalResponse  = client.readResponse(optionalRequestId, 5000);

    assertEquals(200, optionalResponse.getStatus());
    assertTrue(optionalResponse.getBody().isPresent());
    assertEquals(new String(optionalResponse.getBody().get()), "moxie");
  }

  @Test
  public void testBadAuthentication() throws Exception {
    try {
      SynchronousClient client = new SynchronousClient(new URI("ws://localhost:" + RULE.getLocalPort() + "/websocket/?login=moxie&password=wrongpassword"));
      client.waitForConnected(5000);
    } catch (ExecutionException e) {
      assertTrue(e.getCause() instanceof UpgradeException);
      assertEquals(((UpgradeException)e.getCause()).getResponseStatusCode(), 403);
      return;
    }

    throw new AssertionError("authenticated");
  }

  @Test
  public void testMissingAuthentication() throws Exception {
    SynchronousClient client = new SynchronousClient(new URI("ws://localhost:" + RULE.getLocalPort() + "/websocket/"));
    client.waitForConnected(5000);

    long                     requestId = client.sendRequest("GET", "/hello");
    WebSocketResponseMessage response  = client.readResponse(requestId, 5000);

    assertEquals(response.getStatus(), 200);
    assertTrue(response.getBody().isPresent());
    assertEquals(new String(response.getBody().get()), "world!");

    long                     badRequest  = client.sendRequest("GET", "/hello/named");
    WebSocketResponseMessage badResponse = client.readResponse(badRequest, 5000);

    assertEquals(401, badResponse.getStatus());
    assertFalse(badResponse.getBody().isPresent());

    long                     optionalRequest  = client.sendRequest("GET", "/hello/optional");
    WebSocketResponseMessage optionalResponse = client.readResponse(optionalRequest, 5000);

    assertEquals(200, optionalResponse.getStatus());
    assertTrue(optionalResponse.getBody().isPresent());
    assertEquals("missing", new String(optionalResponse.getBody().get()));
  }
}
