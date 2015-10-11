package com.codepath.apps.SimpleTwitter.activities;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.codepath.apps.SimpleTwitter.R;
import com.codepath.apps.SimpleTwitter.TwitterApplication;
import com.codepath.apps.SimpleTwitter.TwitterClient;
import com.codepath.apps.SimpleTwitter.adapters.MessageArrayAdapter;
import com.codepath.apps.SimpleTwitter.adapters.UsersArrayAdapter;
import com.codepath.apps.SimpleTwitter.fragments.ComposeTweetFragment;
import com.codepath.apps.SimpleTwitter.fragments.SendMessageFragment;
import com.codepath.apps.SimpleTwitter.helpers.Helper;
import com.codepath.apps.SimpleTwitter.models.Message;
import com.codepath.apps.SimpleTwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity
        implements Helper.OnMessageSentListener {

    TwitterClient client;
    ListView lvMessages;
    ArrayList<Message> messages;
    MessageArrayAdapter aMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        client = TwitterApplication.getRestClient();

        lvMessages = (ListView) findViewById(R.id.lvMessages);
        messages = new ArrayList<>();
        aMessages = new MessageArrayAdapter(this, messages);
        lvMessages.setAdapter(aMessages);

        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setLogo(getResources().getDrawable(R.mipmap.ic_launcher));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateMessages(true);
        populateMessages(false);
    }

    private void populateMessages(final boolean bReceived) {
        if (!Helper.isNetworkAvailable(this)) {
            return;
        }
        client.getMessages(bReceived, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONArray response) {
                try {
                    ArrayList<Message> messages = Message.fromJSONArray(response, bReceived);
                    aMessages.addAll(messages);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_compose) {
            // Display the compose tweet dialog
            FragmentManager fm = getSupportFragmentManager();
            SendMessageFragment msgDialog = new SendMessageFragment();
            msgDialog.show(fm, "SendMsgDialog");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMessageSent(Message message) {
        aMessages.insert(message, 0);
    }
}
