package com.codepath.apps.SimpleTwitter.helpers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.SimpleTwitter.R;
import com.codepath.apps.SimpleTwitter.TwitterClient;
import com.codepath.apps.SimpleTwitter.activities.ProfileActivity;
import com.codepath.apps.SimpleTwitter.models.Message;
import com.codepath.apps.SimpleTwitter.models.Tweet;
import com.codepath.apps.SimpleTwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by SushmaNayak on 9/29/2015.
 */
public class Helper {

    public interface OnReTweetListener {
        void onReTweet(Tweet tweet, Tweet newTweet);
    }

    public interface OnFavoriteListener {
        void onFavorite(Tweet tweet, Tweet newTweet);
    }

    public interface OnReplyListener {
        void onReply(Tweet tweet);
    }

    public interface OnTweetListener {
        void onTweetSent(Tweet tweet);
    }

    public interface OnMessageSentListener {
        void onMessageSent(Message message);
    }

    public interface OnUserProfileClick {
        void onDisplayUser(User user);
    }

    public interface OnUpdateFriendship {
        void onUpdateFriendship(User user, User newUser);
    }

    public static User currentUser = null;
    public static final String SCREEN_NAME = "ScreenName";
    public static final String LIST_TYPE = "ListType";
    public static final String COMPOSE_TWEET = "Compose Tweet Dialog";

    public enum ListType {
        FOLLOWER, FOLLOWING
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean bRet = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        if (!bRet)
            Toast.makeText(context, context.getResources().getString(R.string.networkUnavailable), Toast.LENGTH_SHORT).show();
        return bRet;
    }

    public static String getRelativeTimeStamp(String timeStamp) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(timeStamp).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString().replace("ago", "")
                    .replace(" days", "d").replace(" hours", "h").replace(" minutes", "m").replace(" seconds", "s")
                    .replace(" day", "d").replace(" hour", "h").replace(" minute", "m").replace(" second", "s");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }

    public void SetText(Context context, String textToAdd, TextView view) {

        view.setTextColor(context.getResources().getColor(android.R.color.black));

        SpannableStringBuilder ssb = new SpannableStringBuilder(textToAdd);

        addSpans(context, textToAdd, '#', ssb);
        addSpans(context, textToAdd, '@', ssb);

        view.setText(ssb, TextView.BufferType.EDITABLE);
        view.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void addSpans(Context context, String body, char prefix, SpannableStringBuilder ssb) {

        Pattern pattern = Pattern.compile(prefix + "\\w+");
        Matcher matcher = pattern.matcher(body);

        // Check all occurrences
        while (matcher.find()) {
            ssb.setSpan(
                    new CalloutLink(context),
                    matcher.start(),
                    matcher.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public class CalloutLink extends ClickableSpan {
        Context mContext;

        public CalloutLink(Context context) {
            super();
            mContext = context;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(mContext.getResources().getColor(R.color.colorPrimary));
        }

        @Override
        public void onClick(View widget) {
            TextView tv = (TextView) widget;
            Spanned s = (Spanned) tv.getText();
            int start = s.getSpanStart(this);
            int end = s.getSpanEnd(this);
            String theWord = s.subSequence(start, end).toString();

            if (theWord.startsWith("@")) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra(SCREEN_NAME, theWord.replace("@", ""));
                mContext.startActivity(intent);
            }
        }
    }

    public static User getCurrentUser(TwitterClient client, Context context) {
        if (!isNetworkAvailable(context))
            return null;

        if (currentUser == null) {
            client.getCurrentUser(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                    currentUser = User.fromJSON(response);
                }

                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    currentUser = null;
                }
            });
        }
        return currentUser;
    }

    public static void onReTweet(TwitterClient client, final Tweet tweet, final OnReTweetListener listener) {

        client.reTweet(Long.toString(tweet.getTweetId()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                Tweet newTweet = Tweet.fromJSONObject(response);
                listener.onReTweet(tweet, newTweet);
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.v("Twitter", errorResponse.toString());
            }
        });
    }

    public static void onFavorite(TwitterClient client, final Tweet tweet, final OnFavoriteListener listener) {
        client.likeTweet(Long.toString(tweet.getTweetId()), tweet.isFavorited(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                Tweet newTweet = Tweet.fromJSONObject(response);
                listener.onFavorite(tweet, newTweet);
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.v("Twitter", errorResponse.toString());
            }
        });
    }

    public static void onTweet(TwitterClient client, String replyStatusID, String tweet, final OnTweetListener listener) {
        client.composeTweet(tweet, replyStatusID, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                Tweet newTweet = Tweet.fromJSONObject(response);
                listener.onTweetSent(newTweet);
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.v("Twitter", errorResponse.toString());
            }
        });
    }

    public static void onUpdateFriendship(TwitterClient client, final User user, final OnUpdateFriendship listener) {
        client.updateFriendship(user.getScreenName(), !user.isFollowing(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                User newUser = User.fromJSON(response);
                listener.onUpdateFriendship(user, newUser);
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.v("Twitter", errorResponse.toString());
            }
        });
    }

    public static void onSendMessage(final Context context, TwitterClient client, String sender, String message, final OnMessageSentListener listener) {
        client.sendMessage(sender, message, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                Message newMessage = Message.fromJSON(response, false);
                listener.onMessageSent(newMessage);
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(context, context.getResources().getString(R.string.sendmsgfailed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void onReply(final Tweet tweet, final OnReplyListener listener) {
        listener.onReply(tweet);
    }

    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);
    private static final Map<View, AnimatorSet> likeAnimations = new HashMap<>();

    public static void resetLikeAnimationState(ImageView holder) {
        likeAnimations.remove(holder);
    }

    public static void updateButtonStatus(final ImageView holder, boolean animated, final int iconID) {
        if (animated) {
            if (!likeAnimations.containsKey(holder)) {
                AnimatorSet animatorSet = new AnimatorSet();
                likeAnimations.put(holder, animatorSet);

                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder, "scaleX", 0.2f, 1f);
                bounceAnimX.setDuration(300);
                bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder, "scaleY", 0.2f, 1f);
                bounceAnimY.setDuration(300);
                bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                bounceAnimY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        holder.setImageResource(iconID);
                    }
                });

                animatorSet.play(bounceAnimX).with(bounceAnimY);

                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        resetLikeAnimationState(holder);
                    }
                });

                animatorSet.start();
            }
        }
    }
}
