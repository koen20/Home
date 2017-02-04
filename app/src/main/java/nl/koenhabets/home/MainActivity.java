package nl.koenhabets.home;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import nl.koenhabets.home.models.APIResponse;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    TextView textView2;
    RequestQueue requestQueue;

    Switch switch1;
    Switch switch2;
    Switch switch3;
    GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);
        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);

        graph = (GraphView) findViewById(R.id.graph);

        switch1 = (Switch) findViewById(R.id.switch1);
        switch2 = (Switch) findViewById(R.id.switch2);
        switch3 = (Switch) findViewById(R.id.switch3);

        final SurvurApi request = new SurvurApi(new Response.Listener<APIResponse>() {
            @Override
            public void onResponse(APIResponse response) {
                textView.setText(String.format(Locale.getDefault(), "%.2f °C", response.getTemperatureInside()));
                textView2.setText(String.format(Locale.getDefault(), "%.2f °C", response.getTemperatureOutside()));
                switch1.setChecked(response.getLightA());
                switch2.setChecked(response.getLightB());
                switch3.setChecked(response.getLightC());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(request);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                requestQueue.add(request);
            }
        }, 0, 5 * 1000);


        APIGraph apiGraphRequest = new APIGraph(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                            new DataPoint(0, 1),
                            new DataPoint(1, 5),
                            new DataPoint(2, 3),
                            new DataPoint(3, 2),
                            new DataPoint(4, 6)
                    });
                    graph.addSeries(series);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());
            }
        });
        requestQueue.add(apiGraphRequest);


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
}