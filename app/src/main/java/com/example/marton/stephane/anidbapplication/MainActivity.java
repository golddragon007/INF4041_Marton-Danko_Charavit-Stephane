package com.example.marton.stephane.anidbapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView hot;
    AnimeListItemAdapter adapter;
    int permission;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private String PrefFileName;

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            Toast.makeText(activity, R.string.storage_permission_need, Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 22) {
            verifyStoragePermissions(this);
            permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (android.os.Build.VERSION.SDK_INT <= 22 || permission == PackageManager.PERMISSION_GRANTED) {
            startService(new Intent(this, BackgroundService.class));

            StartDownload();
        }
    }

    private void StartDownload(){
        PrefFileName = "AniDBAppPrefs";

        SharedPreferences settings = getSharedPreferences(PrefFileName, 0);
        Boolean onlyWifiPref = settings.getBoolean("onlyWifi", false);
        boolean downloadEnabled = Connectivity.isConnectedWifi(this) || !Connectivity.isConnectedWifi(this) && !onlyWifiPref;
        CacheSystem cs = new CacheSystem(this, "main", downloadEnabled, Connectivity.isConnected(this), new DatabaseController(this));
        cs.setOnCacheListener(new CacheSystem.OnCacheListener() {
            @Override
            public void onCacheSuccess(final Object obj) {
                if (!isFinishing()) {
                    if (obj instanceof ArrayList<?> && ((ArrayList<?>)obj).get(0) instanceof AnimeListItem) {
                        adapter = new AnimeListItemAdapter((ArrayList<AnimeListItem>) obj, MainActivity.this);

                        hot = (ListView) findViewById(R.id.hot);
                        hot.setAdapter(adapter);
                        hot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                AnimeListItem item = (AnimeListItem) adapter.getItem(position);

                                Intent intent = new Intent(MainActivity.this, AnimeWatchActivity.class);
                                intent.putExtra("animeId", item.getId());
                                startActivity(intent);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCacheImageReady(String id) {
                if (!isFinishing()) {
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onNewHotAnime() {

            }

            @Override
            public void onCacheFailed(final String message) {
                if (!isFinishing()) {
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }
        });

        cs.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, OptionActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
