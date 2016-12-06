package com.example.marton.stephane.anidbapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.*;
import android.os.*;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

public class BackgroundService extends Service {

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    private String PrefFileName;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        PrefFileName = "AniDBAppPrefs";

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                SharedPreferences settings = getSharedPreferences(PrefFileName, 0);
                Boolean onlyWifiPref = settings.getBoolean("onlyWifi", false);
                boolean downloadEnabled = Connectivity.isConnectedWifi(context) || !Connectivity.isConnectedWifi(context) && !onlyWifiPref;
                CacheSystem cs = new CacheSystem(context, "main", downloadEnabled, Connectivity.isConnected(context), new DatabaseController(context));

                cs.setOnCacheListener(new CacheSystem.OnCacheListener() {
                    @Override
                    public void onCacheSuccess(Object text) {
                    }

                    @Override
                    public void onCacheImageReady(String id) {
                    }

                    @Override
                    public void onNewHotAnime() {
                        notifyUser();
                    }

                    @Override
                    public void onCacheFailed(String message) {
                    }
                });
                cs.start();

                handler.postDelayed(runnable, 60000*30);
            }
        };

        handler.postDelayed(runnable, 60000*30);
    }

    private void notifyUser() {
        Intent intent = new Intent();
        intent.setAction("com.example.marton.stephane.anidbapplication.NEW_HOT_ANIME");
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //code for runnable
        return START_NOT_STICKY;
    }
}
