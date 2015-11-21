package jp.co.sskyknr.simpletaskmanage.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.Objects;

public class TaskManageContentProvider extends ContentProvider{
    // Authority
    public static final String AUTHORITY = "jp.co.sskyknr.simpletaskmanage.db.TaskManageContentProvider";

    // TASK テーブル URI ID
    private static final int TASK = 1;
    // TASKS テーブル 個別 URI ID
    private static final int TASK_ID = 2;
    // STATUS テーブル URI ID
    private static final int STATUS = 3;
    // STATUS テーブル 個別 URI ID
    private static final int STATUS_ID = 4;

    // 利用者がメソッドを呼び出したURIに対応する処理を判定処理に使用します
    private static UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, TaskDbDao.PATH, TASK);
        sUriMatcher.addURI(AUTHORITY, TaskDbDao.PATH + "/#", TASK_ID);
        sUriMatcher.addURI(AUTHORITY, StatusDbDao.PATH, STATUS);
        sUriMatcher.addURI(AUTHORITY, StatusDbDao.PATH + "/#", STATUS_ID);
    }

    // DBHelperのインスタンス
    private TaskDbHelper mDBHelper;

    /** 排他制御 */
    Object mutex = new Object();

    // コンテンツプロバイダの作成
    @Override
    public boolean onCreate() {
        mDBHelper = new TaskDbHelper(getContext());
        return true;
    }

    // query実行
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        synchronized (mutex) {
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            switch (sUriMatcher.match(uri)) {
                case TASK:
                case TASK_ID:
                    queryBuilder.setTables(TaskDbDao.TABLE_NAME);
                    break;
                case STATUS:
                case STATUS_ID:
                    queryBuilder.setTables(StatusDbDao.TABLE_NAME);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }

            SQLiteDatabase db = mDBHelper.getReadableDatabase();
            Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            return cursor;
        }
    }

    // insert実行
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        synchronized (mutex) {
            String insertTable;
            Uri contentUri;
            switch (sUriMatcher.match(uri)) {
                case TASK:
                case TASK_ID:
                    insertTable = TaskDbDao.TABLE_NAME;
                    contentUri = TaskDbDao.CONTENT_URI;
                    break;
                case STATUS:
                case STATUS_ID:
                    insertTable = StatusDbDao.TABLE_NAME;
                    contentUri = StatusDbDao.CONTENT_URI;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }

            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            long rowId = 0;
            try {
                rowId = db.insert(insertTable, null, values);
            } finally {
                db.close();
            }
            if (rowId > 0) {
                Uri returnUri = ContentUris.withAppendedId(contentUri, rowId);
                getContext().getContentResolver().notifyChange(returnUri, null);
                return returnUri;
            } else {
                throw new IllegalArgumentException("Failed to insert row into " + uri);
            }
        }
    }

    // update実行
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        synchronized (mutex) {
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            int count;
            switch (sUriMatcher.match(uri)) {
                case TASK:
                    try {
                        count = db.update(TaskDbDao.TABLE_NAME, values, selection, selectionArgs);
                    } finally {
                        db.close();
                    }
                    break;
                case STATUS:
                    try {
                        count = db.update(StatusDbDao.TABLE_NAME, values, selection, selectionArgs);
                    } finally {
                        db.close();
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
    }

    // delete実行
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        synchronized (mutex) {
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            int count;
            switch (sUriMatcher.match(uri)) {
                case TASK:
                case TASK_ID:
                    try {
                        count = db.delete(TaskDbDao.TABLE_NAME, selection, selectionArgs);
                    } finally {
                        db.close();
                    }
                    break;
                case STATUS:
                case STATUS_ID:
                    try {
                        count = db.delete(StatusDbDao.TABLE_NAME, selection, selectionArgs);
                    } finally {
                        db.close();
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
    }

    // コンテントタイプ取得
    @Override
    public String getType(Uri uri) {
        switch(sUriMatcher.match(uri)) {
            case TASK:
                return TaskDbDao.CONTENT_TYPE;
            case TASK_ID:
                return TaskDbDao.CONTENT_ITEM_TYPE;
            case STATUS:
                return StatusDbDao.CONTENT_TYPE;
            case STATUS_ID:
                return StatusDbDao.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }
}