package com.example.marton.stephane.anidbapplication;

import java.util.Locale;

/**
 * Created by Marton on 2016.11.27..
 */

public class SimilarAnime {
    private String id;
    private String name;
    private String approval;
    private String total;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getApproval() {
        return approval.isEmpty() ? "0" : approval;
    }

    public String getTotal() {
        return total.isEmpty() ? "0" : total;
    }

    public String getRatio() {
        if (total.isEmpty()) {
            return "-";
        }

        return String.format(Locale.getDefault(),"%.2f", Double.parseDouble(approval) / Integer.parseInt(total) * 100);
    }

    public SimilarAnime(String id, String name, String approval, String total) {
        this.id = id;
        this.name = name;
        this.approval = approval;
        this.total = total;
    }
}
