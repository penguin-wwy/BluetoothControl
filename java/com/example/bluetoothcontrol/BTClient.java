package com.example.bluetoothcontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by penguin on 2016/12/10.
 */

public class BTClient implements Mask {

    public static BTClient client = null;

    private BluetoothAdapter bluetoothAdapter = null;
    //private BluetoothDevice bluetoothDevice = null;
    private BluetoothSocket bluetoothSocket = null;
    private BufferedInputStream bis = null;
    private BufferedOutputStream bos = null;
    private BtManager mBtManager = null;
    private List<Msg> listMsg = null;

    //private Activity mActivity = null;

    public static BTClient getInstance(BtManager mBtManager) {
        if (client == null)
            client = new BTClient(mBtManager);
        return client;
    }

    public static BTClient getInstance() {
        return client;
    }

    private BTClient(BtManager mBtManager) {
        this.mBtManager = mBtManager;
        this.bluetoothAdapter = mBtManager.getmBAdapter();
        //this.listMsg = listMsg;
    }

    public void setlistMsg(List<Msg> listMsg) {
        this.listMsg = listMsg;
    }

    public BluetoothSocket getSocket() {
        return this.bluetoothSocket;
    }

    public int connectBT(BluetoothDevice target) {
        if (target.getBondState() == BluetoothDevice.BOND_BONDED) {
            if (!mBtManager.bDevices.contains(target))
                return DEVICE_BUSY;
        }
        try {
            bluetoothSocket = target.createInsecureRfcommSocketToServiceRecord(UUID
                    .fromString("00001101-0000-1000-8000-00805F9B34FB"));
            bluetoothSocket.connect();
            return CONN_SUCC;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CONN_ERR;
    }

    public boolean sendMsg(Msg msg) {
        boolean result = false;
        if (null == bluetoothSocket || null == bos) {
            return result;
        }
        try {
            bos.write(msg.getData().getBytes());
            bos.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public class taskThread extends Thread {
        public void run() {
            byte[] buffer = new byte[2048];
            int totalRead;

            try {
                bis = new BufferedInputStream(bluetoothSocket.getInputStream());
                bos = new BufferedOutputStream(bluetoothSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                while ((totalRead = bis.read(buffer)) > 0) {
                    String txt = new String(buffer, 0, totalRead, "UTF-8");
                    listMsg.add(new Msg(txt, Msg.TYPE_RECE));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Thread thread = new taskThread();
}
