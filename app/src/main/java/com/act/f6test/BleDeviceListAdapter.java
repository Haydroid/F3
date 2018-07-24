package com.act.f6test;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/22 0022.
 */
public class BleDeviceListAdapter extends BaseAdapter {
    private ArrayList<BleDeviceItem> mLeDevices;
    private LayoutInflater mInflator;
    private Activity mContext;

    public BleDeviceListAdapter(Activity c) {
        super();
        mContext = c;
        mLeDevices = new ArrayList<BleDeviceItem>();
        mInflator = mContext.getLayoutInflater();
    }

    public void addDevice(BluetoothDevice device) {
        for (int i = 0; i < mLeDevices.size(); i++) {
            if (mLeDevices.get(i).equals(device))
                return;
        }
        mLeDevices.add(new BleDeviceItem(device));
    }

    public BleDeviceItem getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflator.inflate(R.layout.listitem_device, null);//xml链接
            viewHolder = new ViewHolder();
            viewHolder.deviceAddress = (TextView) view
                    .findViewById(R.id.device_address);
            viewHolder.deviceName = (TextView) view
                    .findViewById(R.id.device_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BleDeviceItem device = mLeDevices.get(i);
        String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0) {
            switch(device.getConnectState()) {
                case CS_CONNECTING:
                    deviceName += " [ 正在连接... ]";
                    break;
                case CS_CONNECTED:
                    deviceName += " [当前连接方式： 蓝牙]";

                    break;
                default:
                    switch(device.getBondState()) {
                        case BluetoothDevice.BOND_BONDING:
                            deviceName += " [ 正在配对... ]";
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            deviceName += " [ 已配对 ]";
                            break;
                    }
            }
            viewHolder.deviceName.setText(deviceName);
        }
        else
            viewHolder.deviceName.setText(R.string.unknown_device);
        viewHolder.deviceAddress.setText(device.getAddress());
        return view;
    }

    class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}