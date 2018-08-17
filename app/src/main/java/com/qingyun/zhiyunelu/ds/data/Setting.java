package com.qingyun.zhiyunelu.ds.data;

import java.util.concurrent.TimeUnit;

public class Setting {
    public static class Logging {
        public Integer logLevel;
        public Boolean suppressPrimitiveLog;
        public Boolean suppressFileLog;
        public Boolean suppressLogReport;
    }
    public static class Network {
        public String apiRootUrl;
        public Long connectTimeoutMs;
        public Long readTimeoutMs;
        public Long writeTimeoutMs;
    }
    public static class Format {
        public String defaultDateTime;
    }

    public Network network;
    public Logging logging;
    public Format format;
}