package com.example.marton.stephane.anidbapplication;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Marton on 2016.11.29..
 */

public class SimilarAnimeAdapter extends BaseAdapter {
    private ArrayList<SimilarAnime> items;

    public SimilarAnimeAdapter(ArrayList<SimilarAnime> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.similar_anime_item, null);
            holder = new ViewHolder();
            holder.titleText = (TextView) convertView.findViewById(R.id.title);
            holder.ratioText = (TextView) convertView.findViewById(R.id.ratio);
            holder.approvalTotalText = (TextView) convertView.findViewById(R.id.approvalTotal);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleText.setText(((SimilarAnime) getItem(position)).getName());
        holder.ratioText.setText(((SimilarAnime) getItem(position)).getRatio() + '%');
        holder.approvalTotalText.setText(((SimilarAnime) getItem(position)).getApproval() + " / " + ((SimilarAnime) getItem(position)).getTotal());

        return convertView;
    }

    /* Helper class for managing items in the list view
     */
    private static class ViewHolder {
        TextView titleText;
        TextView ratioText;
        TextView approvalTotalText;
    }
}
