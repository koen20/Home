package nl.koenhabets.home.activities;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import nl.koenhabets.home.R;
import nl.koenhabets.home.api.ConfigApi;
import nl.koenhabets.home.api.SurvurApi;
import nl.koenhabets.home.models.APIResponse;

public class SettingsActivity extends AppCompatActivity {
    View parentLayout;
    Switch switchAlarm;
    Switch switchMovement;
    EditText editText;
    Button button;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        parentLayout = findViewById(android.R.id.content);
        EventBus.getDefault().register(this);

        switchAlarm = findViewById(R.id.switchAlarm);
        switchMovement = findViewById(R.id.switchMovement);
        editText = findViewById(R.id.editTextFeedInterval);
        button = findViewById(R.id.buttonSetInterval);

        SurvurApi request = new SurvurApi(new Response.Listener<APIResponse>() {
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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setConfig("feedInterval", editText.getText().toString());
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
        editText.setText(data.getFeedInterval() + "");
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
