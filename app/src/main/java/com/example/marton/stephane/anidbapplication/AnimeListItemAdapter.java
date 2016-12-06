package com.example.marton.stephane.anidbapplication;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Marton on 2016.11.25..
 */

public class AnimeListItemAdapter extends BaseAdapter {

    private ArrayList<AnimeListItem> items;
    private Context context;

    public AnimeListItemAdapter(ArrayList<AnimeListItem> items, Context context) {
        this.items = items;
        this.context = context;
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
            convertView = View.inflate(parent.getContext(), R.layout.anime_list_item, null);
            holder = new ViewHolder();
            holder.cover = (ImageView) convertView.findViewById(R.id.cover);
            holder.titleText = (TextView) convertView.findViewById(R.id.title);
            holder.datesText = (TextView) convertView.findViewById(R.id.dates);
            holder.epsText = (TextView) convertView.findViewById(R.id.eps);
            holder.ratingTempText = (TextView) convertView.findViewById(R.id.ratingTemp);
            holder.ratingPermText = (TextView) convertView.findViewById(R.id.ratingPerm);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.cover.setImageURI(((AnimeListItem) getItem(position)).getPicURI());
        holder.titleText.setText(((AnimeListItem) getItem(position)).getTitle());
        holder.datesText.setText(((AnimeListItem) getItem(position)).getDates());
        holder.epsText.setText(context.getString(R.string.episodes) + ((AnimeListItem) getItem(position)).getFullEps());
        holder.ratingPermText.setText(context.getString(R.string.permanent_rating) + ((AnimeListItem) getItem(position)).getFullRatingPerm());
        holder.ratingTempText.setText(context.getString(R.string.temporary_rating) + ((AnimeListItem) getItem(position)).getFullRatingTemp());

        return convertView;
    }

    /* Helper class for managing items in the list view
     */
    private static class ViewHolder {
        ImageView cover;
        TextView titleText;
        TextView datesText;
        TextView epsText;
        TextView ratingTempText;
        TextView ratingPermText;
    }
}
