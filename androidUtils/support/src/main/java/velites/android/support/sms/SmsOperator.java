package velites.android.support.sms;

import android.content.Context;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import velites.android.utility.db.SqliteHelper;
import velites.java.utility.misc.SyntaxUtil;

public class SmsOperator {
    private static final Uri uri = Uri.parse("content://sms/");

    private final Context context;

    public SmsOperator(Context ctx) {
        this.context = ctx;
    }

    public final SmsContactInfo[] fetchContacts() {
        List<SmsContactInfo> list = new ArrayList<>();
        SqliteHelper.useCursorEnsureClose(context.getContentResolver().query(uri, new String[]{"address", "max(date) as lastChatTime"},"0=0) GROUP BY (address",null,null), c -> {
            while (c.moveToNext()) {
                SmsContactInfo v = new SmsContactInfo();
                v.phoneTo = c.getString(c.getColumnIndex("address"));
                v.lastChatTime = SqliteHelper.getLong(c, "lastChatTime");
                list.add(v);
            }
        });
        return list.toArray(new SmsContactInfo[0]);
    }

    public final SmsMessageInfo[] fetchMessages(String address, Long lastUpdateTime, Long untilTimestamp) {
        List<SmsMessageInfo> list = new ArrayList<>();
        String sel = "address = ? AND date >= ?" + (untilTimestamp == null ? "" : " AND date < ?");
        List<String> params = new ArrayList<>();
        params.add(address);
        params.add(String.valueOf(SyntaxUtil.nvl(lastUpdateTime)));
        if (untilTimestamp != null) {
            params.add(String.valueOf(untilTimestamp));
        }
        SqliteHelper.useCursorEnsureClose(context.getContentResolver().query(uri, new String[]{"_id", "thread_id", "address", "body", "date","type"},sel, params.toArray(new String[0]),"date asc"), c -> {
            while (c.moveToNext()) {
                SmsMessageInfo v = new SmsMessageInfo();
                int type = c.getInt(c.getColumnIndex("type"));
                if (type == 1) {
                    v.isSend = false;
                } else if (type == 2) {
                    v.isSend = true;
                } else {
                    continue;
                }
                v.msgId = c.getString(c.getColumnIndex("_id"));
                v.content = c.getString(c.getColumnIndex("body"));
                v.createTime = c.getString(c.getColumnIndex("date"));
                list.add(v);
            }
        });
        return list.toArray(new SmsMessageInfo[0]);
    }
}
