package com.example.bluetoothcontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by penguin on 2016/12/8.
 */

public class MainActivity extends Activity implements Mask {
    private BtManager btManager = null;
    private BTClient mClient = null;

    private int index_of_choice = -1;
    protected int CONNECT_NO = -1;

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.main_activity);

        TextView mTextView = (TextView) findViewById(R.id.textView);
        btManager = BtManager.getInstance(mTextView);
        btManager.registerBluetoothReceiver(getApplicationContext());
        mTextView.setText(mTextView.getText(), TextView.BufferType.EDITABLE);

        Button search = (Button) findViewById(R.id.search);
        Button contect = (Button) findViewById(R.id.contect);
        Button chat = (Button) findViewById(R.id.chat);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btManager.openBluetooth(MainActivity.this);
                btManager.startDiscovery();
            }
        });

        contect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btManager.allDevices.size() < 1 && btManager.getmBAdapter().getBondedDevices().size() < 1) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("Error").setMessage("没有设备")
                            .setNegativeButton("cancel", null).show();
                    return;
                }
                else if (btManager.bDevices.size() == 0) {
                    for (int i = 0; i < btManager.getmBAdapter().getBondedDevices().size(); i++) {
                        BluetoothDevice device = btManager.getmBAdapter().getBondedDevices().iterator().next();
                        btManager.allDevices.add(device);
                        btManager.bDevices.add(device);
                    }
                }
                String [] name = new String[btManager.allDevices.size()];
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("请选择设备");
                for (int i = 0; i < btManager.allDevices.size(); i++) {
                    name[i] = btManager.allDevices.get(i).getName();
                }
                alertDialog.setSingleChoiceItems(name, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        index_of_choice = i;
                    }
                }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //开始连接设备
                        if (index_of_choice == -1) {
                            Toast.makeText(MainActivity.this, "请选择设备", Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }
                        mClient = BTClient.getInstance(btManager);
                        btManager.setTarget(btManager.allDevices.get(index_of_choice));
                        int mask = mClient.connectBT(btManager.getTarget());
                        if (mask == CONN_SUCC) {
                            Toast.makeText(MainActivity.this, "connect success", Toast.LENGTH_SHORT)
                                    .show();
                            CONNECT_NO = index_of_choice;
                        }
                        else if (mask == CONN_ERR){
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                            dialog.setTitle("Error").setMessage("连接失败")
                                    .setNegativeButton("cancel", null).show();
                        }
                        else if (mask == DEVICE_BUSY) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                            dialog.setTitle("Error");
                            dialog.setMessage("该设备已连接").setNegativeButton("cancel", null).show();
                        }
                    }
                }).setNegativeButton("cancel", null).show();

            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }
}
