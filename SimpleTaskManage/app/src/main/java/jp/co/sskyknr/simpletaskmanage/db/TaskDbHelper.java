package jp.co.sskyknr.simpletaskmanage.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * データベース管理クラス
 */
public class TaskDbHelper extends SQLiteOpenHelper{
    private static final String DB_NAME = "taskManageDb";

    /** 「taskTable」テーブルの削除用SQL */
    private static final String DROP_TABLE_SQL = "drop table if exists memo";

    /**
     * コンストラクタ（必須）
     * @param context
     */
    public TaskDbHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    /**
     * テーブルの生成（必須）
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TaskDbDao.CREATE_TABLE_SQL);
        db.execSQL(StatusDbDao.CREATE_TABLE_SQL);
    }

    /**
     * テーブルの再作成（必須）
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_SQL);
        db.execSQL(TaskDbDao.CREATE_TABLE_SQL);
        db.execSQL(StatusDbDao.CREATE_TABLE_SQL);
    }
}
