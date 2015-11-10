package jp.co.sskyknr.simpletaskmanage.db;

import android.content.ContentValues;

/**
 * タスク管理データベース用Entity
 */
public class TaskDbEntity {

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // private フィールド
    // ////////////////////////////////////////////////////////////////////////////////////////////
    /** ID */
    private int rowId;
    /** タスク */
    private String task;
    /** タスクステータス */
    private int statusId;
    /** タスク作成日 */
    private int createdAt;

    public TaskDbEntity() {}

    public TaskDbEntity(ContentValues values) {
        task = (String) values.get(TaskDbDao.COLUMN_TASK);
        statusId = (int) values.get(TaskDbDao.COLUMN_STATUS);
        createdAt = (int) values.get(TaskDbDao.COLUMN_CREATED_AT);
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // Public メソッド
    // ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * IDのセッター
     *
     * @param rowId
     */
    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    /**
     * IDのゲッター
     *
     * @return
     */
    public int getRowId() {
        return rowId;
    }

    /**
     * タスクのセッター
     *
     * @param task
     */
    public void setTask(String task) {
        this.task = task;
    }

    /**
     * タスクのゲッター
     *
     * @return
     */
    public String getTask() {
        return task;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public int getCreatedAt() {
        return createdAt;
    }
}
