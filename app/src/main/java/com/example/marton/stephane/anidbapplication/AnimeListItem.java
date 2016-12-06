package com.example.marton.stephane.anidbapplication;

import android.net.Uri;

import java.net.URI;

/**
 * Created by Marton on 2016.11.25..
 */

public class AnimeListItem {
    private String picURL;
    private String id;
    private String title;
    private String eps;
    private String startDate;
    private String endDate;
    private String ratingTemp;
    private String ratingTempCount;
    private String ratingPerm;
    private String ratingPermCount;
    private boolean restricted;

    public String getPicURL() {
        return picURL;
    }
    public Uri getPicURI() {
        return Uri.parse(picURL);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getEps() {
        return eps;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getRatingTemp() {
        return ratingTemp;
    }

    public String getRatingTempCount() {
        return ratingTempCount;
    }

    public String getRatingPerm() {
        return ratingPerm;
    }

    public String getRatingPermCount() {
        return ratingPermCount;
    }

    public boolean isRestricted() {
        return restricted;
    }

    public String getDates() {
        return startDate + " - " + endDate;
    }

    public String getFullEps() {
        return eps;
    }
    public String getFullRatingPerm () {
        return ratingPerm + "/10 (" + ratingPermCount + ")";
    }

    public String getFullRatingTemp () {
        return ratingTemp + "/10 (" + ratingTempCount + ")";
    }

    public AnimeListItem(String picURL, String id, String title, String eps, String startDate, String endDate, String ratingTemp, String ratingTempCount, String ratingPerm, String ratingPermCount, boolean restricted) {
        this.picURL = picURL;
        this.id = id;
        this.title = title;
        this.eps = eps;
        this.startDate = startDate;
        this.endDate = endDate;
        this.ratingTemp = ratingTemp;
        this.ratingTempCount = ratingTempCount;
        this.ratingPerm = ratingPerm;
        this.ratingPermCount = ratingPermCount;
        this.restricted = restricted;
    }
}
