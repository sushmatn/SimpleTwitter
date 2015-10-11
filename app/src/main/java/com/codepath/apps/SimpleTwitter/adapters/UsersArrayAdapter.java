package com.codepath.apps.SimpleTwitter.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.SimpleTwitter.R;
import com.codepath.apps.SimpleTwitter.TwitterApplication;
import com.codepath.apps.SimpleTwitter.TwitterClient;
import com.codepath.apps.SimpleTwitter.activities.ProfileActivity;
import com.codepath.apps.SimpleTwitter.helpers.Helper;
import com.codepath.apps.SimpleTwitter.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by SushmaNayak on 10/7/2015.
 */
public class UsersArrayAdapter extends ArrayAdapter<User> {

    Context mContext;

    public UsersArrayAdapter(Context context, List<User> objects) {
        super(context, 0, objects);
        mContext = context;
    }

    static class UserViewHolder {
        ImageView ivProfileImg;
        TextView tvUserName;
        TextView tvScreenName;
        TextView tvBody;
        ImageView ivFollow;

        public void bind(final User user, final Context context) {
            ivProfileImg.setImageResource(android.R.color.transparent);
            Picasso.with(context).load(user.getProfileImageUrl()).into(ivProfileImg);
            tvUserName.setText(user.getName());
            tvScreenName.setText("@" + user.getScreenName());
            tvBody.setText(Html.fromHtml(user.getTagLine()));

            ivFollow.setImageResource(android.R.color.transparent);
            int followID = user.isFollowing() ? R.drawable.ic_friends : R.drawable.ic_addfriend;
            ivFollow.setImageResource(followID);

            ivFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TwitterClient client = TwitterApplication.getRestClient();
                    Helper.onUpdateFriendship(client, user,(Helper.OnUpdateFriendship)context);
                }
            });

            ivProfileImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("user", user);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position);
        UserViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_user, parent, false);
            viewHolder = new UserViewHolder();
            viewHolder.ivProfileImg = (ImageView) convertView.findViewById(R.id.ivProfileImg);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.ivFollow = (ImageView) convertView.findViewById(R.id.ivFollow);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (UserViewHolder) convertView.getTag();

        viewHolder.bind(user, mContext);
        return convertView;
    }
}
