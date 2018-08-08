package org.whispersystems.websocket;

import com.google.common.util.concurrent.SettableFuture;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.client.io.UpgradeListener;
import org.whispersystems.websocket.messages.InvalidMessageException;
import org.whispersystems.websocket.messages.WebSocketMessage;
import org.whispersystems.websocket.messages.WebSocketMessageFactory;
import org.whispersystems.websocket.messages.WebSocketResponseMessage;
import org.whispersystems.websocket.messages.protobuf.ProtobufWebSocketMessageFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@WebSocket(maxTextMessageSize = 64 * 1024)
public class SynchronousClient {

  private final SettableFuture<Session>             connectFuture = SettableFuture.create();
  private final Map<Long, WebSocketResponseMessage> responses     = new HashMap<>();

  private Session session;

  public SynchronousClient(URI uri) throws Exception {
    WebSocketClient client = new WebSocketClient();
    client.start();
    client.connect(this, uri, new ClientUpgradeRequest(), new UpgradeListener() {
      @Override
      public void onHandshakeRequest(UpgradeRequest upgradeRequest) {

      }

      @Override
      public void onHandshakeResponse(UpgradeResponse upgradeResponse) {
        System.out.println("Handshake response: " + upgradeResponse.getStatusCode());
      }
    });
  }

  @OnWebSocketError
  public void onError(Throwable error) {
    connectFuture.setException(error);
    error.printStackTrace();
  }

  @OnWebSocketClose
  public void onClose(int statusCode, String reason) {
    System.out.println("onClose(" + statusCode + ", " + reason + ")");
  }

  @OnWebSocketConnect
  public void onConnect(Session session) {
    this.session = session;
    connectFuture.set(session);
  }

  @OnWebSocketMessage
  public void onMessage(byte[] buffer, int offset, int length) {
    try {
      WebSocketMessageFactory factory = new ProtobufWebSocketMessageFactory();
      WebSocketMessage        message = factory.parseMessage(buffer, offset, length);

      if (message.getType() == WebSocketMessage.Type.RESPONSE_MESSAGE) {
        synchronized (responses) {
          responses.put(message.getResponseMessage().getRequestId(), message.getResponseMessage());
          responses.notifyAll();
        }
      } else {
        System.out.println("Received websocket message of unknown type: " + message.getType());
      }

    } catch (InvalidMessageException e) {
      e.printStackTrace();
    }
  }

  public void waitForConnected(int timeoutMillis) throws InterruptedException, ExecutionException, TimeoutException {
    connectFuture.get(timeoutMillis, TimeUnit.MILLISECONDS);
  }

  public long sendRequest(String verb, String path) throws IOException {
    WebSocketMessageFactory factory = new ProtobufWebSocketMessageFactory();
    long                    id      = new SecureRandom().nextLong();

    WebSocketMessage message = factory.createRequest(Optional.of(id), verb, path, new LinkedList<>(), Optional.empty());
    session.getRemote().sendBytes(ByteBuffer.wrap(message.toByteArray()));

    return id;
  }

  public WebSocketResponseMessage readResponse(long id, long timeoutMillis) throws InterruptedException {
    synchronized (responses){
      while (!responses.containsKey(id)) responses.wait(timeoutMillis);
      return responses.get(id);
    }
  }

}
