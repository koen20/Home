package nl.koenhabets.home.api;


import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

import nl.koenhabets.home.models.APIResponse;

public class SurvurApi extends Request<APIResponse> {
    private final static String URL = "https://koenhabets.nl/api/info";

    private Response.Listener<APIResponse> responseListener;

    public SurvurApi(Response.Listener<APIResponse> responseListener,
                     Response.ErrorListener errorListener) {

        super(Method.GET, URL, errorListener);

        this.responseListener = responseListener;
    }

    @Override
    protected Response<APIResponse> parseNetworkResponse(NetworkResponse response) {
        String data;
        try {
            data = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            data = new String(response.data);
        }

        Gson parser = new Gson();
        APIResponse parsedData = parser.fromJson(data, APIResponse.class);

        return Response.success(parsedData, null);
    }

    @Override
    protected void deliverResponse(APIResponse response) {
        responseListener.onResponse(response);
    }
}
