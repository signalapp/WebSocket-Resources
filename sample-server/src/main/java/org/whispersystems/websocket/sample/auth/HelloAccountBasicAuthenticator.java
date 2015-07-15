package org.whispersystems.websocket.sample.auth;

import com.google.common.base.Optional;
import org.whispersystems.dropwizard.simpleauth.Authenticator;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.basic.BasicCredentials;

public class HelloAccountBasicAuthenticator implements Authenticator<BasicCredentials, HelloAccount> {
  @Override
  public Optional<HelloAccount> authenticate(BasicCredentials credentials)
      throws AuthenticationException
  {
    if (credentials.getUsername().equals("moxie") &&
        credentials.getPassword().equals("insecure"))
    {
      return Optional.of(new HelloAccount("moxie"));
    }

    return Optional.absent();
  }
}
