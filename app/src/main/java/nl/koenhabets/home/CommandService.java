package nl.koenhabets.home;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.Objects;

import nl.koenhabets.home.models.APIResponse;

public class CommandService extends IntentService {
    public CommandService() {
        super("CommandService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        SurvurApi request = new SurvurApi(new Response.Listener<APIResponse>() {
            @Override
            public void onResponse(APIResponse response) {
                if (Objects.equals(intent.getAction(), "lightA")) {
                    if (response.getLightA()) {
                        setLight("Aoff");
                    } else {
                        setLight("Aon");
                    }
                } else if (Objects.equals(intent.getAction(), "lightB")) {
                    if (response.getLightB()) {
                        setLight("Boff");
                    } else {
                        setLight("Bon");
                    }
                } else if (Objects.equals(intent.getAction(), "lightC")) {
                    if (response.getLightC()) {
                        setLight("Coff");
                    } else {
                        setLight("Con");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(request);

    }

    private void setLight(String code) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
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
