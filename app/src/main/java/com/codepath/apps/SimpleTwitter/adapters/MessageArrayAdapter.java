package com.codepath.apps.SimpleTwitter.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.SimpleTwitter.R;
import com.codepath.apps.SimpleTwitter.helpers.Helper;
import com.codepath.apps.SimpleTwitter.models.Message;
import com.codepath.apps.SimpleTwitter.models.User;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by SushmaNayak on 10/8/2015.
 */
public class MessageArrayAdapter extends ArrayAdapter<Message> {

    Context mContext;
    boolean bReceived;

    public MessageArrayAdapter(Context context, List<Message> objects) {
        super(context, 0, objects);
        mContext = context;
    }

    static class MsgViewHolder {
        RoundedImageView ivProfileImg;
        TextView tvUserName;
        TextView tvScreenName;
        TextView tvBody;
        TextView tvCreatedAt;
        ImageView ivSent;

        void bind(Context context, Message message) {
            User user = null;
            ivProfileImg.setImageResource(android.R.color.transparent);
            if (message.bReceived) {
                user = message.getSender();
                ivSent.setVisibility(View.GONE);
            }
            else {
                user = message.getReceiver();
                ivSent.setVisibility(View.VISIBLE);
            }

            Picasso.with(context).load(user.getProfileImageUrl()).into(ivProfileImg);
            tvUserName.setText(user.getName());
            tvScreenName.setText("@" + user.getScreenName());
            tvBody.setText(Html.fromHtml(message.getText()));
            tvCreatedAt.setText(Helper.getRelativeTimeStamp(message.getCreatedAt()));
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = getItem(position);
        MsgViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_message, parent, false);
            viewHolder = new MsgViewHolder();
            viewHolder.ivProfileImg = (RoundedImageView)convertView.findViewById(R.id.ivProfileImg);
            viewHolder.tvUserName = (TextView)convertView.findViewById(R.id.tvUserName);
            viewHolder.tvScreenName = (TextView)convertView.findViewById(R.id.tvScreenName);
            viewHolder.tvBody = (TextView)convertView.findViewById(R.id.tvBody);
            viewHolder.tvCreatedAt = (TextView)convertView.findViewById(R.id.tvCreatedAt);
            viewHolder.ivSent = (ImageView)convertView.findViewById(R.id.ivSent);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (MsgViewHolder) convertView.getTag();

        viewHolder.bind(mContext, message);
        return convertView;
    }
}
