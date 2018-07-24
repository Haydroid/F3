package com.act.f3api;

/**
 * Created by xsh on 18-5-11.
 */
public class F3CardPosition {
    public byte	bLaneStatus; //卡片位置 : 机内无卡(0x30), 卡在出卡口位(0x31),机内有卡(0x32)
    public byte	bCardBoxStatus;//卡箱状态: 卡箱空(0x30), 卡箱卡少(0x31),卡箱卡足(0x32)
    public boolean	fCaptureBoxFull;//回收箱状态： 满（true）,未满（false）
}