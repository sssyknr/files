package jp.co.sskyknr.simpletaskmanage.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import jp.co.sskyknr.simpletaskmanage.db.TaskDbEntity;
import jp.co.sskyknr.simpletaskmanage.dto.TaskListItemDto;

/**
 * タスク追加ダイアログ
 */
public class DeleteTaskDialogFragment extends DialogFragment{
    public static final String TAG = DeleteTaskDialogFragment.class.getCanonicalName();

    /** デフォルトコンストラクタ */
    public DeleteTaskDialogFragment() {}

    /** 消去対象アイテム */
    private TaskListItemDto mDeleteItem;

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // Private フィールド
    // ////////////////////////////////////////////////////////////////////////////////////////////

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // Override メソッド
    // ////////////////////////////////////////////////////////////////////////////////////////////

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("タスクの消去");
        builder.setMessage("選択したタスクを消去しますか？");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getActivity() instanceof deleteDialogCallback) {
                    // deleteDialogCallbackが実装されていた場合
                    ((deleteDialogCallback) getActivity()).onOkClick(mDeleteItem);
                }
            }
        });
        builder.setNegativeButton("キャンセル", null);

        return builder.create();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getDialog() != null) {
            dismiss();
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // public メソッド
    // ////////////////////////////////////////////////////////////////////////////////////////////
    public void setDeleteItem(TaskListItemDto entity) {
        mDeleteItem = entity;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // interface
    // ////////////////////////////////////////////////////////////////////////////////////////////
    public interface deleteDialogCallback {
        void onOkClick(TaskListItemDto entity);
    }
}
