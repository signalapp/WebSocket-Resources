package org.whispersystems.websocket.client;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.StdErrLog;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.whispersystems.websocket.messages.WebSocketRequestMessage;
import org.whispersystems.websocket.messages.WebSocketResponseMessage;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@WebSocket(maxTextMessageSize = 64 * 1024)
public class Client implements WebSocketInterface.Listener {

  private final WebSocketInterface webSocket;

  public Client(WebSocketInterface webSocket) {
    this.webSocket = webSocket;
  }

  public static void main(String[] argv) {
    WebSocketClient    holder    = new WebSocketClient();
    WebSocketInterface webSocket = new WebSocketInterface();
    Client             client    = new Client(webSocket);

    StdErrLog logger = new StdErrLog();
    logger.setLevel(StdErrLog.LEVEL_OFF);
    Log.setLog(logger);

    try {
      webSocket.setListener(client);
      holder.start();

      URI                  uri     = new URI("ws://localhost:8080/websocket/?login=moxie&password=insecure");
      ClientUpgradeRequest request = new ClientUpgradeRequest();
      holder.connect(webSocket, uri, request);

      System.out.printf("Connecting...");
      Thread.sleep(10000);
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  @Override
  public void onReceivedRequest(WebSocketRequestMessage requestMessage) {
    System.err.println("Got request");

    try {
      webSocket.sendResponse(requestMessage.getRequestId(), 200, "OK", "world!".getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onReceivedResponse(WebSocketResponseMessage responseMessage) {
    System.err.println("Got response: " + responseMessage.getStatus());

    if (responseMessage.getBody().isPresent()) {
      System.err.println("Got response body: " + new String(responseMessage.getBody().get()));
    }
  }

  @Override
  public void onClosed() {
    System.err.println("onClosed()");
  }

  @Override
  public void onConnected() {
    try {
      webSocket.sendRequest(1, "GET", "/hello");
      webSocket.sendRequest(2, "GET", "/hello/named");
      webSocket.sendRequest(3, "GET", "/hello/prompt");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}