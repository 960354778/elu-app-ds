package com.qingyun.zhiyunelu.ds.record;

import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogHub;
import velites.java.utility.thread.ThreadUtil;

/**
 * Created by luohongzhen on 01/12/2017.
 */

public class RecordTest {
    private static boolean isRecording = false;
    private static MediaRecorder recorder = null;
    private static final String dirName = "ARecord";
    private static File DirFile = null;

    public static void startRecord(final String number) {
        File file = Environment.getExternalStorageDirectory();
        DirFile = new File(file, dirName);
        if (!DirFile.exists()) {
            DirFile.mkdirs();
        }

        ThreadUtil.runInNewThread(new Runnable() {
            @Override
            public void run() {
                synchronized (RecordTest.class) {
                    try {
                        if (recorder != null) {
                            recorder.stop();
                            recorder.release();
                            recorder = null;
                        }

                        recorder = new MediaRecorder();
                        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        StringBuilder builder = new StringBuilder();
                        builder.append(number).append("-").append(System.currentTimeMillis() / 1000);

                        File audioFile = File.createTempFile(builder.toString(), ".amr", DirFile);
                        recorder.setOutputFile(audioFile.getAbsolutePath());
                        recorder.prepare();
                        recorder.start();
                        isRecording = true;
                        LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, RecordTest.class, "autio path: %s", audioFile.getAbsolutePath()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        }, null, null);
    }

    public static void stopRecord() {
        ThreadUtil.runInNewThread(new Runnable() {
            @Override
            public void run() {
                synchronized (RecordTest.class) {
                    if (isRecording) {
                        isRecording = false;
                        if (recorder != null) {
                            recorder.stop();
                            recorder.reset();
                            recorder.release();
                            recorder = null;
                            LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, RecordTest.class, "stop audio record"));
                        }
                    }
                }
            }
        }, null, null);

    }
}
