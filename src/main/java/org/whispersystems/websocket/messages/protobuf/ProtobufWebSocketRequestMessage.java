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
import org.whispersystems.websocket.messages.WebSocketRequestMessage;

public class ProtobufWebSocketRequestMessage implements WebSocketRequestMessage {

  private final SubProtocol.WebSocketRequestMessage message;

  ProtobufWebSocketRequestMessage(SubProtocol.WebSocketRequestMessage message) {
    this.message = message;
  }

  @Override
  public String getVerb() {
    return message.getVerb();
  }

  @Override
  public String getPath() {
    return message.getPath();
  }

  @Override
  public Optional<byte[]> getBody() {
    if (message.hasBody()) {
      return Optional.of(message.getBody().toByteArray());
    } else {
      return Optional.absent();
    }
  }

  @Override
  public long getRequestId() {
    return message.getId();
  }

  @Override
  public boolean hasRequestId() {
    return message.hasId();
  }
}
