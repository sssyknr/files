package jp.co.sskyknr.simpletaskmanage.dto;

import android.content.Context;

import jp.co.sskyknr.simpletaskmanage.util.colorUtil;

/**
 * リストのアイテム
 */
public class TaskListItemDto {
    private int taskId;
    private String task;
    private String stasus;
    private int sequenceId;
    private int bgId;
    private int createAt;

    public TaskListItemDto() {

    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setStasus(String stasus) {
        this.stasus = stasus;
    }

    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    public void setCreateAt(int createAt) {
        this.createAt = createAt;
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
        return stasus;
    }

    public int getSequenceId() {
        return sequenceId;
    }

    public int getCreateAt() {
        return createAt;
    }

    public int getBgId() {
        return bgId;
    }
}
