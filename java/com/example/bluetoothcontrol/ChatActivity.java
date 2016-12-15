package com.example.bluetoothcontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by penguin on 2016/12/9.
 */

public class ChatActivity extends Activity {
    private ListView msgListView;
    private EditText inputText;
    private Button send;
    private MsgAdapter adapter;
    private BTClient mBTClient;
    private List<Msg> listMsg = new ArrayList<Msg>();

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        setContentView(R.layout.chat_activty);
        initMsg();
        adapter = new MsgAdapter(getApplicationContext(), R.layout.msg_item, listMsg);
        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        msgListView = (ListView) findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = inputText.getText().toString() + "$";
                if (!"".equals(data)) {
                    Msg msg = new Msg(data, Msg.TYPE_SEND);
                    listMsg.add(msg);
                    adapter.notifyDataSetChanged();
                    msgListView.setSelection(listMsg.size());
                    inputText.setText("");
                    if(!mBTClient.sendMsg(msg)) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ChatActivity.this);
                        dialog.setTitle("Error").setMessage("发送失败")
                                .setNegativeButton("cancel", null).show();
                    }
                }
            }
        });
        receiverMessageTask();
    }

    private void initMsg() {
        mBTClient = BTClient.getInstance();
        mBTClient.setlistMsg(listMsg);

        if (mBTClient.getSocket().isConnected()) {
            listMsg.add(new Msg("welcome! You are connecting\n name : "
                    + BtManager.getInstance().getTarget().getName(), Msg.TYPE_OTHE));
        }
        else {
            listMsg.add(new Msg("welcome! You aren't contect devices yet, " +
                    "you will not get and receive\n", Msg.TYPE_OTHE));
        }

    }

    private void receiverMessageTask() {
        Thread thread = mBTClient.thread;
        thread.start();
    }


}
