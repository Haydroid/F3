package com.act.f6test;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Administrator on 2017/2/22 0022.
 */
public class BleDeviceItem {

    public enum ConnectState {
        CS_NONE,
        CS_CONNECTING,
        CS_CONNECTED;
    }


    protected final BluetoothDevice mDevice;
    private ConnectState mState = ConnectState.CS_NONE;

    public BleDeviceItem(BluetoothDevice device) {
        mDevice = device;
    }

    public ConnectState getConnectState() {
        return mState;
    }

    public int getBondState() {
        return mDevice.getBondState();
    }

    public String getName() {
        return mDevice.getName();
    }

    public String getAddress() {
        return mDevice.getAddress();
    }
    public void setConnectState(ConnectState state) {
        mState = state;
    }

    public boolean createBond(byte[] pinData) {
        return mDevice.createBond();
    }

    public boolean equals(Object obj) {
        if (obj instanceof  BluetoothDevice) {
            return mDevice.equals(obj);
        }
        return false;
    }
}
