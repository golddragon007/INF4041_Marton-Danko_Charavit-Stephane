package com.example.marton.stephane.anidbapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class AnimeWatchActivity extends AppCompatActivity {
    TextView title, eps, dates, type, description, ratingTemp, ratingPerm, similarText;
    ImageView cover;
    MyListView similarList;
    SimilarAnimeAdapter adapter;
    Button openInBrowser;
    Uri imageUri;
    private String PrefFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_watch);

        Intent intent = getIntent();
        final String id = intent.getStringExtra("animeId");

        cover = (ImageView) this.findViewById(R.id.cover);
        title = (TextView) this.findViewById(R.id.title);
        eps = (TextView) this.findViewById(R.id.eps);
        dates = (TextView) this.findViewById(R.id.dates);
        type = (TextView) this.findViewById(R.id.type);
        description = (TextView) this.findViewById(R.id.description);
        ratingPerm = (TextView) this.findViewById(R.id.ratingPerm);
        ratingTemp = (TextView) this.findViewById(R.id.ratingTemp);
        similarList = (MyListView) this.findViewById(R.id.similarList);
        similarText = (TextView) this.findViewById(R.id.similarText);
        openInBrowser = (Button) this.findViewById(R.id.openInBrowser);

        if (id.isEmpty()) {
            Toast.makeText(AnimeWatchActivity.this, R.string.error_id_missing, Toast.LENGTH_LONG).show();
        }
        else {
            openInBrowser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://anidb.net/perl-bin/animedb.pl?show=anime&aid=" + id));
                    startActivity(browserIntent);
                }
            });
            StartDownload(id);
        }
    }

    private void StartDownload(String id) {
        PrefFileName = "AniDBAppPrefs";

        SharedPreferences settings = getSharedPreferences(PrefFileName, 0);
        Boolean onlyWifiPref = settings.getBoolean("onlyWifi", false);
        boolean downloadEnabled = Connectivity.isConnectedWifi(this) || !Connectivity.isConnectedWifi(this) && !onlyWifiPref;
        CacheSystem cs = new CacheSystem(this, "anime", downloadEnabled, Connectivity.isConnected(this), new DatabaseController(this), id);
        cs.setOnCacheListener(new CacheSystem.OnCacheListener() {
            @Override
            public void onCacheSuccess(final Object object) {
                if (!isFinishing()) {
                    if (object instanceof Anime) {
                        Anime a = (Anime) object;
                        imageUri = a.getPicURI();
                        cover.setImageURI(imageUri);
                        title.setText(a.getTitle());
                        eps.setText(getString(R.string.episodes) + a.getFullEps());
                        dates.setText(a.getDates());
                        type.setText(a.getType());
                        description.setText(a.getDescription());
                        ratingPerm.setText(getString(R.string.permanent_rating) + a.getFullRatingPerm());
                        ratingTemp.setText(getString(R.string.temporary_rating) + a.getFullRatingTemp());

                        ArrayList<SimilarAnime> sa = new ArrayList<SimilarAnime>(Arrays.asList(a.getSimilarAnimes()));

                        if (!sa.isEmpty()) {
                            similarText.setText(R.string.similar_anime);
                        }

                        adapter = new SimilarAnimeAdapter(sa);

                        similarList.setAdapter(adapter);
                        similarList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                SimilarAnime item = (SimilarAnime) adapter.getItem(position);

                                Intent intent = new Intent(AnimeWatchActivity.this, AnimeWatchActivity.class);
                                intent.putExtra("animeId", item.getId());
                                startActivity(intent);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCacheImageReady(final String id) {
                if (!isFinishing()) {
                    cover.setImageURI(null);
                    cover.setImageURI(imageUri);
                }
            }

            @Override
            public void onNewHotAnime() {

            }

            @Override
            public void onCacheFailed(final String message) {
                if (!isFinishing()) {
                    Toast.makeText(AnimeWatchActivity.this, message, Toast.LENGTH_LONG).show();
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
            Intent intent = new Intent(AnimeWatchActivity.this, OptionActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
