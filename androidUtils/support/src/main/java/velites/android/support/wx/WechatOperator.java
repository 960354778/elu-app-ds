package velites.android.support.wx;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Xml;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import velites.android.utility.db.SqliteHelper;
import velites.android.utility.framework.EnvironmentInfo;
import velites.android.utility.root.RootUtility;
import velites.java.utility.ex.CodedException;
import velites.java.utility.generic.Action0;
import velites.java.utility.generic.Action1;
import velites.java.utility.generic.Func2;
import velites.java.utility.misc.ObjectWrapper;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;
import velites.java.utility.misc.EncryptUtil;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.misc.FileUtil;
import velites.java.utility.misc.IOUtil;
import velites.java.utility.misc.PathUtil;
import velites.java.utility.misc.StringUtil;
import velites.java.utility.misc.SyntaxUtil;
import velites.java.utility.system.ProcessRunner;

public class WechatOperator {

    public static final String DATA_ROOT_MAIN = "/data/data/com.tencent.mm";
    private static final String AUTH_INFO_PATH = "shared_prefs/auth_info_key_prefs.xml";
    private static final String MAJOR_DATA_PATH = "MicroMsg";
    private static final String SYSTEM_INFO_PATH = "MicroMsg/CompatibleInfo.cfg";
    private static final String DB_PATH_FORMAT = "MicroMsg/%s/EnMicroMsg.db";

    private static final String INDIVIDUAL_DIR_FORMAT = "mm%s";
    private static final String KEY_AUTH_UIN = "_auth_uin";
    private static final String DEVICE_ID_FALLINGDOWN = "1234567890ABCDEF";
    private static final Integer SYSTEM_INFO_KEY_DEVICE_ID = Integer.valueOf(258);

    private static final String TEMP_DB_FILE_NAME = "wechat.db";
    private static final String TEMP_AUTH_INFO_FILE_NAME = "auth.xml";
    private static final String TEMP_SYSTEM_INFO_FILE_NAME = "system.cfg";

    private static final int USER_INFO_ID_USER_NAME = 2;
    private static final int USER_INFO_ID_NICK_NAME = 4;
    private static final int USER_INFO_ID_PHONE = 6;
    private static final String USER_INFO_FIELD_ID = "id";
    private static final String USER_INFO_FIELD_VALUE = "value";

    public static final String EXCEPTION_CODE_NO_OWNER = "Wechat.NoOwner";

    private final Context context;
    private String rootPath;
    private String majorDataPath;
    private String authInfoPath;
    private String systemInfoPath;
    private String dbPathFormat;

    public static class TempFiles {
        public File root;
        public String individualDirSegment;
        public File authInfo;
        public File systemInfo;
        public File db;
    }

    public WechatOperator(Context ctx) {
        this.context = ctx;
        this.calculatePathes(DATA_ROOT_MAIN);
    }

    private void calculatePathes(String rootPath) {
        this.rootPath = rootPath;
        this.majorDataPath = PathUtil.combine(true, true, rootPath, MAJOR_DATA_PATH);
        this.authInfoPath = PathUtil.combine(true, true, rootPath, AUTH_INFO_PATH);
        this.systemInfoPath = PathUtil.combine(true, true, rootPath, SYSTEM_INFO_PATH);
        this.dbPathFormat = PathUtil.combine(true, true, rootPath, DB_PATH_FORMAT);
    }

    public void fixPermission() {
        final ObjectWrapper<Boolean> needFix = new ObjectWrapper<>();
        ExceptionUtil.executeWithRetry(() -> {
            File f = new File(majorDataPath);
            needFix.set(f.canWrite());
        }, 1, (ex, tried) -> {
            needFix.set(false);
            return true;
        });
        if (needFix.get() != null && needFix.get()) {
            RootUtility.runAsRoot(null, StringUtil.formatInvariant("chmod -R o-rw %s", rootPath), StringUtil.formatInvariant("chmod -R g-w %s", rootPath));
        }
    }

