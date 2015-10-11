package com.codepath.apps.SimpleTwitter.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.codepath.apps.SimpleTwitter.R;
import com.codepath.apps.SimpleTwitter.TwitterApplication;
import com.codepath.apps.SimpleTwitter.TwitterClient;
import com.codepath.apps.SimpleTwitter.adapters.PhotoArrayAdapter;
import com.codepath.apps.SimpleTwitter.helpers.EndlessScrollListener;
import com.codepath.apps.SimpleTwitter.helpers.Helper;
import com.codepath.apps.SimpleTwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotosFragment extends Fragment {

    static final int FIRST_PAGE = 1;
    static long maxID = 1;
    GridView gvPhotos;
    PhotoArrayAdapter aPhotos;
    ArrayList<Tweet> tweets;
    TwitterClient client;

    public PhotosFragment() {
        // Required empty public constructor
    }

    public static PhotosFragment newInstance(String screenName) {
        PhotosFragment fragment = new PhotosFragment();
        Bundle args = new Bundle();
        args.putString(Helper.SCREEN_NAME, screenName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        tweets = new ArrayList<>();
        aPhotos = new PhotoArrayAdapter(getContext(), tweets);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        gvPhotos = (GridView) view.findViewById(R.id.gvPhotos);
        gvPhotos.setOnScrollListener(scrollListener);

        gvPhotos.setAdapter(aPhotos);
        populatePhotos(FIRST_PAGE);
        return view;
    }

    EndlessScrollListener scrollListener = new EndlessScrollListener() {
        @Override
        public boolean onLoadMore(int page, int totalItemsCount) {
            populatePhotos(maxID);
            return true;
        }
    };

    public void populatePhotos(long max_id) {
        if (!Helper.isNetworkAvailable(getActivity())) {
            return;
        }
        client.getUserPhotos(getArguments().getString(Helper.SCREEN_NAME), max_id, handler);
    }

    JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONArray response) {
            ArrayList<Tweet> tweetArrayList = Tweet.fromJSONArray(response);
            if (tweetArrayList.size() > 0) {
                Tweet mostRecentTweet = tweetArrayList.get(tweetArrayList.size() - 1);
                maxID = mostRecentTweet.getTweetId();
            }

            ArrayList<Tweet> toRemove = new ArrayList<>();
            for (Tweet tweet : tweetArrayList) {
                if (tweet.getMediaURL() == null)
                    toRemove.add(tweet);
            }
            tweetArrayList.removeAll(toRemove);
            aPhotos.addAll(tweetArrayList);
        }

        @Override
        public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONArray errorResponse) {
            Log.v("Twitter", errorResponse.toString());
        }
    };
}