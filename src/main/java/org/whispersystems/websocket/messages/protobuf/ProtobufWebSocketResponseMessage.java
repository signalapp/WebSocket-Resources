package org.whispersystems.websocket.messages.protobuf;

import com.google.common.base.Optional;
import org.whispersystems.websocket.messages.WebSocketResponseMessage;

public class ProtobufWebSocketResponseMessage implements WebSocketResponseMessage {

  private final SubProtocol.WebSocketResponseMessage message;

  public ProtobufWebSocketResponseMessage(SubProtocol.WebSocketResponseMessage message) {
    this.message = message;
  }

  @Override
  public long getRequestId() {
    return message.getId();
  }

  @Override
  public int getStatus() {
    return message.getStatus();
  }

  @Override
  public String getMessage() {
    return message.getMessage();
  }

  @Override
  public Optional<byte[]> getBody() {
    if (message.hasBody()) {
      return Optional.of(message.getBody().toByteArray());
    } else {
      return Optional.absent();
    }
  }
}
