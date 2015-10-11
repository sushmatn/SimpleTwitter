package com.codepath.apps.SimpleTwitter;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.FlickrApi;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
    public static final String REST_URL = "https://api.twitter.com/1.1/";
    public static final String REST_CONSUMER_KEY = "kMjq7wUMT3064Fg5VUnXNe6pP";
    public static final String REST_CONSUMER_SECRET = "cFfl0qOgZxSyt020U29BuZfLFUMZGlQsmw5qQzXeG6joPshwAb";
    public static final String REST_CALLBACK_URL = "oauth://TestTwitterClientSN";

    public static final String HOME_TIMELINE = "statuses/home_timeline.json";
    public static final String PARAM_SINCE_ID = "since_id";
    public static final String PARAM_MAX_ID = "max_id";

    public static final String MENTIONS_TIMELINE = "statuses/mentions_timeline.json";
    public static final String USER_TIMELINE = "statuses/user_timeline.json";
    public static final String PARAM_SCREEN_NAME = "screen_name";

    public static final String UPDATE_STATUS = "statuses/update.json";
    public static final String PARAM_STATUS = "status";
    public static final String PARAM_REPLY_ID = "in_reply_to_status_id";

    public static final String FAVORITES_LIST = "favorites/list.json";
    public static final String ADD_FAVORITE = "favorites/create.json";
    public static final String REMOVE_FAVORITE = "favorites/destroy.json";

    public static final String GET_CURRENT_USER = "account/verify_credentials.json";
    public static final String FOLLOWERS_LIST = "followers/list.json";
    public static final String FOLLOWING_LIST = "friends/list.json";
    public static final String SEARCH_URL = "search/tweets.json";
    public static final String SHOW_USER = "users/show.json";

    public static final String DIRECT_MSG = "direct_messages.json";
    public static final String DIRECT_MSG_SENT = "direct_messages/sent.json";
    public static final String DIRECT_MSG_NEW = "direct_messages/new.json";

    public static final String FRIENDSHIP_CREATE = "friendships/create.json";
    public static final String FRIENDSHIP_DESTROY = "friendships/destroy.json";

    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    public void getHomeTimeLine(long since_id, AsyncHttpResponseHandler handler) {
        String api_url = getApiUrl(HOME_TIMELINE);
        RequestParams params = new RequestParams();
        params.put("count", 10);
        if (since_id == 1)
            params.put(PARAM_SINCE_ID, 1);
        else
            params.put(PARAM_MAX_ID, since_id - 1);

        getClient().get(api_url, params, handler);
    }

    public void getMentionsTimeLine(long since_id, AsyncHttpResponseHandler handler) {
        String api_url = getApiUrl(MENTIONS_TIMELINE);
        RequestParams params = new RequestParams();
        params.put("count", 10);
        if (since_id == 1)
            params.put(PARAM_SINCE_ID, 1);
        else
            params.put(PARAM_MAX_ID, since_id - 1);

        getClient().get(api_url, params, handler);
    }

    public void getFavoritesList(String screenName, long since_id, AsyncHttpResponseHandler handler) {
        String api_url = getApiUrl(FAVORITES_LIST);
        RequestParams params = new RequestParams();
        params.put("count", 10);
        params.put(PARAM_SCREEN_NAME, screenName);
        if (since_id == 1)
            params.put(PARAM_SINCE_ID, 1);
        else
            params.put(PARAM_MAX_ID, since_id - 1);
        getClient().get(api_url, params, handler);
    }

    public void getUserTimeLine(String screenName, long since_id, AsyncHttpResponseHandler handler) {
        String api_url = getApiUrl(USER_TIMELINE);
        RequestParams params = new RequestParams();
        params.put("count", 10);
        params.put(PARAM_SCREEN_NAME, screenName);
        if (since_id == 1)
            params.put(PARAM_SINCE_ID, 1);
        else
            params.put(PARAM_MAX_ID, since_id - 1);
        getClient().get(api_url, params, handler);
    }

    public void SearchTweets(String query, long since_id, AsyncHttpResponseHandler handler) {
        String api_url = getApiUrl(SEARCH_URL);
        RequestParams params = new RequestParams();
        params.put("q", query);
        params.put("count", 20);
        if (since_id == 1)
            params.put(PARAM_SINCE_ID, 1);
        else
            params.put(PARAM_MAX_ID, since_id - 1);
        getClient().get(api_url, params, handler);
    }

    public void getFollowers(String screenName, long cursorId, AsyncHttpResponseHandler handler) {
        String api_url = getApiUrl(FOLLOWERS_LIST);
        RequestParams params = new RequestParams();
        params.put("count", 10);
        params.put(PARAM_SCREEN_NAME, screenName);
        params.put("skip_status", "true");
        params.put("cursor", cursorId);
        getClient().get(api_url, params, handler);
    }

    public void getFollowing(String screenName, long cursorId, AsyncHttpResponseHandler handler) {
        String api_url = getApiUrl(FOLLOWING_LIST);
        RequestParams params = new RequestParams();
        params.put("count", 10);
        params.put(PARAM_SCREEN_NAME, screenName);
        params.put("skip_status", "true");
        params.put("cursor", cursorId);
        getClient().get(api_url, params, handler);
    }

    public void getUserPhotos(String screenName, long since_id, AsyncHttpResponseHandler handler) {
        String api_url = getApiUrl(USER_TIMELINE);
        RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put(PARAM_SCREEN_NAME, screenName);
        params.put("exclude_replies", "true");
        params.put("include_rts", "false");
        if (since_id == 1)
            params.put(PARAM_SINCE_ID, 1);
        else
            params.put(PARAM_MAX_ID, since_id - 1);
        getClient().get(api_url, params, handler);
    }

    // COMPOSE a tweet
    public void composeTweet(String tweet, String replyStatusID, AsyncHttpResponseHandler handler) {
        String api_url = getApiUrl(UPDATE_STATUS);
        RequestParams params = new RequestParams();
        params.put(PARAM_STATUS, tweet);
        if (replyStatusID != null)
            params.put(PARAM_REPLY_ID, replyStatusID);

        getClient().post(api_url, params, handler);
    }

    public void reTweet(String id, AsyncHttpResponseHandler handler) {
        String api_url = getApiUrl("statuses/retweet/" + id + ".json");
        RequestParams params = new RequestParams();
        getClient().post(api_url, params, handler);
    }

    public void likeTweet(String id, boolean liked, AsyncHttpResponseHandler handler) {
        String api_url;
        if (liked)
            api_url = getApiUrl(REMOVE_FAVORITE);
        else
            api_url = getApiUrl(ADD_FAVORITE);

        RequestParams params = new RequestParams();
        params.put("id", id);
        getClient().post(api_url, params, handler);
    }

    public void getCurrentUser(AsyncHttpResponseHandler handler) {
        String api_url = getApiUrl(GET_CURRENT_USER);
        getClient().get(api_url, null, handler);
    }

    public void getUser(String screenName, AsyncHttpResponseHandler handler) {
        String api_url = getApiUrl(SHOW_USER);
        RequestParams params = new RequestParams();
        params.put(PARAM_SCREEN_NAME, screenName);
        getClient().get(api_url, params, handler);
    }

    public void getMessages(boolean bReceived, AsyncHttpResponseHandler handler) {
        String api_url;
        if (bReceived)
            api_url = getApiUrl(DIRECT_MSG);
        else
            api_url = getApiUrl(DIRECT_MSG_SENT);
        RequestParams params = new RequestParams();
        params.put("count", 20);
        getClient().get(api_url, params, handler);
    }

    public void sendMessage(String screenName, String text, AsyncHttpResponseHandler handler) {
        String api_url = getApiUrl(DIRECT_MSG_NEW);
        RequestParams params = new RequestParams();
        params.put(PARAM_SCREEN_NAME, screenName);
        params.put("text", text);
        getClient().post(api_url, params, handler);
    }

    public void updateFriendship(String screenName, boolean bCreate, AsyncHttpResponseHandler handler) {
        String api_url;
        if (bCreate)
            api_url = getApiUrl(FRIENDSHIP_CREATE);
        else
            api_url = getApiUrl(FRIENDSHIP_DESTROY);

        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        getClient().post(api_url, params, handler);
    }
}