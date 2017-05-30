package nl.koenhabets.home;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.Objects;

public class CommandService extends IntentService {
    public CommandService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(Objects.equals(intent.getAction(), "lightA")){
            setLight("Aon");
        }
    }

    private void setLight(String code){
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
}
