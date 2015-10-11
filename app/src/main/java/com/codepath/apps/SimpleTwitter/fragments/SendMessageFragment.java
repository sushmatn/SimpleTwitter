package com.codepath.apps.SimpleTwitter.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.SimpleTwitter.R;
import com.codepath.apps.SimpleTwitter.TwitterApplication;
import com.codepath.apps.SimpleTwitter.TwitterClient;
import com.codepath.apps.SimpleTwitter.helpers.Helper;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendMessageFragment extends DialogFragment {

    EditText etRecipient;
    EditText etBody;
    TextView tvCancel;
    TextView tvSend;

    public SendMessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_message, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etRecipient = (EditText) view.findViewById(R.id.etRecipient);
        etBody = (EditText) view.findViewById(R.id.etBody);
        tvCancel = (TextView) view.findViewById(R.id.tvCancel);
        tvSend = (TextView) view.findViewById(R.id.tvSend);

        if (!Helper.isNetworkAvailable(getActivity())) {
            return;
        }

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendMessage();
            }
        });
    }

    private void onSendMessage() {

        if (Helper.isNetworkAvailable(getActivity())) {
            TwitterClient client = TwitterApplication.getRestClient();
            Helper.onSendMessage(getContext(), client, etRecipient.getText().toString().replace("@", ""), etBody.getText().toString(), (Helper.OnMessageSentListener) getActivity());
        }
        dismiss();
    }
}
