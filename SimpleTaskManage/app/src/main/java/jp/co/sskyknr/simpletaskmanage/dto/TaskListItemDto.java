package jp.co.sskyknr.simpletaskmanage.dto;

import android.content.Context;

import jp.co.sskyknr.simpletaskmanage.util.colorUtil;

/**
 * リストのアイテム
 */
public class TaskListItemDto {
    private int taskId;
    private String task;
    private String status;
    private int sequenceId;
    private int bgId;

    public TaskListItemDto() {

    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setStasus(String stasus) {
        this.status = stasus;
    }

    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    public void setBgImage(Context context, String color) {
        this.bgId = colorUtil.setBgImage(context, color);
    }

    public int getTaskId() {
        return taskId;
    }

    public String getTask() {
        return task;
    }

    public String getStasus() {
        return status;
    }

    public int getSequenceId() {
        return sequenceId;
    }

    public int getBgId() {
        return bgId;
    }
}
