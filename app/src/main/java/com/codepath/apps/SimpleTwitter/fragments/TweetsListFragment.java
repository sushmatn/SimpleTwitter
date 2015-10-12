package com.codepath.apps.SimpleTwitter.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.codepath.apps.SimpleTwitter.R;
import com.codepath.apps.SimpleTwitter.TwitterApplication;
import com.codepath.apps.SimpleTwitter.TwitterClient;
import com.codepath.apps.SimpleTwitter.adapters.TweetsArrayAdapter;
import com.codepath.apps.SimpleTwitter.helpers.EndlessScrollListener;
import com.codepath.apps.SimpleTwitter.helpers.Helper;
import com.codepath.apps.SimpleTwitter.helpers.NestedListView;
import com.codepath.apps.SimpleTwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TweetsListFragment extends ListFragment {

    static final int FIRST_PAGE = 1;
    static long maxID = 1;

    public enum TweetListType {
        HOME_TIMELINE, MENTIONS_TIMELINE, USER_TIMELINE, FAVORITES_LIST, SEARCHED_TWEETS;
    }

    TweetsArrayAdapter aTweets;
    ArrayList<Tweet> tweets;
    ListView lvTweets;
    SwipeRefreshLayout swipeContainer;
    TwitterClient client;
    TweetListType tweetListType;
    MenuItem miActionProgressItem;
    String mResultsParameter;

    public TweetsListFragment() {
        // Required empty public constructor
    }

    public static TweetsListFragment newInstance(TweetListType tweetListType) {

        TweetsListFragment fragment = new TweetsListFragment();
        Bundle args = new Bundle();
        args.putInt(Helper.LIST_TYPE, tweetListType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    public static TweetsListFragment newInstance(String screenName, TweetListType tweetListType) {
        Bundle args = new Bundle();
        args.putInt(Helper.LIST_TYPE, tweetListType.ordinal());
        args.putString(Helper.SCREEN_NAME, screenName);
        TweetsListFragment fragment = new TweetsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        setupViews(view);
        mResultsParameter = getArguments().getString(Helper.SCREEN_NAME);
        populateList(FIRST_PAGE);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        lvTweets = (ListView) getListView();
        lvTweets.setOnScrollListener(scrollChangedListener);
        lvTweets.setAdapter(aTweets);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = TwitterApplication.getRestClient();
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(getContext(), client, tweets);
        tweetListType = TweetListType.values()[getArguments().getInt(Helper.LIST_TYPE)];
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_tweetlist, menu);
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
    }

    private void setupViews(View view) {

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTimeLine();
            }
        });
    }

    private AbsListView.OnScrollListener scrollChangedListener = new EndlessScrollListener() {
        @Override
        public boolean onLoadMore(int page, int totalItemsCount) {
            populateList(maxID);
            return true;
        }
    };

    private void refreshTimeLine() {
        aTweets.clear();
        populateList(FIRST_PAGE);
    }

    public void populateList(long max_id) {
        if (!Helper.isNetworkAvailable(getActivity())) {
            fetchTweetsFromDB();
            return;
        }

        showProgressBar();
        if (tweetListType == TweetListType.HOME_TIMELINE)
            client.getHomeTimeLine(max_id, handler);
        else if (tweetListType == TweetListType.MENTIONS_TIMELINE)
            client.getMentionsTimeLine(max_id, handler);
        else if (tweetListType == TweetListType.USER_TIMELINE)
            client.getUserTimeLine(mResultsParameter, max_id, handler);
        else if (tweetListType == TweetListType.FAVORITES_LIST)
            client.getFavoritesList(mResultsParameter, max_id, handler);
        else if (tweetListType == TweetListType.SEARCHED_TWEETS)
            client.SearchTweets(mResultsParameter, max_id, handler);
    }

    JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONArray response) {
            ArrayList<Tweet> tweetArrayList = Tweet.fromJSONArray(response);
            aTweets.addAll(tweetArrayList);
            if (tweetArrayList.size() > 0) {
                Tweet mostRecentTweet = tweetArrayList.get(tweetArrayList.size() - 1);
                maxID = mostRecentTweet.getTweetId();
            }
            swipeContainer.setRefreshing(false);
            hideProgressBar();
        }

        public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
            try {
                JSONArray jsonArray = response.getJSONArray("statuses");
                ArrayList<Tweet> tweetArrayList = Tweet.fromJSONArray(jsonArray);
                aTweets.addAll(tweetArrayList);
                if (tweetArrayList.size() > 0) {
                    Tweet mostRecentTweet = tweetArrayList.get(tweetArrayList.size() - 1);
                    maxID = mostRecentTweet.getTweetId();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            swipeContainer.setRefreshing(false);
            hideProgressBar();
        }

        @Override
        public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONArray errorResponse) {
            Log.v("Twitter", errorResponse.toString());
            swipeContainer.setRefreshing(false);
            hideProgressBar();
        }
    };

    public void showProgressBar() {
        if (miActionProgressItem != null)
            miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        if (miActionProgressItem != null)
            miActionProgressItem.setVisible(false);
    }

    public void onReTweet(final Tweet tweet, Tweet newTweet) {

        int position = aTweets.getPosition(tweet);

        // Update the tweet with the new info returned
        tweet.setRetweetCount(newTweet.getRetweetCount());
        tweet.setRetweeted(newTweet.isRetweeted());

        aTweets.remove(tweet);
        aTweets.insert(tweet, position);
    }

    public void onSearch(String query) {
        aTweets.clear();
        mResultsParameter = query;
        tweetListType = TweetListType.SEARCHED_TWEETS;
        populateList(FIRST_PAGE);
    }

    public void onFavorite(final Tweet tweet, Tweet newTweet) {
        int position = aTweets.getPosition(tweet);
        aTweets.remove(tweet);
        aTweets.insert(newTweet, position);
    }

    public void onTweetSent(Tweet tweet) {
        aTweets.insert(tweet, 0);
    }

    private void fetchTweetsFromDB() {
        aTweets.clear();
        List<Tweet> savedTweets = new Select().from(Tweet.class)
                .orderBy("tweetId DESC")
                .execute();
        aTweets.addAll(savedTweets);
    }
}
