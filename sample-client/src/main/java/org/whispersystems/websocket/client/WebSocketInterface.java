package org.whispersystems.websocket.client;

import com.google.common.base.Optional;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.whispersystems.websocket.messages.InvalidMessageException;
import org.whispersystems.websocket.messages.WebSocketMessage;
import org.whispersystems.websocket.messages.WebSocketMessageFactory;
import org.whispersystems.websocket.messages.WebSocketRequestMessage;
import org.whispersystems.websocket.messages.WebSocketResponseMessage;
import org.whispersystems.websocket.messages.protobuf.ProtobufWebSocketMessageFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

@WebSocket(maxTextMessageSize = 64 * 1024)
public class WebSocketInterface {

  private final WebSocketMessageFactory factory = new ProtobufWebSocketMessageFactory();

  private       Listener listener;
  private       Session  session;

  public WebSocketInterface() {}

  public void setListener(Listener listener) {
    this.listener = listener;
  }

  @OnWebSocketClose
  public void onClose(int statusCode, String reason) {
    listener.onClosed();
  }

  @OnWebSocketConnect
  public void onConnect(Session session) {
    this.session = session;
    listener.onConnected();
  }

  @OnWebSocketMessage
  public void onMessage(byte[] buffer, int offset, int length) {
    try {
      WebSocketMessage message = factory.parseMessage(buffer, offset, length);

      if (message.getType() == WebSocketMessage.Type.REQUEST_MESSAGE) {
        listener.onReceivedRequest(message.getRequestMessage());
      } else if (message.getType() == WebSocketMessage.Type.RESPONSE_MESSAGE) {
        listener.onReceivedResponse(message.getResponseMessage());
      } else {
        System.out.println("Received websocket message of unknown type: " + message.getType());
      }

    } catch (InvalidMessageException e) {
      e.printStackTrace();
    }
  }

  public void sendRequest(long id, String verb, String path) throws IOException {
    WebSocketMessage message = factory.createRequest(Optional.of(id), verb, path, Optional.<byte[]>absent());
    session.getRemote().sendBytes(ByteBuffer.wrap(message.toByteArray()));
  }

  public void sendResponse(long id, int code, String message, byte[] body) throws IOException {
    WebSocketMessage response = factory.createResponse(id, code, message, Optional.fromNullable(body));
    session.getRemote().sendBytes(ByteBuffer.wrap(response.toByteArray()));
  }

  public interface Listener {
    public void onReceivedRequest(WebSocketRequestMessage requestMessage);
    public void onReceivedResponse(WebSocketResponseMessage responseMessage);
    public void onClosed();
    public void onConnected();
  }
}
