package com.codepath.apps.SimpleTwitter.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.SimpleTwitter.R;
import com.codepath.apps.SimpleTwitter.TwitterApplication;
import com.codepath.apps.SimpleTwitter.TwitterClient;
import com.codepath.apps.SimpleTwitter.fragments.ComposeTweetFragment;
import com.codepath.apps.SimpleTwitter.fragments.PhotosFragment;
import com.codepath.apps.SimpleTwitter.fragments.TweetsListFragment;
import com.codepath.apps.SimpleTwitter.helpers.Helper;
import com.codepath.apps.SimpleTwitter.models.Tweet;
import com.codepath.apps.SimpleTwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.NumberFormat;

public class ProfileActivity extends AppCompatActivity
        implements Helper.OnReTweetListener, Helper.OnFavoriteListener,
        Helper.OnReplyListener, Helper.OnTweetListener, Helper.OnUserProfileClick, Helper.OnUpdateFriendship {

    User currentUser;
    ImageView ivBackDrop;
    ImageView ivProfileImg;
    TextView tvUserName;
    TextView tvScreenName;
    TextView tvDescription;
    TextView tvFollowingCount;
    TextView tvFollowersCount;
    TextView tvFollowing;
    TextView tvFollowers;
    ImageView ivFollow;
    TweetsPagerAdapter tweetsPagerAdapter;
    ViewPager viewPager;
    PagerSlidingTabStrip tabStrip;
    String screenName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getIntent().hasExtra("user")) {
            currentUser = getIntent().getParcelableExtra("user");
            screenName = currentUser.getScreenName();
        } else if (getIntent().hasExtra(Helper.SCREEN_NAME)) {
            screenName = getIntent().getStringExtra(Helper.SCREEN_NAME);
            getUser(screenName);
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tweetsPagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tweetsPagerAdapter);

        ivBackDrop = (ImageView) findViewById(R.id.ivBackDrop);
        ivProfileImg = (ImageView) findViewById(R.id.ivProfileImg);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        tvFollowersCount = (TextView) findViewById(R.id.tvFollowersCount);
        tvFollowing = (TextView) findViewById(R.id.tvfollowing);
        tvFollowingCount = (TextView) findViewById(R.id.tvFollowingCount);
        ivFollow = (ImageView) findViewById(R.id.ivFollow);

        ivFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwitterClient client = TwitterApplication.getRestClient();
                Helper.onUpdateFriendship(client, currentUser, ProfileActivity.this);
            }
        });

        tvFollowing.setOnClickListener(followingListener);
        tvFollowingCount.setOnClickListener(followingListener);

        tvFollowers.setOnClickListener(followerListener);
        tvFollowersCount.setOnClickListener(followerListener);

        tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(viewPager);

        if (currentUser != null)
            setupUI();
        else
            getUser(screenName);
    }

    private void setupUI() {
        if (currentUser.getProfile_banner_url() != null)
            Picasso.with(this).load(currentUser.getProfile_banner_url()).into(ivBackDrop);
        Picasso.with(this).load(currentUser.getProfileImageUrl()).into(ivProfileImg);
        tvUserName.setText(currentUser.getName());
        tvScreenName.setText("@" + currentUser.getScreenName());
        tvDescription.setText(currentUser.getTagLine());
        tvFollowersCount.setText(NumberFormat.getNumberInstance(this.getResources().getConfiguration().locale).format(currentUser.getFollowersCount()));
        tvFollowingCount.setText(NumberFormat.getNumberInstance(this.getResources().getConfiguration().locale).format(currentUser.getFollowingCount()));
        ivFollow.setImageResource(android.R.color.transparent);
        int followID = currentUser.isFollowing() ? R.drawable.ic_friends : R.drawable.ic_addfriend;
        ivFollow.setImageResource(followID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onReTweet(Tweet tweet, Tweet newTweet) {
        if (viewPager.getCurrentItem() == 0 || viewPager.getCurrentItem() == 2) {
            TweetsListFragment fragmentTweetsList = (TweetsListFragment) tweetsPagerAdapter.instantiateItem(viewPager, viewPager.getCurrentItem());
            fragmentTweetsList.onReTweet(tweet, newTweet);
        }
    }

    @Override
    public void onFavorite(final Tweet tweet, Tweet newTweet) {
        if (viewPager.getCurrentItem() == 0 || viewPager.getCurrentItem() == 2) {
            TweetsListFragment fragmentTweetsList = (TweetsListFragment) tweetsPagerAdapter.instantiateItem(viewPager, viewPager.getCurrentItem());
            fragmentTweetsList.onFavorite(tweet, newTweet);
        }
    }

    @Override
    public void onReply(Tweet tweet) {
        // Display the compose tweet dialog
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetFragment tweetDialog = ComposeTweetFragment.newInstance(Helper.getCurrentUser(TwitterApplication.getRestClient(), this), tweet);
        tweetDialog.show(fm, Helper.COMPOSE_TWEET);
    }

    @Override
    public void onTweetSent(Tweet tweet) {
        if (viewPager.getCurrentItem() == 0 || viewPager.getCurrentItem() == 2) {
            TweetsListFragment fragmentTweetsList = (TweetsListFragment) tweetsPagerAdapter.instantiateItem(viewPager, viewPager.getCurrentItem());
            fragmentTweetsList.onTweetSent(tweet);
        }
    }

    @Override
    public void onDisplayUser(User user) {
        Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    View.OnClickListener followerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startFriendshipActivity(Helper.ListType.FOLLOWER);
        }
    };

    View.OnClickListener followingListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startFriendshipActivity(Helper.ListType.FOLLOWING);
        }
    };

    private void startFriendshipActivity(Helper.ListType listType) {
        Intent intent = new Intent(this, ContainerActivity.class);
        intent.putExtra(Helper.LIST_TYPE, listType.ordinal());
        intent.putExtra(Helper.SCREEN_NAME, currentUser.getScreenName());
        startActivity(intent);
    }

    private void getUser(String screenName) {
        TwitterApplication.getRestClient().getUser(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                try {
                    currentUser = User.fromJSON(response);
                    viewPager.setAdapter(tweetsPagerAdapter);
                    tabStrip.setViewPager(viewPager);
                    setupUI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.v("Twitter", errorResponse.toString());
            }
        });
    }

    @Override
    public void onUpdateFriendship(User user, User newUser) {
        int followID = newUser.isFollowing() ? R.drawable.ic_friends : R.drawable.ic_addfriend;
        ivFollow.setImageResource(followID);
    }

    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = {"Tweets", "Photos", "Favorites"};

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return TweetsListFragment.newInstance(currentUser.getScreenName(), TweetsListFragment.TweetListType.USER_TIMELINE);
            else if (position == 1)
                return PhotosFragment.newInstance(currentUser.getScreenName());
            else if (position == 2)
                return TweetsListFragment.newInstance(currentUser.getScreenName(), TweetsListFragment.TweetListType.FAVORITES_LIST);
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
