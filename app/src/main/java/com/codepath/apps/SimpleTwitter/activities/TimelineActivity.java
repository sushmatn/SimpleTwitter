package com.codepath.apps.SimpleTwitter.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.SimpleTwitter.R;
import com.codepath.apps.SimpleTwitter.TwitterApplication;
import com.codepath.apps.SimpleTwitter.TwitterClient;
import com.codepath.apps.SimpleTwitter.fragments.ComposeTweetFragment;
import com.codepath.apps.SimpleTwitter.fragments.TweetsListFragment;
import com.codepath.apps.SimpleTwitter.helpers.Helper;
import com.codepath.apps.SimpleTwitter.models.Tweet;
import com.codepath.apps.SimpleTwitter.models.User;

public class TimelineActivity extends AppCompatActivity
        implements Helper.OnReTweetListener, Helper.OnFavoriteListener,
        Helper.OnReplyListener, Helper.OnTweetListener, Helper.OnUserProfileClick {

    TwitterClient client;
    TweetsPagerAdapter adapter;
    ViewPager viewPager;
    PagerSlidingTabStrip tabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timeline);
        client = TwitterApplication.getRestClient();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new TweetsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(viewPager);

        // Display the logo
        getSupportActionBar().setLogo(getResources().getDrawable(R.mipmap.ic_launcher));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // Get the user that is currently logged in
        Helper.getCurrentUser(client, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(TimelineActivity.this, SearchActivity.class);
                intent.putExtra(Helper.SCREEN_NAME, query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_compose) {
            FragmentManager fm = getSupportFragmentManager();
            ComposeTweetFragment tweetDialog = ComposeTweetFragment.newInstance(Helper.getCurrentUser(client, this), null);
            tweetDialog.show(fm, Helper.COMPOSE_TWEET);
            return true;
        } else if (id == R.id.action_profile) {
            Intent intent = new Intent(TimelineActivity.this, ProfileActivity.class);
            intent.putExtra("user", Helper.currentUser);
            startActivity(intent);
        } else if (id == R.id.action_messages) {
            Intent intent = new Intent(this, MessageActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
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
        Intent intent = new Intent(TimelineActivity.this, ProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = {"Home", "Mentions"};

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return TweetsListFragment.newInstance(TweetsListFragment.TweetListType.HOME_TIMELINE);
            else if (position == 1)
                return TweetsListFragment.newInstance(TweetsListFragment.TweetListType.MENTIONS_TIMELINE);
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
