package velites.android.support.devices.xiaomi;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import velites.java.utility.log.LogStub;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.misc.StringUtil;

public class Behavior {
    private Behavior() {}

    /**
     *
     * @param phoneNum
     * @param dirPath MIUI records sound dir path
     * @return The latest recording file path
     */
    public static String getRecentlyMiUiSoundPath(String phoneNum, String dirPath, Long timeRangeBegin, Long timeRangeEnd){
        if(!StringUtil.isNullOrEmpty(phoneNum)){
            Map<Long, File> map = new HashMap<>();
            ArrayList<Long> times = new ArrayList<>();
            File dir = new File(dirPath);
            if(dir.exists() && dir.isDirectory()){
                File[] files = dir.listFiles();
                if(files != null && files.length > 0){
                    for(File item : files){
                        if(item != null){
                            try {
                                String pathItem = item.getPath();
                                int start = pathItem.lastIndexOf("(");
                                int end = pathItem.lastIndexOf(")");
                                String num = pathItem.substring(start + 1, end);
                                if (num != null && num.equals(phoneNum)) {
                                    String timeStr = pathItem.substring(pathItem.lastIndexOf("_") + 1, pathItem.lastIndexOf("."));
                                    Long time = null;
                                    try {
                                        time = new SimpleDateFormat("yyyyMMddHHmmss").parse(timeStr).getTime();
                                    } catch (ParseException ex) {
                                        ExceptionUtil.swallowThrowable(ex);
                                    }
                                    if ((timeRangeBegin == null || time != null && time >= timeRangeBegin) && (timeRangeEnd == null || time != null && time <= timeRangeEnd)) {
                                        map.put(time, item);
                                        times.add(time);
                                    }
                                }
                            }
                            catch (Throwable ex) {
                                ExceptionUtil.swallowThrowable(ex, LogStub.LOG_LEVEL_INFO, Behavior.class, StringUtil.formatInvariant("file: %s", item));
                            }
                        }
                    }

                    if(times.size() > 0){
                        Long max =  Collections.max(times);
                        File maxTimeFile = map.get(max);
                        if(maxTimeFile != null && maxTimeFile.exists()){
                            return maxTimeFile.getAbsolutePath();
                        }
                    }
                }
            }
        }
        return null;
    }
}
