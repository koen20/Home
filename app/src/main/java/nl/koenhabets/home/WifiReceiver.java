package nl.koenhabets.home;

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
                        NotificationCompat.Builder notificationBuilder =
                                new NotificationCompat.Builder(context)
                                        .setSmallIcon(R.drawable.ic_home)
                                        .setPriority(NotificationCompat.PRIORITY_MIN)
                                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                                        .setOngoing(true)
                                        .setStyle(new NotificationCompat.InboxStyle());

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                        notificationManager.notify(989, notificationBuilder.build());
                    }
                    firstTime = false;
                } else {
                    firstTime = true;
                }
            }
        }
    }
}
