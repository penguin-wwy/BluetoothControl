package com.example.bluetoothcontrol;

/**
 * Created by penguin on 2016/12/9.
 */

public class Msg {
    public static final int TYPE_RECE = 0;
    public static final int TYPE_SEND = 1;
    public static final int TYPE_OTHE = 2;
    private int type;
    private String data;

    public Msg(String data, int type) {
        this.type = type;
        this.data = data;
    }

    public int getType() {
        return this.type;
    }

    public String getData() {
        return this.data;
    }
}

