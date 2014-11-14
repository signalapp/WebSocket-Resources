package org.whispersystems.websocket.auth;

public class AuthenticationException extends Exception {

  public AuthenticationException(String s) {
    super(s);
  }

  public AuthenticationException(Exception e) {
    super(e);
  }

}
