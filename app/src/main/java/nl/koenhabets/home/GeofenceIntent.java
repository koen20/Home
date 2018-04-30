package nl.koenhabets.home;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import nl.koenhabets.home.api.ActionRequest;
import nl.koenhabets.home.api.SurvurApi;
import nl.koenhabets.home.models.APIResponse;


public class GeofenceIntent extends IntentService {

    public GeofenceIntent() {
        super("Geofence");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.i("Geofence", "geofence eroror");
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.i("geofence", "enter");
            createNotification(this);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            ActionRequest request = new ActionRequest("Enter", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            requestQueue.add(request);
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.i("geofence", "leave");
            dismissNotification(this);
        }
    }

    public static void createNotification(final Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        final NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, "my_channel_01")
                        .setSmallIcon(R.drawable.ic_home)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setOngoing(true)
                        .setContentTitle("Home")
                        .setChannelId("my_channel_01");

        Intent intentLightA = new Intent(context, CommandService.class);
        Intent intentLightB = new Intent(context, CommandService.class);
        Intent intentLightC = new Intent(context, CommandService.class);
        Intent intentLedStip = new Intent(context, CommandService.class);


        intentLightA.setAction("lightA");
        intentLightB.setAction("lightB");
        intentLightC.setAction("lightC");
        intentLedStip.setAction("ledStrip");

        PendingIntent pendingIntentLightA = PendingIntent.getService(context, 10, intentLightA, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pendingIntentLightB = PendingIntent.getService(context, 10, intentLightB, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pendingIntentLightC = PendingIntent.getService(context, 10, intentLightC, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pendingIntenLedStrip = PendingIntent.getService(context, 10, intentLedStip, PendingIntent.FLAG_CANCEL_CURRENT);

        notificationBuilder.addAction(R.drawable.ic_light, "A", pendingIntentLightA);
        //notificationBuilder.addAction(R.drawable.ic_light, "B", pendingIntentLightB);
        notificationBuilder.addAction(R.drawable.ic_light, "C", pendingIntentLightC);
        notificationBuilder.addAction(R.drawable.ic_light, "Led", pendingIntenLedStrip);


        final SurvurApi request = new SurvurApi(new Response.Listener<APIResponse>() {
            @Override
            public void onResponse(APIResponse response) {
                notificationBuilder.setContentText("In:" + response.getTemperatureInside() + "/Out:" + response.getTemperatureOutside());

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(989, notificationBuilder.build());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(request);
    }

    public static void dismissNotification(Context context) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.cancel(989);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        ActionRequest request = new ActionRequest("Leave", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(request);
    }
}
