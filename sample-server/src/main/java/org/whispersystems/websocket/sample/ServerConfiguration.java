package org.whispersystems.websocket.sample;

import org.hibernate.validator.constraints.NotEmpty;

import io.dropwizard.Configuration;

public class ServerConfiguration extends Configuration {

  @NotEmpty
  private String helloResponse = "world!";

  public String getHelloResponse() {
    return helloResponse;
  }

}
