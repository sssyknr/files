package jp.co.sskyknr.simpletaskmanage.adapter;

import java.util.Comparator;

import jp.co.sskyknr.simpletaskmanage.db.StatusDbEntity;

/**
 * 状態リストシーケンス順
 */
public class StatusListSequenceComparator implements Comparator<StatusDbEntity> {
    @Override
    public int compare(StatusDbEntity lhs, StatusDbEntity rhs) {
        if (lhs.getSequenceId() > rhs.getSequenceId()) {
            return 1;
        } else if (lhs.getSequenceId() < rhs.getSequenceId()) {
            return -1;
        } else {
            // 同値の場合idで比較
            if (lhs.getId() > rhs.getId()) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
