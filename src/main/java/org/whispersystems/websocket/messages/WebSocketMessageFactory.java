package org.whispersystems.websocket.messages;

import com.google.common.base.Optional;

public interface WebSocketMessageFactory {

  public WebSocketMessage parseMessage(byte[] serialized, int offset, int len)
      throws InvalidMessageException;

  public WebSocketMessage createRequest(Optional<Long> requestId,
                                        String verb, String path,
                                        Optional<byte[]> body);

  public WebSocketMessage createResponse(long requestId, int status, String message,
                                         Optional<byte[]> body);

}
