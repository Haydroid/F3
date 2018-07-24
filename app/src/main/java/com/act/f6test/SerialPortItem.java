package com.act.f6test;

/**
 * Created by chy on 15-5-12.
 */
public class SerialPortItem {
    private String _name;
    private String _fullPath;

    protected SerialPortItem(String name, String fullPath) {
        _name = name;
        _fullPath = fullPath;
    }

    public String getName() {
        return _name;
    }

    public String getFullPath() {
        return _fullPath;
    }

    @Override
    public String toString() {
        return _name;
    }
}
