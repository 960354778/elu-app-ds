package com.qingyun.zhiyunelu.ds;

import com.qingyun.zhiyunelu.ds.data.Setting;
import velites.java.utility.merge.ObjectMerger;

import org.junit.Test;


import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TestingUnitTest {
    @Test
    public void merge_objects() {
        Setting a = new Setting();
        a.logging = new Setting.Logging();
        a.logging.logLevel = 300;
        a.logging.suppressLogReport = true;
        Setting b = new Setting();
        b.logging = new Setting.Logging();
        b.logging.logLevel = 800;
        b.logging.suppressFileLog = true;
        b.network = new Setting.Network();
        Setting c = new ObjectMerger().merge(b, a, a);
        int v = c.logging.logLevel;
    }
}