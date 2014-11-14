package org.whispersystems.websocket;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.websocket.messages.WebSocketMessage;
import org.whispersystems.websocket.messages.WebSocketMessageFactory;
import org.whispersystems.websocket.messages.WebSocketResponseMessage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;

public class WebSocketClient {

  private static final Logger logger = LoggerFactory.getLogger(WebSocketClient.class);

  private final Session                                             session;
  private final WebSocketMessageFactory                             messageFactory;
  private final Map<Long, SettableFuture<WebSocketResponseMessage>> pendingRequestMapper;

  public WebSocketClient(Session session,
                         WebSocketMessageFactory messageFactory,
                         Map<Long, SettableFuture<WebSocketResponseMessage>> pendingRequestMapper)
  {
    this.session              = session;
    this.messageFactory       = messageFactory;
    this.pendingRequestMapper = pendingRequestMapper;
  }

  public ListenableFuture<WebSocketResponseMessage> sendRequest(String verb, String path,
                                                                Optional<byte[]> body)
  {
    long                                     requestId = generateRequestId();
    SettableFuture<WebSocketResponseMessage> future    = SettableFuture.create();

    pendingRequestMapper.put(requestId, future);

    WebSocketMessage requestMessage = messageFactory.createRequest(Optional.of(requestId), verb, path, body);

    try {
      session.getRemote().sendBytes(ByteBuffer.wrap(requestMessage.toByteArray()));
    } catch (IOException e) {
      logger.debug("Write", e);
      pendingRequestMapper.remove(requestId);
      future.setException(e);
    }

    return future;
  }

  public void close(int code, String message) {
    try {
      session.close(code, message);
    } catch (IOException e) {
      logger.debug("Closing", e);
    }
  }

  private long generateRequestId() {
    try {
      return Math.abs(SecureRandom.getInstance("SHA1PRNG").nextLong());
    } catch (NoSuchAlgorithmException e) {
      throw new AssertionError(e);
    }
  }

}
