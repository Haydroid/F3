package com.act.f6test;

import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by chy on 15-5-12.
 */
public class SerialPortFinder {
    public class Driver {
        public Driver(String name, String root) {
            Log.i("chw", "SerialPortFinder ---> Driver");
            mDriverName = name;
            mDeviceRoot = root;
        }

        private String mDriverName;
        private String mDeviceRoot;
        Vector<File> mDevices = null;

        public Vector<File> getDevices() {
            Log.i("chw", "SerialPortFinder ---> getDevices");

            if (mDevices == null) {
                mDevices = new Vector<File>();
                File dev = new File("/dev");
                File[] files = dev.listFiles();
                int i;
                for (i = 0; i < files.length; i++) {
                    if (files[i].getAbsolutePath().startsWith(mDeviceRoot)) {
                        Log.d(TAG, "Found new device: " + files[i]);
                        mDevices.add(files[i]);
                    }
                }
            }
            return mDevices;
        }

        public String getName() {
            Log.i("chw", "SerialPortFinder ---> getName");
            return mDriverName;
        }
    }

    private static final String TAG = "SerialPort";

    private Vector<Driver> mDrivers = null;

    Vector<Driver> getDrivers() throws IOException {

        Log.i("chw", "SerialPortFinder ---> getDrivers");

        if (mDrivers == null) {
            mDrivers = new Vector<Driver>();
            LineNumberReader r = new LineNumberReader(new FileReader("/proc/tty/drivers"));
            String l;
            while ((l = r.readLine()) != null) {
                String[] w = l.split(" +");
                if ((w.length == 5) && (w[4].equals("serial"))) {
                    Log.d(TAG, "Found new driver: " + w[1]);
                    mDrivers.add(new Driver(w[0], w[1]));
                }
            }
            r.close();
        }
        return mDrivers;
    }

    public Vector<SerialPortItem> getAllPorts() {
        Vector<SerialPortItem> ports = new Vector<SerialPortItem>();
        Iterator<Driver> itdriv;
        try {
            itdriv = getDrivers().iterator();
            while (itdriv.hasNext()) {
                Driver driver = itdriv.next();
                Iterator<File> itdev = driver.getDevices().iterator();
                while (itdev.hasNext()) {
                    File file = itdev.next();
                    String name = String.format("%s (%s)", file.getName(), driver.getName());
                    String fullPath = file.getAbsolutePath();
                    ports.add(new SerialPortItem(name, fullPath));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ports;
    }
}
