package com.example.marton.stephane.anidbapplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marton on 2016.11.27..
 */

public class Anime extends AnimeListItem {
    private String type;

    private SimilarAnime[] similarAnimes;

    private String description;

    public String getType() {
        return type;
    }

    public SimilarAnime[] getSimilarAnimes() {
        return similarAnimes;
    }

    public String getDescription() {
        return description;
    }

    public Anime(String picURL, String id, String title, String eps, String startDate, String endDate, String ratingTemp, String ratingTempCount, String ratingPerm, String ratingPermCount, boolean restricted, String type, SimilarAnime[] similarAnimes, String description) {
        super(picURL, id, title, eps, startDate, endDate, ratingTemp, ratingTempCount, ratingPerm, ratingPermCount, restricted);
        this.type = type;
        this.similarAnimes = similarAnimes;
        this.description = description;
    }
}
