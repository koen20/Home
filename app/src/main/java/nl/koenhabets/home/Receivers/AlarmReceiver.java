package nl.koenhabets.home.Receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Alarm", "Started AlarmReceiver");
        String ssid = null;
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo;

        wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            ssid = wifiInfo.getSSID();
        }

        if (ssid != null && (ssid.contains("Henk de Router"))) {
            Log.i("Alarm", "Connected to Henk");
            WifiReceiver.createNotification(context);
        }
    }
}
