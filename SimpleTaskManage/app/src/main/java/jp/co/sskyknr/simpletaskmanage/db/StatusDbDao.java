package jp.co.sskyknr.simpletaskmanage.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * タスクの状態管理についてのデータベース
 */
public class StatusDbDao {
    // ////////////////////////////////////////////////////////////////////////////////////////////
    // private static final フィールド
    // ////////////////////////////////////////////////////////////////////////////////////////////
    // URIパス
    public static final String PATH = "status";
    // コンテントURI
    public static final Uri CONTENT_URI = Uri.parse("content://" + TaskManageContentProvider.AUTHORITY + "/" + PATH);
    // テーブル指定コンテントタイプ
    public static final String CONTENT_TYPE = "vnd.android.cursor.item/vnd.sskyknr.status";
    // レコード個別指定コンテントタイプ
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.dir/vnd.sskyknr.status";

    /** テーブルの定数 */
    public static final String TABLE_NAME = "statusTable";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SEQUENCE_ID = "sequenceId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_COLOR = "color";
    public static final String[] COLUMNS = {COLUMN_ID, COLUMN_SEQUENCE_ID, COLUMN_NAME, COLUMN_COLOR};

    /** 「taskTable」テーブルの作成用SQL */
    public static final String CREATE_TABLE_SQL = "" +
            "create table " + TABLE_NAME + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_SEQUENCE_ID + " integer not null, " +
            COLUMN_NAME + " text not null, " +
            COLUMN_COLOR + " text not null" +
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
    public StatusDbDao(SQLiteDatabase db) {
        this.db = db;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // Public メソッド
    // ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 全データの取得   ----------------①
     *
     * @return
     */
    public List<StatusDbEntity> findAll(Context context) {
        List<StatusDbEntity> entityList = new ArrayList<StatusDbEntity>();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(CONTENT_URI, COLUMNS, null, null, null);

        while (cursor.moveToNext()) {
            StatusDbEntity entity = new StatusDbEntity();
            entity.setId(cursor.getInt(0));
            entity.setName(cursor.getString(1));
            entity.setSequenceId(cursor.getInt(2));
            entity.setColor(cursor.getString(3));
            entityList.add(entity);
        }

        return entityList;
    }

    /**
     * 特定IDのデータを取得   ----------------②
     *
     * @param rowId
     * @return
     */
    public StatusDbEntity findById(Context context, int rowId) {
        String selection = COLUMN_ID + "=" + rowId;
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(
                CONTENT_URI,
                COLUMNS,
                selection,
                null,
                null);

        cursor.moveToNext();
        StatusDbEntity entity = new StatusDbEntity();
        entity.setId(cursor.getInt(0));
        entity.setName(cursor.getString(1));
        entity.setSequenceId(cursor.getInt(2));
        entity.setColor(cursor.getString(3));

        return entity;
    }

    /**
     * データの登録   ----------------③
     *
     * @param context
     * @param name
     * @param sequenceId
     * @param color
     */
    public Uri insert(Context context, String name, int sequenceId, String color) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_SEQUENCE_ID, sequenceId);
        values.put(COLUMN_COLOR, color);

        return resolver.insert(CONTENT_URI, values);
    }

    /**
     * データの更新   ----------------④
     *
     * @param entity
     * @return
     */
    public int update(StatusDbEntity entity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, entity.getName());
        values.put(COLUMN_SEQUENCE_ID, entity.getSequenceId());
        values.put(COLUMN_COLOR, entity.getColor());
        String whereClause = COLUMN_ID + "=" + entity.getId();
        return db.update(TABLE_NAME, values, whereClause, null);
    }

    public int updateSequence(Context context, int id, int sequenceId) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SEQUENCE_ID, sequenceId);
        String whereClause = COLUMN_ID + "=" + id;
        String[] selection = {COLUMN_SEQUENCE_ID};
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
