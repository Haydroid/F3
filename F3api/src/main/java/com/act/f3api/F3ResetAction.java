package com.act.f3api;

/**
 * Created by chy on 15-5-11.
 */
public enum F3ResetAction {

    RESET_NOACTION(0x33),
    RESET_FRONT_CARD(0x30),
    RESET_RETURN_CARD(0x31);

    private byte mValue;
    F3ResetAction(int value) {
        mValue = (byte)value;
    }

    public byte getValue() {
        return mValue;
    }
}
