package com.sg.server;

import android.util.Log;
import java.net.InetSocketAddress;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 * @Author HBY
 * @Date 2021/5/28 15:20 概述：
 */
public class SocketServer extends WebSocketServer {

  private static SocketServer socketServer;

  public SocketServer(InetSocketAddress myHost) {
    super(myHost);
    EventBus.getDefault().register(this);
  }

  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    Log.d("WebSocketServer", "onOpen()：连接到: " + getRemoteSocketAddress(conn));
  }

  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    Log.d("WebSocketServer", "onClose");
  }

  @Override
  public void onMessage(WebSocket conn, String message) {
    Log.d("WebSocketServer", "onMessage" + message);
  }

  @Override
  public void onError(WebSocket conn, Exception ex) {
    Log.d("WebSocketServer", "onError" + ex.toString());
  }

  @Override
  public void onStart() {
    Log.d("WebSocketServer", "onStart:" + socketServer.getAddress());
  }

  public static void ready() {
    InetSocketAddress myHost = new InetSocketAddress(15210);
    socketServer = new SocketServer(myHost);
    try {
      socketServer.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Subscribe(threadMode = ThreadMode.BACKGROUND)
  public void send(Msg msg) {
    try {
      socketServer.broadcast(msg.getContent());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

