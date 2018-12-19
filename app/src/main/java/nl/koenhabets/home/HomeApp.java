package nl.koenhabets.home;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class HomeApp extends AppCompatActivity {
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
    }
}
