package nl.koenhabets.home;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class Lights extends Request<String> {
    private static String url = "https://koenhabets.nl/api/lights";

    private Response.Listener<String> responListener;
    private String light;

    public Lights(String light,
            Response.Listener<String> responseListener,
                    Response.ErrorListener errorListener) {

        super(Request.Method.POST, url + "?" + light, errorListener);

        this.responListener = responseListener;
        this.light = light;
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("light", light);
        return params;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String data;
        try {
            data = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            data = new String(response.data);
        }
        return Response.success(data, null);
    }

    @Override
    protected void deliverResponse(String response) {
        responListener.onResponse(response);
    }
}
