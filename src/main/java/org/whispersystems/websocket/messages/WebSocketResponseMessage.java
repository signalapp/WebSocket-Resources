package org.whispersystems.websocket.messages;

import com.google.common.base.Optional;

public interface WebSocketResponseMessage {
  public long             getRequestId();
  public int              getStatus();
  public String           getMessage();
  public Optional<byte[]> getBody();
}
