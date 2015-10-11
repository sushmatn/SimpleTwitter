package com.codepath.apps.SimpleTwitter.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.SimpleTwitter.R;
import com.codepath.apps.SimpleTwitter.TwitterClient;
import com.codepath.apps.SimpleTwitter.activities.TweetDetailActivity;
import com.codepath.apps.SimpleTwitter.helpers.Helper;
import com.codepath.apps.SimpleTwitter.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by SushmaNayak on 9/28/2015.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    private Context mContext;
    TwitterClient mClient;

    final static class ViewHolder {

        ImageView iconRetweeted;
        ImageView ivProfileImg;
        TextView tvBody;
        TextView tvUserName;
        TextView tvScreenName;
        TextView tvTimeStamp;
        ImageView ivMedia;
        TextView tvRetweets;
        TextView tvLikes;
        ImageView ivReply;
        ImageView ivRetweet;
        ImageView ivLike;
        TextView tvRetweetUser;

        void bind(final Tweet tweet, final Context context, final TwitterClient client) {

            if (tweet.wasRetweeted())
                tvRetweetUser.setText(tweet.getReTweetUser() + " Retweeted");

            tvRetweetUser.setVisibility(tweet.wasRetweeted() ? View.VISIBLE : View.GONE);
            iconRetweeted.setVisibility(tweet.wasRetweeted() ? View.VISIBLE : View.GONE);

            tvUserName.setText(tweet.getUser().getName());
            tvScreenName.setText(" @" + tweet.getUser().getScreenName());
            tvTimeStamp.setText(Helper.getRelativeTimeStamp(tweet.getCreatedAt()));
            tvBody.setText(tweet.getTwitterURL() == null ? Html.fromHtml(tweet.getBody()) : Html.fromHtml(tweet.getBody().replace(tweet.getTwitterURL(), "")));

            ivReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.onReply(tweet, (Helper.OnReplyListener) context);
                }
            });

            tvLikes.setText(Integer.toString(tweet.getFavoriteCount()));
            ivLike.setImageResource(tweet.isFavorited() ? R.drawable.ic_like_on : R.drawable.ic_like);
            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Helper.isNetworkAvailable(context)) {
                        if (tweet.isFavorited())
                            Helper.updateButtonStatus((ImageView) v, true, R.drawable.ic_like);
                        else
                            Helper.updateButtonStatus((ImageView) v, true, R.drawable.ic_like_on);
                        Helper.onFavorite(client, tweet, (Helper.OnFavoriteListener) context);
                    }
                }
            });

            tvRetweets.setText(Integer.toString(tweet.getRetweetCount()));
            ivRetweet.setImageResource(tweet.isRetweeted() ? R.drawable.ic_retweet_on : R.drawable.ic_retweet);
            ivRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Helper.isNetworkAvailable(context)) {
                        Helper.updateButtonStatus((ImageView) v, true, R.drawable.ic_retweet_on);
                        Helper.onReTweet(client, tweet, (Helper.OnReTweetListener) context);
                    }
                }
            });

            ivProfileImg.setImageResource(android.R.color.transparent);
            Picasso.with(context).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImg);
            ivProfileImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Helper.isNetworkAvailable(context)) {
                        ((Helper.OnUserProfileClick) context).onDisplayUser(tweet.getUser());
                    }
                }
            });

            ivMedia.setImageResource(android.R.color.transparent);
            if (tweet.getMediaURL() != null) {
                Picasso.with(context).load(tweet.getMediaURL()).error(R.drawable.image_unavailable).into(ivMedia);
                ivMedia.setVisibility(View.VISIBLE);
            } else
                ivMedia.setVisibility(View.GONE);
        }
    }

    public TweetsArrayAdapter(Context context, TwitterClient client, List<Tweet> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
        mContext = context;
        mClient = client;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Tweet tweet = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_tweet, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.iconRetweeted = (ImageView) convertView.findViewById(R.id.iconRetweeted);
            viewHolder.tvRetweetUser = (TextView) convertView.findViewById(R.id.tvRetweetUser);
            viewHolder.ivProfileImg = (ImageView) convertView.findViewById(R.id.ivProfileImg);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
            viewHolder.tvTimeStamp = (TextView) convertView.findViewById(R.id.tvTimeStamp);
            viewHolder.ivMedia = (ImageView) convertView.findViewById(R.id.ivMedia);
            viewHolder.tvRetweets = (TextView) convertView.findViewById(R.id.tvRetweets);
            viewHolder.tvLikes = (TextView) convertView.findViewById(R.id.tvLikes);
            viewHolder.ivReply = (ImageView) convertView.findViewById(R.id.ivReply);
            viewHolder.ivRetweet = (ImageView) convertView.findViewById(R.id.ivRetweet);
            viewHolder.ivLike = (ImageView) convertView.findViewById(R.id.ivLike);

            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TweetDetailActivity.class);
                intent.putExtra("tweet", tweet);
                mContext.startActivity(intent);
            }
        });

        viewHolder.bind(tweet, mContext, mClient);
        return convertView;
    }
}
