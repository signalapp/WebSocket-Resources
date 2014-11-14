package org.whispersystems.websocket.messages;

public interface WebSocketMessage {

  public enum Type {
    UNKNOWN_MESSAGE,
    REQUEST_MESSAGE,
    RESPONSE_MESSAGE
  }

  public Type                     getType();
  public WebSocketRequestMessage  getRequestMessage();
  public WebSocketResponseMessage getResponseMessage();
  public byte[]                   toByteArray();
}
