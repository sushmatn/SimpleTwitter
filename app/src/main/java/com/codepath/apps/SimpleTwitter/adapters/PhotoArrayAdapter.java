package com.codepath.apps.SimpleTwitter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.codepath.apps.SimpleTwitter.R;
import com.codepath.apps.SimpleTwitter.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SushmaNayak on 10/6/2015.
 */
public class PhotoArrayAdapter  extends ArrayAdapter<Tweet> {

    Context mContext;
    public PhotoArrayAdapter(Context context, List<Tweet> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet tweet = getItem(position);
        if(convertView == null)
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_image, parent, false);
        ImageView ivImage = (ImageView)convertView.findViewById(R.id.ivImage);
        Picasso.with(getContext()).load(tweet.getMediaURL()).placeholder(R.drawable.loading).error(R.drawable.image_unavailable).into(ivImage);
        return convertView;
    }
}
