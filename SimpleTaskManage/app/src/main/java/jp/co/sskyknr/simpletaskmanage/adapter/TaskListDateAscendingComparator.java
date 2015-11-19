package jp.co.sskyknr.simpletaskmanage.adapter;

import java.util.Comparator;

import jp.co.sskyknr.simpletaskmanage.dto.TaskListItemDto;

/**
 * 作成日時昇順ソート
 */
public class TaskListDateAscendingComparator implements Comparator<TaskListItemDto> {

    @Override
    public int compare(TaskListItemDto lhs, TaskListItemDto rhs) {
        if (lhs.createAt > rhs.createAt) {
            return 1;
        } else if (lhs.createAt < rhs.createAt) {
            return -1;
        } else {
            // 同値の場合idで比較
            if (lhs.getTaskId() > rhs.getTaskId()) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
