package org.whispersystems.websocket.sample;

import org.whispersystems.dropwizard.simpleauth.AuthDynamicFeature;
import org.whispersystems.dropwizard.simpleauth.AuthValueFactoryProvider;
import org.whispersystems.dropwizard.simpleauth.BasicCredentialAuthFilter;
import org.whispersystems.websocket.WebSocketResourceProviderFactory;
import org.whispersystems.websocket.sample.auth.HelloAccount;
import org.whispersystems.websocket.sample.auth.HelloAccountBasicAuthenticator;
import org.whispersystems.websocket.sample.auth.HelloAccountWebSocketAuthenticator;
import org.whispersystems.websocket.sample.resources.HelloResource;
import org.whispersystems.websocket.setup.WebSocketEnvironment;

import javax.servlet.ServletRegistration;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class Server extends Application<ServerConfiguration> {
  
  @Override
  public void run(ServerConfiguration serverConfiguration, Environment environment)
      throws Exception
  {
    WebSocketEnvironment           webSocketEnvironment    = new WebSocketEnvironment(environment, serverConfiguration);
    HelloResource                  helloResource           = new HelloResource                 (serverConfiguration.getHelloResponse());
    HelloAccountBasicAuthenticator helloBasicAuthenticator = new HelloAccountBasicAuthenticator(                                      );


    environment.jersey().register(helloResource);
    environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<HelloAccount>()
                                                             .setAuthenticator(helloBasicAuthenticator)
                                                             .setPrincipal(HelloAccount.class)
                                                             .buildAuthFilter()));
    environment.jersey().register(new AuthValueFactoryProvider.Binder());

    webSocketEnvironment.jersey().register(helloResource);
    webSocketEnvironment.setAuthenticator(new HelloAccountWebSocketAuthenticator(helloBasicAuthenticator));

    WebSocketResourceProviderFactory servlet   = new WebSocketResourceProviderFactory(webSocketEnvironment);
    ServletRegistration.Dynamic      websocket = environment.servlets().addServlet("WebSocket", servlet);

    websocket.addMapping("/websocket/*");
    websocket.setAsyncSupported(true);
    servlet.start();
  }

  public static void main(String[] args) throws Exception {
    new Server().run(args);
  }

}
