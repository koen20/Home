package nl.koenhabets.home;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import nl.koenhabets.home.models.APIResponse;


public class WifiReceiver extends BroadcastReceiver {
    private static boolean firstTime = true;

    public static void createNotification(final Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        final NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_home)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setOngoing(true)
                        .setContentTitle("Home");

        Intent intentLightA = new Intent(context, CommandService.class);
        Intent intentLightB = new Intent(context, CommandService.class);
        Intent intentLightC = new Intent(context, CommandService.class);

        intentLightA.setAction("lightA");
        intentLightB.setAction("lightB");
        intentLightC.setAction("lightC");

        PendingIntent pendingIntentLightA = PendingIntent.getService(context, 10, intentLightA, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pendingIntentLightB = PendingIntent.getService(context, 10, intentLightB, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pendingIntentLightC = PendingIntent.getService(context, 10, intentLightC, PendingIntent.FLAG_CANCEL_CURRENT);

        notificationBuilder.addAction(R.drawable.ic_light, "A", pendingIntentLightA);
        notificationBuilder.addAction(R.drawable.ic_light, "B", pendingIntentLightB);
        notificationBuilder.addAction(R.drawable.ic_light, "C", pendingIntentLightC);

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

    private static void dismissNotification(Context context) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.cancel(989);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (info != null) {

            if (info.isConnected()) {
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ssid = wifiInfo.getSSID();

                if (ssid != null && (ssid.contains("Henk de Router"))) {
                    if (firstTime) {
                        Log.i("wifi", "Connected to Henk.");
                        createNotification(context);
                    }
                    firstTime = false;
                } else {
                    firstTime = true;
                    dismissNotification(context);
                }
            } else {
                dismissNotification(context);
            }
        }
    }
}
