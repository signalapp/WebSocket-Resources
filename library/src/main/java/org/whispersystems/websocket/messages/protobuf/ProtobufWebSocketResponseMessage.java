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
