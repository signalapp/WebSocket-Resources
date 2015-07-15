package org.whispersystems.websocket.sample.auth;

import com.google.common.base.Optional;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.whispersystems.websocket.auth.AuthenticationException;
import org.whispersystems.websocket.auth.WebSocketAuthenticator;

import java.util.List;
import java.util.Map;

import io.dropwizard.auth.basic.BasicCredentials;

public class HelloAccountWebSocketAuthenticator implements WebSocketAuthenticator<HelloAccount> {

  private final HelloAccountBasicAuthenticator basicAuthenticator;

  public HelloAccountWebSocketAuthenticator(HelloAccountBasicAuthenticator basicAuthenticator) {
    this.basicAuthenticator = basicAuthenticator;
  }

  @Override
  public Optional<HelloAccount> authenticate(UpgradeRequest request)
      throws AuthenticationException
  {
    try {
      Map<String, List<String>> parameters = request.getParameterMap();
      List<String>              usernames  = parameters.get("login");
      List<String>              passwords  = parameters.get("password");

      if (usernames == null || usernames.size() == 0 ||
          passwords == null || passwords.size() == 0)
      {
        return Optional.absent();
      }

      BasicCredentials credentials = new BasicCredentials(usernames.get(0), passwords.get(0));
      return basicAuthenticator.authenticate(credentials);
    } catch (io.dropwizard.auth.AuthenticationException e) {
      throw new AuthenticationException(e);
    }
  }


}
