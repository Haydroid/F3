package com.act.f3api;

/**
 * Created by chy on 15-5-11.
 */
public enum F3MoveMethod {

    MOVE_TOCARDOUT(0x39),//前端弹出卡片
    MOVE_TOFRONTEND(0x30),//前端出卡口
    MOVE_TORFREAD(0x32),//射频位
    CAPTURE_TO_BOX(0x33),//回收
    MOVE_TOICCPOS(0x31);//移动IC位

    private byte mValue;
    F3MoveMethod(int value) {
        mValue = (byte)value;
    }

    public byte getValue() {
        return mValue;
    }
}