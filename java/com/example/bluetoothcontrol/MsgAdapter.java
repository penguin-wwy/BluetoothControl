package com.example.bluetoothcontrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by penguin on 2016/12/9.
 */

public class MsgAdapter extends ArrayAdapter<Msg> {
    private int resourceId;
    private TextView mTextView;

    public MsgAdapter(Context context, int textViewResourceId, List<Msg> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        Msg msg = getItem(position);
        View view;
        if (converView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            mTextView = (TextView) view.findViewById(R.id.msg_view);
            view.setTag(mTextView);
        } else {
            view = converView;
            mTextView = (TextView) view.getTag();
        }

        if (msg.getType() == msg.TYPE_RECE) {
            mTextView.setText("# " + msg.getData() + "\n");
        } else if (msg.getType() == msg.TYPE_SEND) {
            mTextView.setText("/> " + msg.getData() + "\n");
        } else {
            mTextView.setText(msg.getData() + "\n");
        }
        return view;
    }
}
