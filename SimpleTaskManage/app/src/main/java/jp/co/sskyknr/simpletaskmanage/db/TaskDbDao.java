package jp.co.sskyknr.simpletaskmanage.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * タスク管理データアクセスオブジェクト
 */
public class TaskDbDao {

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // private static final フィールド
    // ////////////////////////////////////////////////////////////////////////////////////////////
    // URIパス
    public static final String PATH = "task";
    // コンテントURI
    public static final Uri CONTENT_URI = Uri.parse("content://" + TaskManageContentProvider.AUTHORITY + "/" + PATH);
    // テーブル指定コンテントタイプ
    public static final String CONTENT_TYPE = "vnd.android.cursor.item/jp.co.sskyknr.tasks";
    // レコード個別指定コンテントタイプ
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.dir/jp.co.sskyknr.tasks";

    /**
     * テーブルの定数
     */
    public static final String TABLE_NAME = "taskTable";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TASK = "task";
    public static final String COLUMN_STATUS = "statusId";
    public static final String COLUMN_CREATED_AT = "createdAt";
    public static final String[] COLUMNS = {COLUMN_ID, COLUMN_TASK, COLUMN_STATUS, COLUMN_CREATED_AT};

    /**
     * 「taskTable」テーブルの作成用SQL
     */
    public static final String CREATE_TABLE_SQL = "" +
            "create table " + TABLE_NAME + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_TASK + " text not null, " +
            COLUMN_STATUS + " integer not null, " +
            COLUMN_CREATED_AT + " integer not null" +
            ")";
    // ////////////////////////////////////////////////////////////////////////////////////////////
    // Private フィールド
    // ////////////////////////////////////////////////////////////////////////////////////////////

    // SQLiteDatabase
    private SQLiteDatabase db;

    /**
     * コンストラクタ
     *
     * @param db
     */
    public TaskDbDao(SQLiteDatabase db) {
        this.db = db;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // Public メソッド
    // ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * データの登録   ----------------③
     *
     * @param task
     * @param statusId
     * @return
     */
    public Uri insert(Context context, String task, int statusId, long createAt) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK, task);
        values.put(COLUMN_STATUS, statusId);
        values.put(COLUMN_CREATED_AT, createAt);

        return resolver.insert(CONTENT_URI, values);
    }

    public int updateStatus(Context context, int id, int statusId) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, statusId);
        String whereClause = COLUMN_ID + "=" + id;
        String[] selection = {COLUMN_STATUS};
        return resolver.update(CONTENT_URI, values, whereClause, null);
    }

    public int updateTask(Context context, int id, String task) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK, task);
        String whereClause = COLUMN_ID + "=" + id;
        String[] selection = {COLUMN_TASK};
        return resolver.update(CONTENT_URI, values, whereClause, null);
    }

    /**
     * データの削除   ----------------⑤
     *
     * @param rowId
     * @return
     */
    public int delete(Context context, int rowId) {
        ContentResolver resolver = context.getContentResolver();
        String whereClause = COLUMN_ID + "=" + rowId;
        return resolver.delete(CONTENT_URI, whereClause, null);
    }
}