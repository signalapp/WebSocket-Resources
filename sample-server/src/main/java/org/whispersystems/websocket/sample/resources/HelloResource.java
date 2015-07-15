package org.whispersystems.websocket.sample.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.websocket.messages.WebSocketResponseMessage;
import org.whispersystems.websocket.sample.auth.HelloAccount;
import org.whispersystems.websocket.session.WebSocketSession;
import org.whispersystems.websocket.session.WebSocketSessionContext;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import io.dropwizard.auth.Auth;

@Path("/hello")
public class HelloResource {

  private static final Logger logger = LoggerFactory.getLogger(HelloResource.class);

  private final String response;

  public HelloResource(String response) {
    this.response = response;
  }

  @GET
  @Timed
  @Produces("text/plain")
  public String sayHello() {
    return response;
  }

  @GET
  @Path("/named")
  @Timed
  @Produces("text/plain")
  public String saySpecialHello(@Auth HelloAccount account) {
    return "Hello " + account.getUsername();
  }

  @GET
  @Path("/prompt")
  public Response askMe(@Auth HelloAccount account,
                        @WebSocketSession WebSocketSessionContext context)
  {
    ListenableFuture<WebSocketResponseMessage> response = context.getClient().sendRequest("GET", "/hello", Optional.<byte[]>absent());
    Futures.addCallback(response, new FutureCallback<WebSocketResponseMessage>() {
      @Override
      public void onSuccess(WebSocketResponseMessage result) {
        logger.warn("Got response: " + new String(result.getBody().orNull()));
      }

      @Override
      public void onFailure(Throwable t) {
        logger.warn("Request error", t);
      }
    });

    return Response.ok().build();
  }

}
