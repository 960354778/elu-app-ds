package velites.java.utility.log;

/**
 * Created by luohongzhen on 14/01/2018.
 */

public final class LogReport {

    private static Builder mBuilder;

    public static void initialize(Builder builder){
        mBuilder = builder;
    }

    public static Builder getBuilder(){
        return mBuilder;
    }

    public static class Builder{
        /**
         * set default cache size is 50M
         */
        private final long DEFAULT_CACHE_SIZE = 50 * 1024 * 1024;

        private long mCacheSize = DEFAULT_CACHE_SIZE;
        private String mReportDir;
        private LogProcessor mSigleLooperLogPreocessor;
        private LogProcessor mReportLogProcessor;
        private boolean enable;

        public long getmCacheSize() {
            return mCacheSize;
        }

        public Builder setCacheSize(long mCacheSize) {
            this.mCacheSize = mCacheSize;
            return this;
        }

        public String getReportDir() {
            return mReportDir;
        }

        public Builder setReportDir(String mReportDir) {
            this.mReportDir = mReportDir;
            return this;
        }

        public LogProcessor getLogProcessor() {
            return mSigleLooperLogPreocessor;
        }

        public Builder setLogProcessor(LogProcessor mProcessor) {
            this.mSigleLooperLogPreocessor = mProcessor;
            return this;
        }

        public LogProcessor getReportProcessor() {
            return mReportLogProcessor;
        }

        public Builder setReportProcessor(LogProcessor mProcessor) {
            this.mReportLogProcessor = mProcessor;
            return this;
        }

        public boolean isEnable() {
            return enable;
        }

        public Builder setEnable(boolean enable) {
            this.enable = enable;
            return this;
        }

        public Builder builder(){
            mCacheSize = mCacheSize == 0L?DEFAULT_CACHE_SIZE: mCacheSize;
            return this;
        }
    }


}
