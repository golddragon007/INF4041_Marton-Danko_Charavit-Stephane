package com.example.marton.stephane.anidbapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Marton on 2016.11.25..
 */

public class CacheSystem extends Thread {
    private String op;
    private String id;
    private boolean downloadEnabled, hasInternetConnection;
    String baseUrl = "http://api.anidb.net:9001/httpapi?client=anidbapplethttp&clientver=1&protover=1&request=";

    private Handler handler;
    private OnCacheListener onCacheListener;

    private DatabaseController dc;
    private Context context;

    public CacheSystem(Context context, String op, boolean downloadEnabled, boolean hasInternetConnection, DatabaseController dc) {
        this.op = op;
        this.downloadEnabled = downloadEnabled;
        this.hasInternetConnection = hasInternetConnection;
        this.handler = new Handler(Looper.getMainLooper());
        this.dc = dc;
        this.context = context;
    }

    public CacheSystem(Context context, String op, boolean downloadEnabled, boolean hasInternetConnection, DatabaseController dc, String id) {
        this(context, op, downloadEnabled, hasInternetConnection, dc);
        this.id = id;
    }

    @Override
    public void run() {
        if (!hasInternetConnection) {
            postCacheFailed(context.getString(R.string.error_no_internet_connection));
        }

        if (op.equals("main")) {
            runMain();
        }
        else if (op.equals("anime")) {
            runAnime();
        }
    }

    private void runAnime() {
        File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/AniDBApp/Anime");
        dir.mkdirs();
        String path = Environment.getExternalStorageDirectory().getPath() + "/AniDBApp/Anime/" + id + ".xml";
        File main = new File(path);

        Calendar c = Calendar.getInstance();
        long diffMin = (c.getTimeInMillis() - main.lastModified()) / 1000 / 60;

        if (diffMin > 30 && hasInternetConnection && downloadEnabled) {
            // If the file is older than 30 mins.
            String url = this.baseUrl + this.op + "&aid=" + id;

            Downloader downloader = new Downloader(url, path);
            downloader.setOnDownloadListener(new Downloader.OnDownloadListener() {
                @Override
                public void onDownloadSuccess(final String text) {
                    getLastAnime(text);
                }

                @Override
                public void onDownloadImageSuccess(String text) {

                }

                @Override
                public void onDownloadFailed(final String message) {
                    postCacheFailed(message);
                }
            });

            downloader.start();
        }
        else {
            if (downloadEnabled && !hasInternetConnection) {
                postCacheFailed(context.getString(R.string.warning_data_from_cache));
            }

            getLastAnime(path);
        }
    }

    private void runMain() {
        File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/AniDBApp");
        dir.mkdirs();
        String path = Environment.getExternalStorageDirectory().getPath() + "/AniDBApp/main.xml";
        File main = new File(path);

        Calendar c = Calendar.getInstance();
        long diffMin = (c.getTimeInMillis() - main.lastModified()) / 1000 / 60;

        if (diffMin > 30) {
            // If the file is older than 30 mins.
            String url = this.baseUrl + this.op;

            Downloader downloader = new Downloader(url, path);
            downloader.setOnDownloadListener(new Downloader.OnDownloadListener() {
                @Override
                public void onDownloadSuccess(final String text) {
                    getLastMain(text);
                }

                @Override
                public void onDownloadImageSuccess(String text) {

                }

                @Override
                public void onDownloadFailed(final String message) {
                    postCacheFailed(message);
                }
            });

            downloader.start();
        }
        else {
            getLastMain(path);
        }
    }

    private void getLastAnime(String path) {
        File targetFile = new File(path);
        //Read text from file
        StringBuilder text = new StringBuilder();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(targetFile));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();

