package org.whispersystems.websocket.sample.auth;

import org.whispersystems.dropwizard.simpleauth.Authenticator;

import java.util.Optional;

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

    return Optional.empty();
  }
}
