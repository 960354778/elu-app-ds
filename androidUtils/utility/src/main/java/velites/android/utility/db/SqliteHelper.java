package velites.android.utility.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.Locale;

import velites.java.utility.generic.Action1;

/**
 * Created by regis on 17/4/13.
 */

public final class SqliteHelper {
    private SqliteHelper() {}

    public static final void useCursorEnsureClose(Cursor c, Action1<Cursor> a) {
        if (a == null || c == null) {
            return;
        }
        try {
            a.a(c);
        } finally {
            c.close();
        }
    }

    public static final void executeWithTransaction(Action1<SQLiteDatabase> act, SQLiteDatabase db, boolean exclusive) {
        if  (act == null || db == null) {
            return;
        }
        if (exclusive) {
            db.beginTransaction();
        } else {
            db.beginTransactionNonExclusive();
        }
        try {
            act.a(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public static final String generateWhereForId(long id) {
        return String.format(Locale.US, "%s=%d", BaseColumns._ID, id);
    }

    public static Long getLong(Cursor cursor, int col) {
        if (cursor.isNull(col)) {
            return null;
        }
        return cursor.getLong(col);
    }

    public static Long getLong(Cursor cursor, String name) {
        return getLong(cursor, cursor.getColumnIndexOrThrow(name));
    }
}
