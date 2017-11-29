package com.qingyun.zhiyunelu.ds.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import velites.android.utility.db.BaseDbOperator;
import velites.android.utility.db.SqliteHelper;
import velites.java.utility.generic.Action1;

/**
 * Created by regis on 17/3/31.
 */
public final class DbOperator extends BaseDbOperator {

    static final String DB_NAME = "data.db";

    private static final String TB_WXSYNC = "wx_sync";
    private static final String COL_WXSYNC_WXID = "wxid";

    private static final String TB_TASK_SQL_CREATE_1 = "CREATE TABLE " +
            TB_WXSYNC + "(" +
            BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_WXSYNC_WXID + " TEXT)";


    public DbOperator(Context context) {
        super(context, DB_NAME, null);
    }

    @Override
    protected String getCreationScript() {
        return TB_TASK_SQL_CREATE_1;
    }

    @Override
    protected String[] getUpgradeScripts() {
        return new String[0];
    }

    private WxSyncInfo wxSyncFromCursor(Cursor c) {
        WxSyncInfo data = new WxSyncInfo();
        data.id = c.getLong(c.getColumnIndex(BaseColumns._ID));
        data.wxid = c.getString(c.getColumnIndex(COL_WXSYNC_WXID));
        return data;
    }

    private ContentValues wxSyncToContent(WxSyncInfo data) {
        ContentValues content = new ContentValues();
        if (data.id > 0) {
            content.put(BaseColumns._ID, data.id);
        }
        content.put(COL_WXSYNC_WXID, data.wxid);
        return content;
    }

    public void insertWxSync(final WxSyncInfo data) {
        operateDBSafely(new Action1<SQLiteDatabase>() {
            @Override
            public void a(SQLiteDatabase db) {
                long id = db.insert(TB_WXSYNC, null, wxSyncToContent(data));
                if (id >= 0) {
                    data.id = id;
                }
            }
        }, true);
    }

    public void updateWxSync(final WxSyncInfo data) {
        operateDBSafely(new Action1<SQLiteDatabase>() {
            @Override
            public void a(SQLiteDatabase db) {
                db.update(TB_WXSYNC, wxSyncToContent(data), SqliteHelper.generateWhereForId(data.id), null);
            }
        }, true);
    }

    public void deleteWxSync(final long id) {
        operateDBSafely(new Action1<SQLiteDatabase>() {
            @Override
            public void a(SQLiteDatabase db) {
                db.delete(TB_WXSYNC, SqliteHelper.generateWhereForId(id), null);
            }
        }, true);
    }

    public void deleteWxSync(final WxSyncInfo data) {
        deleteWxSync(data.id);
    }

    public List<WxSyncInfo> obtainAllWxSync() {
        final List<WxSyncInfo> datas = new ArrayList<>();
        operateDBSafely(new Action1<SQLiteDatabase>() {
            @Override
            public void a(SQLiteDatabase db) {
                final Cursor c = db.query(TB_WXSYNC, null, null, null, null, null, BaseColumns._ID);
                SqliteHelper.useCursorEnsureClose(c, new Action1<Cursor>() {
                    @Override
                    public void a(Cursor arg1) {
                        while (c.moveToNext()) {
                            datas.add(wxSyncFromCursor(c));
                        }
                    }
                });
            }
        }, true);
        return datas;
    }
}
