package com.qingyun.zhiyunelu.ds;

import android.os.SystemClock;

import com.qingyun.zhiyunelu.ds.data.Setting;

import io.reactivex.Single;
import velites.java.utility.merge.ObjectMerger;

import org.junit.Test;


import java.util.Date;
import java.util.concurrent.TimeUnit;

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
        Setting c = new ObjectMerger(true).merge(b, a, a);
        int v = c.logging.logLevel;
    }

    @Test
    public void rxDeelay() {
        Single<Integer> s1 = Single.just(0).delay(3, TimeUnit.SECONDS );
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long t1 = new Date().getTime();
        s1.subscribe(integer -> {
            long t2 = new Date().getTime();
            long t = t2-t1;
        });
    }
}