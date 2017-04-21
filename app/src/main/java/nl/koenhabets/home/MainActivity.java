package nl.koenhabets.home;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.neovisionaries.ws.client.OpeningHandshakeException;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import nl.koenhabets.home.models.APIResponse;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    TextView textView2;
    TextView textView5;
    RequestQueue requestQueue;

    Switch switch1;
    Switch switch2;
    Switch switch3;
    Switch switch4;
    Switch switch5;

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        requestQueue = Volley.newRequestQueue(this);
        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView5 = (TextView) findViewById(R.id.textView5);

        switch1 = (Switch) findViewById(R.id.switch1);
        switch2 = (Switch) findViewById(R.id.switch2);
        switch3 = (Switch) findViewById(R.id.switch3);
        switch4 = (Switch) findViewById(R.id.switch4);
        switch5 = (Switch) findViewById(R.id.switch5);

        button = (Button) findViewById(R.id.button);

        connect_to_server();

        final SurvurApi request = new SurvurApi(new Response.Listener<APIResponse>() {
            @Override
            public void onResponse(APIResponse response) {
                textView.setText(String.format(Locale.getDefault(), "%.2f °C", response.getTemperatureInside()));
                textView2.setText(String.format(Locale.getDefault(), "%.2f °C", response.getTemperatureOutside()));
                switch1.setChecked(response.getLightA());
                switch2.setChecked(response.getLightB());
                switch3.setChecked(response.getLightC());
                switch4.setChecked(response.getAlarmEnabled());
                switch5.setChecked(response.getMotionEnabled());
                textView5.setText("Eten: " + response.getFishFood());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        //requestQueue.add(request);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //requestQueue.add(request);
            }
        }, 0, 5 * 1000);

        /*
        APIGraph apiGraphRequest = new APIGraph(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());
            }
        });
        requestQueue.add(apiGraphRequest);*/


        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switch1.isChecked()) {
                    setLight("Aon");
                } else {
                    setLight("Aoff");
                }
            }
        });

        switch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switch2.isChecked()) {
                    setLight("Bon");
                } else {
                    setLight("Boff");
                }
            }
        });


        switch3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switch3.isChecked()) {
                    setLight("Con");
                } else {
                    setLight("Coff");
                }
            }
        });

        switch4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switch4.isChecked()) {
                    setConfig("alarm", "true");
                } else {
                    setConfig("alarm", "false");
                }
            }
        });

        switch5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switch5.isChecked()) {
                    setConfig("motion", "true");
                } else {
                    setConfig("motion", "false");
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fish fishRequest = new Fish(new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error", "" + error.getMessage());
                    }
                });
                requestQueue.add(fishRequest);
            }
        });
    }

    public void setLight(String code) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Lights lightRequest = new Lights(code, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());
            }
        });
        requestQueue.add(lightRequest);
    }

    public void setConfig(String thing, String status) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        ConfigApi configApi = new ConfigApi(thing, status, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());
            }
        });
        requestQueue.add(configApi);
    }

    public void parseData(String data) {
        Log.i("Parsing", data);
        Gson parser = new Gson();
        APIResponse parsedData = parser.fromJson(data, APIResponse.class);
        Log.i("lampa", parsedData.getLightA() + "");
        textView.setText(String.format(Locale.getDefault(), "%.2f °C", parsedData.getTemperatureInside()));
        textView2.setText(String.format(Locale.getDefault(), "%.2f °C", parsedData.getTemperatureOutside()));
        switch1.setChecked(parsedData.getLightA());
        switch2.setChecked(parsedData.getLightB());
        //switch3.setChecked(parsedData.getLightC());
        switch4.setChecked(parsedData.getAlarmEnabled());
        switch5.setChecked(parsedData.getMotionEnabled());
        textView5.setText("Eten: " + parsedData.getFishFood());
    }

    public void connect_to_server() {
        WebSocketFactory factory = new WebSocketFactory().setConnectionTimeout(5000);

        WebSocket ws = null;
        try {
            ws = factory.createSocket("ws://server.koenhabets.nl/ws");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ws.addListener(new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket websocket, String message) {
                Log.i("Websocket message", message);
                parseData(message);
            }
        });
        try {
            ws.connect();
        } catch (OpeningHandshakeException e) {
            e.printStackTrace();
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }
}