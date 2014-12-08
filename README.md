# WebSocket-Resources

A Dropwizard library that lets you use Jersey-style Resources over WebSockets.

Install from maven central:

```
<dependency>
  <groupId>org.whispersystems</groupId>
  <artifactId>websocket-resources</artifactId>
  <version>${latest_version}</version>
</dependency>
```

## The problem

In the standard HTTP world, we might use Jersey to define a set of REST APIs:

```
@Path("/api/v1/mail")
public class MailResource {
  
  @Timed
  @POST
  @Path("/{destination}/")
  @Consumes(MediaType.APPLICATION_JSON_TYPE)
  public void sendMessage(@Auth Account sender, 
                          @PathParam("destination") String destination,
                          @Valid Message message) 
  {
    ...
  }
}
```

Using JAX-RS annotations and some Dropwizard glue, we can easily define a set of resource methods
that allow an authenticated sender to POST a JSON Message object.  All of the routing, parsing,
validation, and authentication are taken care of, and the resource method can focus on the business
logic.

What if we want to expose a similar API over a WebSocket?  It's not pretty.  We have to define our
own sub-protocol, do all of the parsing and validation ourselves, keep track of the connection state,
and do our own routing.  It's basically the equivalent of writing a raw servlet, but worse.

## The WebSocket-Resources model

WebSocket-Resources is designed to make exposing an API over a WebSocket as simple as writing a
Jersey resource.  The library is based on the premise that the WebSocket client and the
WebSocket server should each be modeled as both a HTTP client and server simultaneously.

That is, the WebSocket server receives HTTP-style requests and issues HTTP-style responses, but it
can also issue HTTP-style requests to the client, and expects HTTP-style responses from the client.
This allows us to write Jersey-style resources, while also initiating bi-directional communication
from the server.

What if we wanted to make the exact same resource above available over a WebSocket using
WebSocket-Resources? In your standard Dropwizard service run method, just initialize
WebSocket-Resources and register a standard Jersey resource:

```
  @Override
  public void run(WhisperServerConfiguration config, Environment environment)
      throws Exception
  {
    WebSocketEnvironment webSocketEnvironment = new WebSocketEnvironment(environment, config);
    webSocketEnvironment.jersey().register(new MailResource());
    webSocketEnvironment.setAuthenticator(new MyWebSocketAuthenticator());

    WebSocketResourceProviderFactory servlet   = new WebSocketResourceProviderFactory(webSocketEnvironment);
    ServletRegistration.Dynamic      websocket = environment.servlets().addServlet("WebSocket", servlet);

    websocket.addMapping("/api/v1/websocket/*");
    websocket.setAsyncSupported(true);
    servlet.start();
    
    ...    
  }
```

It's as simple as creating a `WebSocketEnvironment` from the Dropwizard `Environment` and registering
Jersey resources.

## Making requests

In order to call the Jersey resource we just registered from a client, we need to know how to format
client requests.  It's possible to either define our own subprotocol, or to use the default subprotocol
packaged with WebSocket-Resources, which is based in protobuf.

A subprotocol is composed of `Request`s and `Response`s.  A `Request` has four parts:

1. An `id`.
1. A `method`.
1. A `path`.
1. An optional `body`.

A `Response` has four parts:

1. The request `id` it is in response to.
1. A `status code`.
1. A `status message`.
1. An optional `body`.

This should seem strongly reminiscent of HTTP.  By default, WebSocket-Resources will use a protobuf
formatted subprotocol:

```
message WebSocketRequestMessage {
  optional string verb = 1;
  optional string path = 2;
  optional bytes  body = 3;
  optional uint64 id   = 4;
}

message WebSocketResponseMessage {
  optional uint64 id      = 1;
  optional uint32 status  = 2;
  optional string message = 3;
  optional bytes  body    = 4;
}

message WebSocketMessage {
  enum Type {
    UNKNOWN  = 0;
    REQUEST  = 1;
    RESPONSE = 2;
  }

  optional Type                     type     = 1;
  optional WebSocketRequestMessage  request  = 2;
  optional WebSocketResponseMessage response = 3;
}
```

To use a custom wire format, it's as simple as implementing a custom `WebSocketMessageFactory` and
registering it at initialization time:

```
  @Override
  public void run(WhisperServerConfiguration config, Environment environment)
      throws Exception
  {
    WebSocketEnvironment webSocketEnvironment = new WebSocketEnvironment(environment);
    webSocketEnvironment.setMessageFactory(MyMessageFactory());
    ...
  }
```

## Making requests from the server

To issue requests from the server, use `WebSocketClient`.  There are two ways to get a `WebSocketClient`
instance: a resource annotation or a connection listener.

Resource annotation:

```
@Path("/api/v1/mail")
public class MailResource {
  
  @Timed
  @POST
  @Path("/{destination}/")
  @Consumes(MediaType.APPLICATION_JSON_TYPE)
  public void sendMessage(@Auth Account sender, 
                          @WebSocketSession WebSocketSessionContext context, 
                          @PathParam("destination") String destination,
                          @Valid Message message) 
  {
    WebSocketClient client = context.getClient();
    ...
  }
}

```

Or a connect listener:

```
  @Override
  public void run(WhisperServerConfiguration config, Environment environment)
      throws Exception
  {
    WebSocketEnvironment webSocketEnvironment = new WebSocketEnvironment(environment);
    webSocketEnvironment.setConnectListener(new WebSocketConnectListener() {
      @Override
      public void onConnect(WebSocketSessionContext context) {
        WebSocketClient client = context.getClient();
        ...
      }
    });
    ...
  }
```

A WebSocketClient can then be issued to transmit requests:

```
  WebSocketClient client = context.getClient();
  
  ListenableFuture<WebSocketResponseMessage> response = client.sendRequest("PUT", "/api/v1/message", body);
  
  Futures.addCallback(response, new FutureCallback<WebSocketResponseMessage>() {
    @Override
    public void onSuccess(@Nullable WebSocketResponseMessage response) {
      ...
    }

    @Override
    public void onFailure(@Nonnull Throwable throwable) {
      ...
    }
  });
```

License
---------------------

Copyright 2014 Open Whisper Systems

Licensed under the AGPLv3: https://www.gnu.org/licenses/agpl-3.0.html
