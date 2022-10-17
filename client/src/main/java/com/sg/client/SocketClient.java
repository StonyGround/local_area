package com.sg.client;

import android.util.Log;
import java.net.URI;
import org.greenrobot.eventbus.EventBus;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;

public class SocketClient extends WebSocketClient {

  public SocketClient(URI serverUri) {
    super(serverUri);
  }

  @Override
  public void onOpen(ServerHandshake handshakedata) {
    Log.d("WebSocketClient", "onOpen" + "成功连接到：" + getRemoteSocketAddress());
    EventBus.getDefault().post(new Msg("已连接到" + getRemoteSocketAddress()));
  }

  @Override
  public void onMessage(String message) {
    Log.d("WebSocketClient", "onMessage" + message);
    EventBus.getDefault().post(new Msg(message));
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
    Log.d("WebSocketClient", "onClose" + reason);
  }

  @Override
  public void onError(Exception ex) {
    Log.d("WebSocketClient", "onError" + ex.getMessage());
  }

  private static WebSocketClient webSocketClient;

  public static boolean connect(String ip) {
    if (webSocketClient != null) {
      Release();
    }
    if (webSocketClient == null) {
      URI uri = URI.create("ws://" + ip + ":15210");
      webSocketClient = new SocketClient(uri);
    }
    try {
      webSocketClient.connectBlocking();
      return true;
    } catch (InterruptedException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static void Release() {
    Close();
    webSocketClient = null;
  }

  public static void Close() {
    if (webSocketClient == null) {
      return;
    }
    if (!webSocketClient.isOpen()) {
      return;
    }
    try {
      webSocketClient.closeBlocking();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void Send(String string) {
    if (webSocketClient == null) {
      return;
    }
    if (!webSocketClient.isOpen()) {
      Reconnect();
    }
    try {
      webSocketClient.send(string);
    } catch (WebsocketNotConnectedException e) {
      e.printStackTrace();
    }
  }

  public static boolean Reconnect() {
    if (webSocketClient == null) {
      return false;
    }
    if (webSocketClient.isOpen()) {
      return true;
    }
    try {
      webSocketClient.reconnectBlocking();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}


