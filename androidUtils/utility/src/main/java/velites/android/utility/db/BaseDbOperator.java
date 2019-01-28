package velites.android.utility.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import velites.java.utility.generic.Action1;

/**
 * Created by regis on 17/4/21.
 */

public abstract class BaseDbOperator {

    private final SQLiteOpenHelper helper;

    protected BaseDbOperator(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        helper = new SQLiteOpenHelper(context, name, factory, getUpgradeScripts().length + 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(getCreationScript());
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                String[] scripts = getUpgradeScripts();
                for (int i = oldVersion - 1; i < scripts.length; i++) {
                    db.execSQL(scripts[i]);
                }
            }
        };
    }

    protected abstract String getCreationScript();

    protected abstract String[] getUpgradeScripts();

    /**
     * @param op
     * @param transExclusive {@code null} means no trans applied, {@code true} to apply trans with EXCLUSIVE, {@code false} uses IMMEDIATE.
     */
    protected final void operateDBSafely(Action1<SQLiteDatabase> op, Boolean transExclusive) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            if (transExclusive == null) {
                op.a(db);
            } else {
                SqliteHelper.executeWithTransaction(op, db, transExclusive);
            }
        } finally {
            db.close();
        }
    }
}
