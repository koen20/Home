package nl.koenhabets.home;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class WifiReceiver extends BroadcastReceiver {
    private static boolean firstTime = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Wifi", "wifireceiver");
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
                }
            }
        }
    }

    private static void createNotification(Context context) {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_home)
                        .setPriority(NotificationCompat.PRIORITY_MIN)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setOngoing(true)
                        .setStyle(new NotificationCompat.InboxStyle());

        Intent intentLightA = new Intent(context, CommandService.class);
        intentLightA.setAction("lightA");
        PendingIntent pendingIntentLightA = PendingIntent.getService(context, 10, intentLightA, 0);
        notificationBuilder.addAction(R.drawable.ic_home, "Light-A", pendingIntentLightA);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(989, notificationBuilder.build());
    }

    private static void dismissNotification(Context context) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.cancel(989);
    }
}
