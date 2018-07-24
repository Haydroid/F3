package com.act.f6test;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/2/22 0022.
 */
public class DeviceScanActivity  extends ActionBarActivity {
    private ListView mLvDevices;
    private BleDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothDevice deviceBlue;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> bluetoothDevices = new ArrayList<String>();

    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mLeDeviceListAdapter.addDevice(device);
                mLeDeviceListAdapter.notifyDataSetChanged();
            }
            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                mLeDeviceListAdapter.notifyDataSetChanged();
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                onScanFinished();
            }
        }
    };

    class ItemClickEvent implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            String s = arrayAdapter.getItem(arg2);
            String address = s.substring(s.indexOf(":") + 1).trim();

            try {
                if (mBluetoothAdapter.isDiscovering()) {
                    //this.mBluetoothAdapter.cancelDiscovery();
                }

                if (deviceBlue == null) {
                    deviceBlue = mBluetoothAdapter.getRemoteDevice(address);
                }
            }catch (Exception e) {

            }

            String strMsg;
            strMsg = String.format("蓝牙地址： %s",address);

            boolean blconnect = false;
            try {
                blconnect = Application.cr.connect(address,0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.i("蓝牙",strMsg + "blconnect = " + blconnect);

            if(blconnect) {
                mLeDeviceListAdapter.notifyDataSetChanged();
                Application.mainView.setTitle(getString(R.string.app_name) + "[当前连接方式：蓝牙]");
                Toast.makeText(getApplication(), "蓝牙连接成功!!",
                        Toast.LENGTH_LONG).show();
                finish();
            }else {
                Toast.makeText(getApplication(), "蓝牙连接失败!!",
                        Toast.LENGTH_LONG).show();
            }


        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);

        mLvDevices = (ListView)findViewById(R.id.lvDevices);
        mLvDevices.setOnItemClickListener(new ItemClickEvent());

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            MessageBox.showError(R.string.ble_not_supported, this);
            finish();
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            MessageBox.showError(R.string.error_bluetooth_not_supported, this);
            finish();
            return;
        }
        else if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

        //Initializes list view adapter.
        mLeDeviceListAdapter = new BleDeviceListAdapter(this);
        mLvDevices.setAdapter(mLeDeviceListAdapter);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        FindBluetooth();
    }

    public void  FindBluetooth()
    {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                .getBondedDevices();

        if (pairedDevices.size() > 0)
        {
            for (BluetoothDevice device : pairedDevices)
            {
                bluetoothDevices.add(device.getName() + ":"
                        + device.getAddress() + "\n");
            }
        }

        arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,
                bluetoothDevices);

        mLvDevices.setAdapter(arrayAdapter);
        mLvDevices.setOnItemClickListener(new ItemClickEvent());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private void onScanFinished()
    {
        mScanning = false;
        invalidateOptionsMenu();
    }
}