package com.codepath.apps.SimpleTwitter.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.codepath.apps.SimpleTwitter.R;
import com.codepath.apps.SimpleTwitter.TwitterApplication;
import com.codepath.apps.SimpleTwitter.TwitterClient;
import com.codepath.apps.SimpleTwitter.adapters.UsersArrayAdapter;
import com.codepath.apps.SimpleTwitter.helpers.EndlessScrollListener;
import com.codepath.apps.SimpleTwitter.helpers.Helper;
import com.codepath.apps.SimpleTwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;

public class ContainerActivity extends AppCompatActivity
        implements Helper.OnUpdateFriendship {

    Helper.ListType listType;
    TwitterClient client;
    long cursorID = -1;

    ListView lvUsers;
    ArrayList<User> users;
    UsersArrayAdapter aUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        listType = Helper.ListType.values()[getIntent().getIntExtra(Helper.LIST_TYPE, 0)];

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(getResources().getDrawable(R.mipmap.ic_launcher));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(listType == Helper.ListType.FOLLOWER ? getResources().getString(R.string.followers) : getResources().getString(R.string.following));

        client = TwitterApplication.getRestClient();
        users = new ArrayList<>();
        aUsers = new UsersArrayAdapter(this, users);

        lvUsers = (ListView) findViewById(R.id.lvUsers);
        lvUsers.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (cursorID != 0)
                    populateList(cursorID);
                return true;
            }
        });
        lvUsers.setAdapter(aUsers);

        populateList(cursorID);
    }

    private void populateList(long cursorID) {
        if (!Helper.isNetworkAvailable(this)) {
            return;
        }
        if (listType == Helper.ListType.FOLLOWER)
            client.getFollowers(getIntent().getStringExtra(Helper.SCREEN_NAME), cursorID, handler);
        else
            client.getFollowing(getIntent().getStringExtra(Helper.SCREEN_NAME), cursorID, handler);
    }

    JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {

        @Override
        public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
            try {
                ArrayList<User> usersArrayList = User.fromJSONArray(response.getJSONArray("users"));
                cursorID = response.getLong("next_cursor");
                aUsers.addAll(usersArrayList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.v("Twitter", errorResponse.toString());
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friendship, menu);
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
    public void onUpdateFriendship(User user, User newUser) {
        int position = aUsers.getPosition(user);
        aUsers.remove(user);
        aUsers.insert(newUser, position);
    }
}
