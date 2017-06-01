package nl.koenhabets.home.api;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ConfigApi extends Request<String> {
    private static String url = "https://koenhabets.nl/api/config";

    private Response.Listener<String> responListener;
    private String status;
    private String thing;

    public ConfigApi(String thing, String status,
                     Response.Listener<String> responseListener,
                     Response.ErrorListener errorListener) {

        super(Request.Method.POST, url + "?config=" + thing + "&status=" + status, errorListener);

        this.responListener = responseListener;
        this.thing = thing;
        this.status = status;
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put(thing, status);
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
