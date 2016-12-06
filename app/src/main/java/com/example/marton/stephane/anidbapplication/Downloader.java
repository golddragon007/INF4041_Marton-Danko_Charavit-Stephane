package com.example.marton.stephane.anidbapplication;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Marton on 2014.11.12..
 */
public class Downloader extends Thread {

    private String url;
    private String path;
    private String id;

    private Handler handler;
    private OnDownloadListener onDownloadListener;

    public Downloader(String url, String path) {
        this(url, path, "-1");
    }

    public Downloader(String url, String path, String id) {
        this.url = url;
        this.path = path;
        this.id = id;
        this.handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void run() {
        try {
            URL sourceURL = new URL(url);
            File targetFile = new File(path);

            URLConnection urlConnection = sourceURL.openConnection();

            // Input and output stream
            InputStream is = urlConnection.getInputStream();
            FileOutputStream fos = new FileOutputStream(targetFile);

            // Copy
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }

            is.close();
            fos.close();

            if (id.equals("-1")) {
                postDownloadSuccess(path);
            }
            else {
                postDownloadImageSuccess(id);
            }
        } catch (IOException e) {
            e.printStackTrace();
            postDownloadFailed(e.getLocalizedMessage());
        } catch (Exception e){
            e.printStackTrace();
            postDownloadFailed(e.getMessage());
        }
    }

    private void postDownloadSuccess(final String text){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (onDownloadListener != null)
                    onDownloadListener.onDownloadSuccess(text);
            }
        });
    }

    private void postDownloadImageSuccess(final String text){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (onDownloadListener != null)
                    onDownloadListener.onDownloadImageSuccess(text);
            }
        });
    }

    private void postDownloadFailed(final String message){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (onDownloadListener != null)
                    onDownloadListener.onDownloadFailed(message);
            }
        });
    }

    private void downloadSuccess(String text){
        if (onDownloadListener != null)
            onDownloadListener.onDownloadSuccess(text);
    }

    private void downloadImageSuccess(String text){
        if (onDownloadListener != null)
            onDownloadListener.onDownloadImageSuccess(text);
    }

    private void downloadFailed(String message){
        if (onDownloadListener != null)
            onDownloadListener.onDownloadFailed(message);
    }

    public String getUrl() {
        return url;
    }

    public String getPath() {
        return path;
    }

    public interface OnDownloadListener {
        void onDownloadSuccess(String text);
        void onDownloadImageSuccess(String text);
        void onDownloadFailed(String message);
    }

    public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
    }
}
