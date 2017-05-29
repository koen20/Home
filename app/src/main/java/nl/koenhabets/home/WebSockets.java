package nl.koenhabets.home;

import android.util.Log;

import com.google.gson.Gson;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import nl.koenhabets.home.events.ConnectionEvent;
import nl.koenhabets.home.models.APIResponse;

public class WebSockets {
    private static boolean connected = false;
    private WebSocket webSocket;

    public static boolean returnConnected() {
        return connected;
    }

    static void setConnected(boolean connectedB) {
        connected = connectedB;
    }

    public void connectToServer() {
        WebSocketFactory factory = new WebSocketFactory().setConnectionTimeout(5000);

        try {
            webSocket = factory.createSocket("ws://server.koenhabets.nl/ws");
            webSocket.addListener(new SurvurAdapater());
            webSocket.connect();
        } catch (IOException | WebSocketException e) {
            e.printStackTrace();
        }
    }

    public void stopWebsocket() {
        webSocket.disconnect();
    }

    public void reconnect() {
        try {
            WebSockets.this.webSocket = webSocket.recreate().connect();
        } catch (WebSocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class SurvurAdapater extends WebSocketAdapter {
        @Override
        public void onTextMessage(WebSocket websocket, String message) {
            Log.i("Websocket message", message);
            Gson parser = new Gson();
            APIResponse response = parser.fromJson(message, APIResponse.class);
            EventBus.getDefault().post(response);
        }

        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
            Log.i("Websocket", "Websocket disconnected reconnecting...");
            connected = false;
            EventBus.getDefault().post(new ConnectionEvent(connected));
            WebSockets.this.webSocket = websocket.recreate().connect();
        }

        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            super.onConnected(websocket, headers);
            Log.i("Websocket", "Connected to websocket");
            connected = true;
            EventBus.getDefault().post(new ConnectionEvent(connected));
        }
    }
}
