package org.whispersystems.websocket.sample.auth;

import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.whispersystems.websocket.auth.AuthenticationException;
import org.whispersystems.websocket.auth.WebSocketAuthenticator;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.dropwizard.auth.basic.BasicCredentials;

public class HelloAccountWebSocketAuthenticator implements WebSocketAuthenticator<HelloAccount> {

  private final HelloAccountBasicAuthenticator basicAuthenticator;

  public HelloAccountWebSocketAuthenticator(HelloAccountBasicAuthenticator basicAuthenticator) {
    this.basicAuthenticator = basicAuthenticator;
  }

  @Override
  public AuthenticationResult<HelloAccount> authenticate(UpgradeRequest request)
      throws AuthenticationException
  {
    try {
      Map<String, List<String>> parameters = request.getParameterMap();
      List<String>              usernames  = parameters.get("login");
      List<String>              passwords  = parameters.get("password");

      if (usernames == null || usernames.size() == 0 ||
          passwords == null || passwords.size() == 0)
      {
        return new AuthenticationResult<>(Optional.empty(), false);
      }

      BasicCredentials credentials = new BasicCredentials(usernames.get(0), passwords.get(0));
      return new AuthenticationResult<>(basicAuthenticator.authenticate(credentials), true);
    } catch (io.dropwizard.auth.AuthenticationException e) {
      throw new AuthenticationException(e);
    }
  }


}
