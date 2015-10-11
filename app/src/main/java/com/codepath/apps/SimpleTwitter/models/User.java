package com.codepath.apps.SimpleTwitter.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.TableInfo;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

/**
 * Created by SushmaNayak on 9/28/2015.
 */

@Table(name = "users")
public class User extends Model implements Parcelable {

    @Column(name = "name")
    private String name;

    @Column(name = "userId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long userId;

    @Column(name = "screenName")
    private String screenName;

    @Column(name = "profileImgURL")
    private String profileImageUrl;

    @Column(name = "tagLine")
    private String tagLine;

    @Column(name = "followersCount")
    private int followersCount;

    @Column(name = "followingCount")
    private int followingCount;

    @Column(name = "following")
    private boolean following;

    @Column(name = "profile_banner_url")
    private String profile_banner_url;

    @Column(name = "location")
    private String location;

    @Column(name = "url")
    private String url;

    // Used to return items from another table based on the foreign key
    public List<User> items() {
        return getMany(User.class, "Category");
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public long getUserId() {
        return userId;
    }

    public String getTagLine() {
        return tagLine;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public boolean isFollowing() {
        return following;
    }

    public String getProfile_banner_url() {
        return profile_banner_url;
    }

    public String getLocation() {
        return location;
    }

    public String getUrl() {
        return url;
    }

    public static User fromJSON(JSONObject jsonObject) {
        User user = null;
        try {
            long userId = jsonObject.getLong("id");
            //user = new Select().from(User.class).where("userId = ?", userId).executeSingle();

            //if (user == null)
            {
                user = new User();
                user.name = jsonObject.getString("name");
                user.userId = jsonObject.getLong("id");
                user.screenName = jsonObject.getString("screen_name");
                user.profileImageUrl = jsonObject.getString("profile_image_url");
                user.tagLine = jsonObject.getString("description");
                user.followingCount = jsonObject.getInt("friends_count");
                user.followersCount = jsonObject.getInt("followers_count");
                user.following = jsonObject.getBoolean("following");
                if (jsonObject.has("profile_banner_url"))
                    user.profile_banner_url = jsonObject.getString("profile_banner_url");
                user.location = jsonObject.getString("location");
                if (jsonObject.has("entities"))
                    if (jsonObject.getJSONObject("entities").has("url"))
                        if (jsonObject.getJSONObject("entities").getJSONObject("url").has("urls"))
                            user.url = jsonObject.getJSONObject("entities").getJSONObject("url").getJSONArray("urls").getJSONObject(0).getString("display_url");
                user.save();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static ArrayList<User> fromJSONArray(JSONArray userArray) {
        ArrayList<User> users = new ArrayList<>();
        try {
            for (int i = 0; i < userArray.length(); i++) {
                try {
                    JSONObject tweetObject = userArray.getJSONObject(i);
                    users.add(User.fromJSON(tweetObject));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public User() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.userId);
        dest.writeString(this.screenName);
        dest.writeString(this.profileImageUrl);
        dest.writeString(this.tagLine);
        dest.writeInt(this.followersCount);
        dest.writeInt(this.followingCount);
        dest.writeByte(following ? (byte) 1 : (byte) 0);
        dest.writeString(this.profile_banner_url);
        dest.writeString(this.location);
        dest.writeString(this.url);
    }

    private User(Parcel in) {
        this.name = in.readString();
        this.userId = in.readLong();
        this.screenName = in.readString();
        this.profileImageUrl = in.readString();
        this.tagLine = in.readString();
        this.followersCount = in.readInt();
        this.followingCount = in.readInt();
        this.following = in.readByte() != 0;
        this.profile_banner_url = in.readString();
        this.location = in.readString();
        this.url = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