    private void prepareForWX(String tempDir, final Action1<TempFiles> act) {
        if (act == null) {
            return;
        }
        TempFiles files = new TempFiles();
        files.root = FileUtil.prepareFileForCreation(tempDir);
        if (files.root == null) {
            return;
        }
        try {
            ProcessRunner pr = ProcessRunner.runProcess(new ProcessBuilder("whoami"), ProcessRunner.defaultOptionsThrowIfExitNonZero, null);
            String[] out = pr.getStdOutput();
            String owner = out != null && out.length > 0 ? out[out.length - 1] : null;
            if (owner == null) {
                throw new CodedException(EXCEPTION_CODE_NO_OWNER, null);
            }
            Log.e("WechatOperator","authInfoPath:"+authInfoPath+"\n");
            Log.e("WechatOperator","TEMP_AUTH_INFO_FILE_NAME:"+TEMP_AUTH_INFO_FILE_NAME+"\n");
            Log.e("WechatOperator","owner:"+owner+"\n");
            files.authInfo = copyFile(authInfoPath, files.root, TEMP_AUTH_INFO_FILE_NAME, owner);
            files.systemInfo = copyFile(systemInfoPath, files.root, TEMP_SYSTEM_INFO_FILE_NAME, owner);
            files.individualDirSegment = EncryptUtil.MD5Lower(StringUtil.formatInvariant(INDIVIDUAL_DIR_FORMAT, obtainWxUin(files.authInfo)));
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_VERBOSE, WechatOperator.class, "Target wx account: %s", files.individualDirSegment));
            files.db = copyFile(StringUtil.formatInvariant(dbPathFormat, files.individualDirSegment), files.root, TEMP_DB_FILE_NAME, owner);
            act.a(files);
        } finally {
            FileUtil.deleteFile(files.root);
        }
    }

    private static File copyFile(String srcFullPath, File root, String destRelPath, String owner) {
        File ret = FileUtil.prepareFileForCreation(PathUtil.combine(true, root.getPath(), destRelPath).getFullPath());
        ProcessRunner runner = RootUtility.runAsRoot(null, StringUtil.formatInvariant("cp --preserve=c %s %s", srcFullPath, ret), StringUtil.formatInvariant("chown %s %s", owner, ret));
        if (!runner.isSuccess()) {
            int l = runner.getErrOutput().length;
            if (l > 0 && runner.getErrOutput()[l - 1].contains("Unknown option preserve=c")) {
                List<String> cmds = new ArrayList<>();
                cmds.add(StringUtil.formatInvariant("cp %s %s", srcFullPath, ret));
                runner = RootUtility.runAsRoot(ProcessRunner.defaultOptionsThrowIfExitNonZero, StringUtil.formatInvariant("ls -Z %s", srcFullPath));
                String con = runner.getStdOutput()[runner.getStdOutput().length - 1];
                Matcher m = Pattern.compile("(?:^|\\s)(\\S+:\\S+:\\S+:\\S+:\\S+)\\s").matcher(con);
                if (m.find())
                {
                    con = m.group(1);
                    cmds.add(StringUtil.formatInvariant("chcon %s %s", con, ret));
                }
                cmds.add(StringUtil.formatInvariant("chown %s %s", owner, ret));
                RootUtility.runAsRoot(ProcessRunner.defaultOptionsThrowIfExitNonZero, cmds.toArray(new String[0]));

            } else {
                Log.e("WechatOperator","异常------------------------------------");
                runner.checkThrowExecFailureException();
            }
        }
        return ret;
    }

    private void decodeDB(Context context, TempFiles files, Action1<SQLiteDatabase> act) {
        SQLiteDatabase.loadLibs(this.context);
        SQLiteDatabase db = null;
        if (files != null && files.db != null && files.db.exists()) {
            try {
                String pwd = obtainPassword(context,files.authInfo, files.systemInfo);
                LogStub.log(new LogEntry(LogStub.LOG_LEVEL_VERBOSE, WechatOperator.class, "Using password for wx db(%s): %s", files.db, pwd));
                db = SQLiteDatabase.openOrCreateDatabase(files.db, pwd, null, new SQLiteDatabaseHook() {
                    public void preKey(SQLiteDatabase database) {
                    }
                    public void postKey(SQLiteDatabase database) {
                        database.rawExecSQL("PRAGMA cipher_migrate;");
                    }
                });
                LogStub.log(new LogEntry(LogStub.LOG_LEVEL_VERBOSE, WechatOperator.class, "Opened wx db: %s", files.db));
                if (act != null) {
                    act.a(db);
                }
            } finally {
                if(db != null) {
                    db.close();
                    LogStub.log(new LogEntry(LogStub.LOG_LEVEL_VERBOSE, WechatOperator.class, "Closed wx db: %s", files.db));
                }
            }
        }
    }

    public void checkWechatAndRun(Context context,String tempDir, final Action1<SQLiteDatabase> act) {
        if (act == null) {
            return;
        }
        RootUtility.assertRooted();
        prepareForWX(tempDir, new Action1<TempFiles>() {
            @Override
            public void a(TempFiles files) {
                decodeDB(context,files, new Action1<SQLiteDatabase>() {
                    @Override
                    public void a(SQLiteDatabase db) {
                        act.a(db);
                    }
                });
            }
        });
    }

    public static void exportDecodedDB(SQLiteDatabase database, String targetPath) {
        if (StringUtil.isNullOrEmpty(targetPath)) {
            targetPath = PathUtil.concat(PathUtil.parse(database.getPath()).getDirectory(), "decrypted_database.db");
        }
        File decrypted = FileUtil.prepareFileForCreation(targetPath);
        LogStub.log(new LogEntry(LogStub.LOG_LEVEL_VERBOSE, WechatOperator.class, "Exporting wx db to: %s", decrypted));
        database.rawExecSQL(StringUtil.formatInvariant("ATTACH DATABASE \"%s\" AS decrypted_database KEY \"\";SELECT sqlcipher_export(\"decrypted_database\");DETACH DATABASE decrypted_database;", decrypted));
    }

    private static String obtainWxUin(File authInfo) {
        String value = "";
        try {
            XmlPullParser pullParser = Xml.newPullParser();
            pullParser.setInput(new FileInputStream(authInfo), "UTF-8");
            for (int event = pullParser.getEventType(); event != 1; event = pullParser.next()) {
                switch (event) {
                    case 2:
                        if ("int".equals(pullParser.getName()) && pullParser.getAttributeValue("", "name").equals(KEY_AUTH_UIN)) {
                            value = pullParser.getAttributeValue("", "value");
                            break;
                        }
                }
                if (!StringUtil.isNullOrEmpty(value)) {
                    break;
                }
            }
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_VERBOSE, WechatOperator.class, "Using wx uin: %s", value));
            return value;
        } catch (Exception ex) {
            throw ExceptionUtil.checkWrapperAsRuntime(ex);
        }
    }

    private static String obtainDeviceId(File systemInfo) {
        String id = null;
        if (systemInfo != null && systemInfo.exists() && systemInfo.length() > 0) {
            FileInputStream fis = null;
            ObjectInputStream ois = null;
            try {
                fis = new FileInputStream(systemInfo);
                ois = new ObjectInputStream(fis);
                Map<Integer, Object> values = (Map<Integer, Object>) ois.readObject();
                id = (String) values.get(SYSTEM_INFO_KEY_DEVICE_ID);
                LogStub.log(new LogEntry(LogStub.LOG_LEVEL_VERBOSE, WechatOperator.class, "Cached device id of wx: %s", id));
            } catch (Throwable ex) {
                ExceptionUtil.swallowThrowable(ex);
            }
            finally {
                IOUtil.silentClose(fis);
                IOUtil.silentClose(ois);
            }
        }
        if (id == null) {
            id = EnvironmentInfo.getPrototype().deviceId;
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_VERBOSE, WechatOperator.class, "Taking device id from system: %s", id));
        }
        if (id == null) {
            id = DEVICE_ID_FALLINGDOWN;
        }
        LogStub.log(new LogEntry(LogStub.LOG_LEVEL_VERBOSE, WechatOperator.class, "Using device id for wx: %s", id));
        return id;
    }

    @SuppressLint({"HardwareIds", "MissingPermission"})
    private static String obtainPassword(Context context, File authInfo, File systemInfo) {
        Log.e("WechatOperator","authInfo:"+authInfo+"\n"+"systemInfo:"+systemInfo);
        String pwd = null;
        String uin = obtainWxUin(authInfo);
        String deviceId = obtainDeviceId(systemInfo);
        Pattern pattern = Pattern.compile("[0-9]*");
        if (pattern.matcher(deviceId).matches()){

        }else {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = tm.getDeviceId();
        }
        Log.e("WechatOperator","uin:"+uin+"\n"+"deviceId:"+deviceId);
        if(!StringUtil.isNullOrEmpty(uin) && !StringUtil.isNullOrEmpty(deviceId)){
            pwd = EncryptUtil.MD5(deviceId+uin).substring(0, 7).toLowerCase();
        }
        Log.e("WechatOperator","pwd:"+pwd);
        return pwd;
    }

    public final WechatMeInfo obtainMe(SQLiteDatabase sql) {
        if (sql != null) {
            String cmd = StringUtil.formatInvariant("SELECT * FROM userinfo WHERE %s in (%s)", USER_INFO_FIELD_ID, StringUtil.join(true, ",", USER_INFO_ID_NICK_NAME, USER_INFO_ID_PHONE, USER_INFO_ID_USER_NAME));
            Cursor c1 = sql.rawQuery(cmd, null);
            if (c1 != null) {
                int idxColumnId = c1.getColumnIndex(USER_INFO_FIELD_ID);
                int idxColumnValue = c1.getColumnIndex(USER_INFO_FIELD_VALUE);
                WechatMeInfo ret = new WechatMeInfo();
                while (c1.moveToNext()) {
                    if (c1.getInt(idxColumnId) == USER_INFO_ID_USER_NAME) {
                        ret.userName = c1.getString(idxColumnValue);
                    } else if (c1.getInt(idxColumnId) == USER_INFO_ID_NICK_NAME) {
                        ret.nickName = c1.getString(idxColumnValue);
                    } else if (c1.getInt(idxColumnId) == USER_INFO_ID_PHONE) {
                        ret.phone = c1.getString(idxColumnValue);
                    }
                }
                c1.close();
                return ret;
            }
        }
        return null;
    }

    public final WechatFriendInfo[] fetchFriends(SQLiteDatabase sql) {
        List<WechatFriendInfo> list = new ArrayList<>();
        String cmd = "select *, (select max(m.createTime) from message m where m.talker = username) as lastChatTime from rcontact";
        SqliteHelper.useCursorEnsureClose(sql.rawQuery(cmd, null), c -> {
            while (c.moveToNext()) {
                WechatFriendInfo f = new WechatFriendInfo();
                f.userName = c.getString(c.getColumnIndex("username"));
                f.userAlias = c.getString(c.getColumnIndex("alias"));
                f.conRemark = c.getString(c.getColumnIndex("conRemark"));
                f.nickName = c.getString(c.getColumnIndex("nickname"));
                f.conRemarkPy = c.getString(c.getColumnIndex("conRemarkPYFull"));
                f.lastChatTime = SqliteHelper.getLong(c, "lastChatTime");
                f.type = c.getInt(c.getColumnIndex("type"));
                list.add(f);
            }
        });
        return list.toArray(new WechatFriendInfo[0]);
    }

    public final WechatMessageInfo[] fetchMessages(SQLiteDatabase sql, String userName,  Long lastUpdateTime, Long untilTimestamp) {
        List<WechatMessageInfo> chats = new ArrayList<>();
        String cmd = "SELECT * FROM message WHERE talker = ? AND createTime >= ?" + (untilTimestamp == null ? "" : " AND createTime < ?") + " ORDER BY createTime ASC";
        List<String> params = new ArrayList<>();
        params.add(userName);
        params.add(String.valueOf(SyntaxUtil.nvl(lastUpdateTime)));
        if (untilTimestamp != null) {
            params.add(String.valueOf(untilTimestamp));
        }
        SqliteHelper.useCursorEnsureClose(sql.rawQuery(cmd, params.toArray(new String[0])), c -> {
            while (c.moveToNext()) {
                WechatMessageInfo chat = new WechatMessageInfo();
                chat.msgId = c.getInt(c.getColumnIndex("msgId"));
                chat.msgSvrId = SqliteHelper.getLong(c, "msgSvrId");
                chat.status = c.getInt(c.getColumnIndex("status"));
                chat.type = c.getInt(c.getColumnIndex("type"));
                chat.createTime = c.getLong(c.getColumnIndex("createTime"));
                chat.content = c.getString(c.getColumnIndex("content"));
                chat.isSend = c.getInt(c.getColumnIndex("isSend")) != 0;
                chats.add(chat);
            }
        });
        return chats.toArray(new WechatMessageInfo[0]);
    }
}
