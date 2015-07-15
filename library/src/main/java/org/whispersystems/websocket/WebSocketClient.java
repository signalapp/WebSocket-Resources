/**
 * Copyright (C) 2014 Open WhisperSystems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.whispersystems.websocket;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;
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
  private final RemoteEndpoint                                      remoteEndpoint;
  private final WebSocketMessageFactory                             messageFactory;
  private final Map<Long, SettableFuture<WebSocketResponseMessage>> pendingRequestMapper;

  public WebSocketClient(Session session, RemoteEndpoint remoteEndpoint,
                         WebSocketMessageFactory messageFactory,
                         Map<Long, SettableFuture<WebSocketResponseMessage>> pendingRequestMapper)
  {
    this.session              = session;
    this.remoteEndpoint       = remoteEndpoint;
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
      remoteEndpoint.sendBytes(ByteBuffer.wrap(requestMessage.toByteArray()));
    } catch (IOException | WebSocketException e) {
      logger.debug("Write", e);
      pendingRequestMapper.remove(requestId);
      future.setException(e);
    }

    return future;
  }

  public void close(int code, String message) {
    session.close(code, message);
  }

  private long generateRequestId() {
    try {
      return Math.abs(SecureRandom.getInstance("SHA1PRNG").nextLong());
    } catch (NoSuchAlgorithmException e) {
      throw new AssertionError(e);
    }
  }

}
