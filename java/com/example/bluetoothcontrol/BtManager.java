package com.example.bluetoothcontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by penguin on 2016/12/8.
 */

public class BtManager {
    private BluetoothAdapter mBAdapter = null;
    private static BtManager btManager = null;
    public ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
    public ArrayList<BluetoothDevice> bDevices = new ArrayList<BluetoothDevice>();
    public ArrayList<BluetoothDevice> allDevices = new ArrayList<BluetoothDevice>();
    TextView view;
    private BluetoothDevice target;

    private BtManager(TextView textView) {
        mBAdapter = BluetoothAdapter.getDefaultAdapter();
        view = textView;
    }

    public static BtManager getInstance(TextView textView) {
        if (null == btManager) {
            btManager = new BtManager(textView);
        }
        return btManager;
    }

    public static BtManager getInstance() {
        return btManager;
    }

    public BluetoothDevice getTarget() {
        return target;
    }

    public void setTarget(BluetoothDevice target) {
        this.target = target;
    }

    public BluetoothAdapter getmBAdapter() {
        return mBAdapter;
    }

    public void setView(TextView textView) {
        this.view = textView;
    }

    public void openBluetooth(Activity activity) {
        if (null == mBAdapter) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setTitle("No bluetooth devices");
            dialog.setMessage("Your equipment does not support bluetooth, please change device");

            dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialog.show();
            return;
        }

        if (!mBAdapter.isEnabled()) {
            mBAdapter.enable();
        }
    }

    public void closeBluetooth() {
        if (mBAdapter.isEnabled()) {
            mBAdapter.disable();
        }
    }

    public boolean isDiscovering() {
        return mBAdapter.isDiscovering();
    }

    public void startDiscovery() {
        if (!mBAdapter.isDiscovering())
            mBAdapter.startDiscovery();
    }

    public void cancelScanDevice() {
        if (mBAdapter.isDiscovering())
            mBAdapter.cancelDiscovery();
    }

    public void registerBluetoothReceiver(Context mContext) {
        IntentFilter startDiscover = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        mContext.registerReceiver(mBluetoothReceiver, startDiscover);
        IntentFilter discover = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mBluetoothReceiver, discover);
        IntentFilter finishDiscover = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mContext.registerReceiver(mBluetoothReceiver, finishDiscover);
    }

    private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                view.append("start discovery bluetooth devices...\n");
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    devices.add(device);
                    allDevices.add(device);
                    view.append("name : " + device.getName() + "\n"
                            + "address : " + device.getAddress() + "\n");
                }

            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Set<BluetoothDevice> bondedDevices = mBAdapter.getBondedDevices();
                if (bondedDevices.size() > 0) {
                    for (int i = 0; i < bondedDevices.size(); i++) {
                        BluetoothDevice bDevice = bondedDevices.iterator().next();
                        bDevices.add(bDevice);
                        allDevices.add(bDevice);
                    }
                }
                view.append("discovery end...\nBonded devices: \n");
                if (bDevices.size() == 0) {
                    view.append("null\n");
                }
                for (int i = 0; i < bDevices.size(); i++) {
                    view.append("name : " + bDevices.get(i).getName() + "\n"
                            + "address : " + bDevices.get(i).getAddress() + "\n");
                }
            }
        }
    };
}
