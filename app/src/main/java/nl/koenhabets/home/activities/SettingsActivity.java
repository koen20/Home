package nl.koenhabets.home.activities;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import nl.koenhabets.home.ConfigApi;
import nl.koenhabets.home.R;
import nl.koenhabets.home.models.APIResponse;

public class SettingsActivity extends AppCompatActivity {
    View parentLayout;
    Switch switchAlarm;
    Switch switchMovement;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        parentLayout = findViewById(android.R.id.content);
        EventBus.getDefault().register(this);

        switchAlarm = (Switch) findViewById(R.id.switchAlarm);
        switchMovement = (Switch) findViewById(R.id.switchMovement);

        switchAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchAlarm.isChecked()) {
                    setConfig("alarm", "true");
                } else {
                    setConfig("alarm", "false");
                }
            }
        });

        switchMovement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchMovement.isChecked()) {
                    setConfig("motion", "true");
                } else {
                    setConfig("motion", "false");
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onData(APIResponse response) {
        parseData(response);
    }

    private void parseData(APIResponse data){
        switchAlarm.setChecked(data.getAlarmEnabled());
        switchMovement.setChecked(data.getMotionEnabled());
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
}
