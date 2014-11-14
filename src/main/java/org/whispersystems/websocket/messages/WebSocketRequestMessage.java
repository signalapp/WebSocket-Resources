package org.whispersystems.websocket.messages;


import com.google.common.base.Optional;

public interface WebSocketRequestMessage {

  public String           getVerb();
  public String           getPath();
  public Optional<byte[]> getBody();
  public long             getRequestId();
  public boolean          hasRequestId();

}
