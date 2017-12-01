package com.qingyun.zhiyunelu.ds.bcst;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.qingyun.zhiyunelu.ds.record.RecordService;

import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogHub;
import velites.java.utility.misc.StringUtil;

/**
 * Created by luohongzhen on 30/11/2017.
 */

public class CallPhoneBCSTR extends BroadcastReceiver {

    public static final int PHONE_OUTGOING_TYPE = 110;
    public static final int PHONE_INCOMING_TYPE = 111;
    public static final int PHONE_IDLE_TYPE = 112;
    public static volatile int phoneCallType = PHONE_IDLE_TYPE;
    private static int phoneChangeState = TelephonyManager.CALL_STATE_IDLE;

    private static String incomingNum;
    private static String outgoingNum;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            phoneCallType = PHONE_OUTGOING_TYPE;
            outgoingNum = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            sendPhoneState(phoneChangeState, phoneCallType, outgoingNum);
        }
        LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, this, "onReceive action:%s Phone number is %s", intent.getAction(), intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)));

    }

    public static void ensureInit(Context context) {
        if (context != null) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private static PhoneStateListener listener = new PhoneStateListener() {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            phoneChangeState = state;
            incomingNum = StringUtil.isNullOrEmpty(incomingNum)? incomingNumber: incomingNum;
            if (!StringUtil.isNullOrEmpty(incomingNum) && phoneCallType != PHONE_OUTGOING_TYPE) {
                phoneCallType = PHONE_INCOMING_TYPE;
            }
            if(phoneCallType == PHONE_IDLE_TYPE)
                return;

            sendPhoneState(state, phoneCallType, phoneCallType == PHONE_OUTGOING_TYPE ? outgoingNum : (phoneCallType == PHONE_INCOMING_TYPE ? incomingNum : ""));
            if (state == TelephonyManager.CALL_STATE_IDLE) {
                incomingNum = "";
                outgoingNum = "";
                phoneCallType = PHONE_IDLE_TYPE;
            }
        }
    };

    private static void sendPhoneState(int state, int type, String num) {
        synchronized (CallPhoneBCSTR.class) {
            LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, CallPhoneBCSTR.class, "Phone state: %d callType: %d phone: %s", state, type, num));
            if (!StringUtil.isNullOrEmpty(num)) {
                //TODO start service record
                RecordService.startRS(state, type, num);
            }
        }
    }

}


