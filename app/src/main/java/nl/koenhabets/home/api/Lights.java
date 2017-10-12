package nl.koenhabets.home.api;


import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import nl.koenhabets.home.KeyHolder;


public class Lights extends Request<String> {
    private static String url = "https://koenhabets.nl/api/lights";

    private Response.Listener<String> responListener;
    private String light;

    public Lights(String light,
            Response.Listener<String> responseListener,
                    Response.ErrorListener errorListener) {

        super(Request.Method.POST, url + "?light=" + light, errorListener);

        this.responListener = responseListener;
        this.light = light;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String>  params = new HashMap<String, String>();
        String credentials = KeyHolder.getUsername() + ":" + KeyHolder.getPassword();
        String auth = "Basic "
                + Base64.encodeToString(credentials.getBytes(),
                Base64.NO_WRAP);
        params.put("Authorization", auth);
        return params;
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
