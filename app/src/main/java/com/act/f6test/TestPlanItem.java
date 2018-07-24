package com.act.f6test;

/**
 * Created by chy on 15-5-12.
 */
public class TestPlanItem {
    private String mName;
    private TestPlanKind mKind;

    public TestPlanItem(TestPlanKind kind, String name) {
        mName = name;
        mKind = kind;
    }

    public TestPlanKind getKind() {
        return mKind;
    }

    @Override
    public String toString() {
        return mName;
    }
}
