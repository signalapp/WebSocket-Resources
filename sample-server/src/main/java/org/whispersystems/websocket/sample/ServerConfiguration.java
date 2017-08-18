package org.whispersystems.websocket.sample;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.whispersystems.websocket.configuration.WebSocketConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.dropwizard.Configuration;

public class ServerConfiguration extends Configuration {

  @NotEmpty
  private String helloResponse = "world!";

  @Valid
  @NotNull
  @JsonProperty
  private WebSocketConfiguration webSocket = new WebSocketConfiguration();

  public String getHelloResponse() {
    return helloResponse;
  }

  public WebSocketConfiguration getWebSocketConfiguration() {
    return webSocket;
  }

}