            Document doc = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            dbf.setCoalescing(true);
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();

                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(text.toString()));
                doc = db.parse(is);

                Anime anime;
                String baseImagePath = Environment.getExternalStorageDirectory().getPath() + "/AniDBApp/image/";
                File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/AniDBApp/image");
                dir.mkdirs();

                Element e = doc.getDocumentElement();
                NodeList eNl = e.getElementsByTagName("error");
                String error = "";

                if (eNl.getLength() > 0) {
                    error = eNl.item(0).getTextContent();
                    postCacheFailed(error);
                }
                else {
                    String imagePath = "";
                    if (e.getElementsByTagName("picture").getLength() > 0) {
                        imagePath = baseImagePath + e.getElementsByTagName("picture").item(0).getTextContent();
                    }

                    String endDate = "";

                    if (e.getElementsByTagName("enddate").getLength() > 0) {
                        endDate = e.getElementsByTagName("enddate").item(0).getTextContent();
                    }

                    String ratingPermRate = "-";
                    String ratingPermCount = "0";
                    String ratingTermRate = "-";
                    String ratingTermCount = "0";
                    if (e.getElementsByTagName("ratings").getLength() > 0) {
                        Element ratings = (Element) e.getElementsByTagName("ratings").item(0);

                        if (ratings.getElementsByTagName("permanent").getLength() > 0) {
                            Element perm = (Element) ratings.getElementsByTagName("permanent").item(0);

                            ratingPermRate = perm.getTextContent();
                            ratingPermCount = perm.getAttribute("count");
                        }
                        if (ratings.getElementsByTagName("temporary").getLength() > 0) {
                            Element temp = (Element) ratings.getElementsByTagName("temporary").item(0);

                            ratingTermRate = temp.getTextContent();
                            ratingTermCount = temp.getAttribute("count");
                        }
                    }

                    String title = "";
                    NodeList titleNl = e.getElementsByTagName("title");
                    for (int i = 0; i < titleNl.getLength(); i++) {
                        Element curTitle = (Element) titleNl.item(i);
                        if (curTitle.getAttribute("type").equals("main")) {
                            title = curTitle.getTextContent();
                            break;
                        }
                    }

                    NodeList similarAnimeNl = e.getElementsByTagName("similaranime");
                    SimilarAnime[] similarAnime = new SimilarAnime[similarAnimeNl.getLength()];
                    if (similarAnimeNl.getLength() > 0) {
                        NodeList simiralAnimeNlAnimeNl = ((Element) similarAnimeNl.item(0)).getElementsByTagName("anime");
                        similarAnime = new SimilarAnime[simiralAnimeNlAnimeNl.getLength()];
                        for (int i = 0; i < simiralAnimeNlAnimeNl.getLength(); i++) {
                            Element curSimilar = (Element) simiralAnimeNlAnimeNl.item(i);
                            similarAnime[i] = new SimilarAnime(
                                    curSimilar.getAttribute("id"),
                                    curSimilar.getTextContent(),
                                    curSimilar.getAttribute("approval"),
                                    curSimilar.getAttribute("total")
                            );
                        }
                    }

                    anime = new Anime(
                            imagePath,
                            e.getAttribute("id"),
                            title,
                            e.getElementsByTagName("episodecount").item(0).getTextContent(),
                            e.getElementsByTagName("startdate").item(0).getTextContent(),
                            endDate,
                            ratingPermRate,
                            ratingPermCount,
                            ratingTermRate,
                            ratingTermCount,
                            Boolean.parseBoolean(e.getAttribute("restricted")),
                            e.getElementsByTagName("type").item(0).getTextContent(),
                            similarAnime,
                            e.getElementsByTagName("description").item(0).getTextContent()
                    );

                    File main = new File(imagePath);

                    Calendar c = Calendar.getInstance();
                    long diffMin = (c.getTimeInMillis() - main.lastModified()) / 1000 / 60;
                    if (diffMin > 30) {
                        String url = "http://img7.anidb.net/pics/anime/" + e.getElementsByTagName("picture").item(0).getTextContent();
                        Downloader downloader = new Downloader(url, imagePath, "0");
                        downloader.setOnDownloadListener(new Downloader.OnDownloadListener() {
                            @Override
                            public void onDownloadSuccess(final String text) {

                            }

                            @Override
                            public void onDownloadImageSuccess(String text) {
                                postCacheImageReady(text);
                            }

                            @Override
                            public void onDownloadFailed(final String message) {
                                postCacheFailed(message);
                            }
                        });

                        downloader.start();
                    }

                    postCacheSuccess(anime);
                }
            } catch (ParserConfigurationException | IOException | SAXException e) {
                e.printStackTrace();
                postCacheFailed(e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            postCacheFailed(e.getMessage());
        }
    }

    private void getLastMain(String path) {
        File targetFile = new File(path);
        //Read text from file
        StringBuilder text = new StringBuilder();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(targetFile));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();

            Document doc = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            dbf.setCoalescing(true);
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();

                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(text.toString()));
                doc = db.parse(is);

                Element err = doc.getDocumentElement();
                NodeList eNl = err.getElementsByTagName("error");
                String error = "";

                if (eNl.getLength() > 0) {
                    error = eNl.item(0).getTextContent();
                    postCacheFailed(error);
                }
                else {
                    NodeList nl = doc.getElementsByTagName("hotanime").item(0).getChildNodes();

                    ArrayList<AnimeListItem> al = new ArrayList<AnimeListItem>();
                    String baseImagePath = Environment.getExternalStorageDirectory().getPath() + "/AniDBApp/image/";
                    File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/AniDBApp/image");
                    dir.mkdirs();

                    for (int i = 0; i < nl.getLength(); i++) {
                        Node n = nl.item(i);

                        if (n.getNodeType() == Node.ELEMENT_NODE) {
                            Element e = (Element) n;

                            String endDate = "";

                            if (e.getElementsByTagName("enddate").getLength() > 0) {
                                endDate = e.getElementsByTagName("enddate").item(0).getTextContent();
                            }

                            String ratingPermRate = "-";
                            String ratingPermCount = "0";
                            String ratingTermRate = "-";
                            String ratingTermCount = "0";
                            if (e.getElementsByTagName("ratings").getLength() > 0) {
                                Element ratings = (Element) e.getElementsByTagName("ratings").item(0);

                                if (ratings.getElementsByTagName("permanent").getLength() > 0) {
                                    Element perm = (Element) ratings.getElementsByTagName("permanent").item(0);

                                    ratingPermRate = perm.getTextContent();
                                    ratingPermCount = perm.getAttribute("count");
                                }
                                if (ratings.getElementsByTagName("temporary").getLength() > 0) {
                                    Element temp = (Element) ratings.getElementsByTagName("temporary").item(0);

                                    ratingTermRate = temp.getTextContent();
                                    ratingTermCount = temp.getAttribute("count");
                                }
                            }


                            al.add(new AnimeListItem(
                                    baseImagePath + e.getElementsByTagName("picture").item(0).getTextContent(),
                                    e.getAttribute("id"),
                                    e.getElementsByTagName("title").item(0).getTextContent(),
                                    e.getElementsByTagName("episodecount").item(0).getTextContent(),
                                    e.getElementsByTagName("startdate").item(0).getTextContent(),
                                    endDate,
                                    ratingPermRate,
                                    ratingPermCount,
                                    ratingTermRate,
                                    ratingTermCount,
                                    Boolean.parseBoolean(e.getAttribute("restricted"))
                            ));

                            if (dc.isNew(al)) {
                                postNewHotAnime();
                                dc.addUploadFiles(al);
                            }

                            String imagePath = baseImagePath + e.getElementsByTagName("picture").item(0).getTextContent();
                            File main = new File(imagePath);

                            Calendar c = Calendar.getInstance();
                            long diffMin = (c.getTimeInMillis() - main.lastModified()) / 1000 / 60;
                            if (diffMin > 30) {
                                String url = "http://img7.anidb.net/pics/anime/" + e.getElementsByTagName("picture").item(0).getTextContent();
                                Downloader downloader = new Downloader(url, imagePath, String.valueOf(i));
                                downloader.setOnDownloadListener(new Downloader.OnDownloadListener() {
                                    @Override
                                    public void onDownloadSuccess(final String text) {

                                    }

                                    @Override
                                    public void onDownloadImageSuccess(String text) {
                                        postCacheImageReady(text);
                                    }

                                    @Override
                                    public void onDownloadFailed(final String message) {
                                        postCacheFailed(message);
                                    }
                                });

                                downloader.start();
                            }
                        }
                    }

                    postCacheSuccess(al);
                }
            } catch (ParserConfigurationException | IOException | SAXException e) {
                e.printStackTrace();
                postCacheFailed(e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            postCacheFailed(e.getMessage());
        }
    }

    private void postCacheSuccess(final Object text){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (onCacheListener != null)
                    onCacheListener.onCacheSuccess(text);
            }
        });
    }

    private void postCacheImageReady(final String id){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (onCacheListener != null)
                    onCacheListener.onCacheImageReady(id);
            }
        });
    }

    private void postNewHotAnime(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (onCacheListener != null)
                    onCacheListener.onNewHotAnime();
            }
        });
    }

    private void postCacheFailed(final String message){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (onCacheListener != null)
                    onCacheListener.onCacheFailed(message);
            }
        });
    }

    private void cacheSuccess(Object text){
        if (onCacheListener != null)
            onCacheListener.onCacheSuccess(text);
    }

    private void cacheImageReady(String id){
        if (onCacheListener != null)
            onCacheListener.onCacheImageReady(id);
    }

    private void newHotAnime(){
        if (onCacheListener != null)
            onCacheListener.onNewHotAnime();
    }

    private void cacheFailed(String message){
        if (onCacheListener != null)
            onCacheListener.onCacheFailed(message);
    }

    public interface OnCacheListener {
        void onCacheSuccess(Object text);
        void onCacheImageReady(String id);
        void onNewHotAnime();
        void onCacheFailed(String message);
    }

    public void setOnCacheListener(OnCacheListener onDownloadListener) {
        this.onCacheListener = onDownloadListener;
    }
}
