package org.whispersystems.websocket.auth;

import com.google.common.base.Optional;
import org.eclipse.jetty.websocket.api.UpgradeRequest;

public interface WebSocketAuthenticator<T> {
  public Optional<T> authenticate(UpgradeRequest request) throws AuthenticationException;
}
