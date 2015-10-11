package com.codepath.apps.SimpleTwitter.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.SimpleTwitter.R;
import com.codepath.apps.SimpleTwitter.TwitterApplication;
import com.codepath.apps.SimpleTwitter.TwitterClient;
import com.codepath.apps.SimpleTwitter.adapters.TweetsArrayAdapter;
import com.codepath.apps.SimpleTwitter.fragments.ComposeTweetFragment;
import com.codepath.apps.SimpleTwitter.fragments.TweetsListFragment;
import com.codepath.apps.SimpleTwitter.helpers.EndlessScrollListener;
import com.codepath.apps.SimpleTwitter.helpers.Helper;
import com.codepath.apps.SimpleTwitter.models.Tweet;
import com.codepath.apps.SimpleTwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity
        implements Helper.OnReTweetListener, Helper.OnFavoriteListener,
        Helper.OnReplyListener, Helper.OnTweetListener, Helper.OnUserProfileClick {

    TwitterClient client;
    TweetsPagerAdapter adapter;
    ViewPager viewPager;
    PagerSlidingTabStrip tabStrip;
    String mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mQuery = getIntent().getStringExtra(Helper.SCREEN_NAME);
        client = TwitterApplication.getRestClient();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new TweetsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(viewPager);

        // Display the logo
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setLogo(getResources().getDrawable(R.mipmap.ic_launcher));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search Results for " + mQuery);

    }

    @Override
    public void onReTweet(Tweet tweet, Tweet newTweet) {

        TweetsListFragment fragmentTweetsList = (TweetsListFragment) adapter.instantiateItem(viewPager, viewPager.getCurrentItem());
        fragmentTweetsList.onReTweet(tweet, newTweet);
    }

    @Override
    public void onFavorite(final Tweet tweet, Tweet newTweet) {
        TweetsListFragment fragmentTweetsList = (TweetsListFragment) adapter.instantiateItem(viewPager, viewPager.getCurrentItem());
        fragmentTweetsList.onFavorite(tweet, newTweet);
    }

    @Override
    public void onReply(Tweet tweet) {
        // Display the compose tweet dialog
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetFragment tweetDialog = ComposeTweetFragment.newInstance(Helper.getCurrentUser(client, this), tweet);
        tweetDialog.show(fm, Helper.COMPOSE_TWEET);
    }

    @Override
    public void onTweetSent(Tweet tweet) {
        // Add the newly added tweet to the top of the adapter
        TweetsListFragment fragmentTweetsList = (TweetsListFragment) adapter.instantiateItem(viewPager, viewPager.getCurrentItem());
        fragmentTweetsList.onTweetSent(tweet);
    }

    @Override
    public void onDisplayUser(User user) {
        Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = {"Search Results"};

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return TweetsListFragment.newInstance(mQuery, TweetsListFragment.TweetListType.SEARCHED_TWEETS);
            else
                return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }
}
