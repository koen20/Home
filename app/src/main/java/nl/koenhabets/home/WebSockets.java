package nl.koenhabets.home;


import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class WebSockets {
    private static WebSocketClient mWebSocketClient;

    public static void connect_to_server() {
        URI uri;
        try {
            uri = new URI("wss://koenhabets.nl/ws");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        Map<String, String> headers = new HashMap<>();

        mWebSocketClient = new WebSocketClient(uri, new Draft_17(), headers, 0) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("Hello from websocket client");
            }

            @Override
            public void onMessage(String s) {
                Log.i("Websocket", "onMessage" + s);
            }

            @Override
            public void onClose(int code, String s, boolean b) {
                Log.i("Websocket", code + ": Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }
}
