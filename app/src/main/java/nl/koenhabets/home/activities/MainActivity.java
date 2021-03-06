package nl.koenhabets.home.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import nl.koenhabets.home.GeofenceIntent;
import nl.koenhabets.home.R;
import nl.koenhabets.home.Receivers.AlarmReceiver;
import nl.koenhabets.home.Wol;
import nl.koenhabets.home.api.APIGraph;
import nl.koenhabets.home.api.ConfigApi;
import nl.koenhabets.home.api.Fish;
import nl.koenhabets.home.api.LedRequest;
import nl.koenhabets.home.api.Lights;
import nl.koenhabets.home.api.RefillFood;
import nl.koenhabets.home.api.SurvurApi;
import nl.koenhabets.home.api.WebSockets;
import nl.koenhabets.home.events.ConnectionEvent;
import nl.koenhabets.home.models.APIResponse;

public class MainActivity extends AppCompatActivity implements ColorPickerDialogListener {
    TextView textView;
    TextView textView2;
    TextView textViewWol;
    RequestQueue requestQueue;
    View parentLayout;

    private LineGraphSeries<DataPoint> mSeries1;

    Switch switch1;
    Switch switch2;
    Switch switch3;
    Switch switchLed;
    Switch switchLamp1;

    Button buttonWol;
    Button ledColorButton;
    private WebSockets websocket = new WebSockets();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        createNotifactionChannel();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    5);
        } else {
            Geofence geofence = new Geofence.Builder()
                    .setRequestId("home")
                    .setCircularRegion(50.903839, 6.029896, 100)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            GeofencingRequest geofenceRequest = new GeofencingRequest.Builder()
                    .addGeofence(geofence)
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .build();

            Intent intent = new Intent(this, GeofenceIntent.class);
            PendingIntent pendingIntent2 = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            GeofencingClient client = LocationServices.getGeofencingClient(this);
            client.addGeofences(geofenceRequest, pendingIntent2)
                    .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("Geofence", "added geofence");
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        parentLayout = findViewById(android.R.id.content);
        EventBus.getDefault().register(this);

        if (!WebSockets.returnConnected()) {
            websocket.connectToServer();
        }

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        requestQueue = Volley.newRequestQueue(this);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        textViewWol = findViewById(R.id.textViewWol);

        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        switch3 = findViewById(R.id.switch3);
        switchLed = findViewById(R.id.switchLed);
        switchLamp1 = findViewById(R.id.switchLamp1);

        buttonWol = findViewById(R.id.button2);
        ledColorButton = findViewById(R.id.ledColorButton);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!WebSockets.returnConnected()) {
                    websocket.reconnect();
                }
            }
        }, 5000, 10 * 1000);

        final SurvurApi request = new SurvurApi(new Response.Listener<APIResponse>() {
            @Override
            public void onResponse(APIResponse response) {
                parseData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(request);

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

        switchLed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchLed.isChecked()) {
                    setLedStrip(255, 255, 255);
                } else {
                    setLedStrip(0, 0, 0);
                }
            }
        });

        switchLamp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchLamp1.isChecked()) {
                    setLight("1;true");
                } else {
                    setLight("1;false");
                }
            }
        });

        ledColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialog.newBuilder().setColor(Color.BLACK).show(MainActivity.this);
            }
        });

        buttonWol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Wol wolRequest = new Wol(new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error", "" + error.getMessage());
                    }
                });
                requestQueue.add(wolRequest);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onData(APIResponse response) {
        parseData(response);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionEvent(ConnectionEvent response) {
        if (response.connected) {
            Snackbar.make(parentLayout, R.string.connected, Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(parentLayout, R.string.disconnected, Snackbar.LENGTH_LONG).show();
        }
    }

    public void setLight(String code) {
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

    public void setLedStrip(int red, int green, int blue) {
        LedRequest ledRequest = new LedRequest(red, green, blue, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());
            }
        });
        requestQueue.add(ledRequest);
    }

    private void parseData(APIResponse response) {
        textView.setText(getString(R.string.inside) + String.format(Locale.getDefault(), "%.2f °C", response.getTemperatureInside()));
        textView2.setText(getString(R.string.outside) + String.format(Locale.getDefault(), "%.2f °C", response.getTemperatureOutside()));
        switch1.setChecked(response.getLightA());
        switch2.setChecked(response.getLightB());
        switch3.setChecked(response.getLightC());
        switchLed.setChecked(response.isLedStrip());
        switchLamp1.setChecked(response.isLamp1());
        if(response.isEspLed()){
            switchLed.setTextColor(Color.GRAY);
        } else {
            switchLed.setTextColor(Color.parseColor("#FF9800"));
        }
        ledColorButton.setBackgroundColor(Color.rgb(response.getLedRed(), response.getLedGreen(), response.getLedBlue()));
        if (response.getPcOn()) {
            textViewWol.setText(R.string.ComputerAan);
        } else {
            textViewWol.setText(R.string.ComputerUit);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createNotifactionChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel mChannel = new NotificationChannel("my_channel_01", "Inside notification", NotificationManager.IMPORTANCE_LOW);
            mChannel.setDescription("Notification with temp and light buttons");
            mChannel.enableLights(false);
            mChannel.enableVibration(false);
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    @Override
    public void onColorSelected(int dialogId, int colorC) {
        int color = (int) Long.parseLong(Integer.toHexString(colorC), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        setLedStrip(r, g, b);
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}