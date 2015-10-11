package com.codepath.apps.SimpleTwitter.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by SushmaNayak on 10/8/2015.
 */
public class Message {

    private User sender;
    private User receiver;
    private String text;
    private String createdAt;

    public boolean bReceived;

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public String getText() {
        return text;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public static Message fromJSON(JSONObject messageJSON, boolean bReceived) {
        Message message = null;
        try {
            message = new Message();
            message.text = messageJSON.getString("text");
            message.createdAt = messageJSON.getString("created_at");
            message.sender = User.fromJSON(messageJSON.getJSONObject("sender"));
            message.receiver = User.fromJSON(messageJSON.getJSONObject("recipient"));
            message.bReceived = bReceived;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    public static ArrayList<Message> fromJSONArray(JSONArray messageArray, boolean bReceived) {
        ArrayList<Message> messages = new ArrayList<>();
        try {
            for (int i = 0; i < messageArray.length(); i++)
                messages.add(Message.fromJSON(messageArray.getJSONObject(i), bReceived));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messages;
    }
}
