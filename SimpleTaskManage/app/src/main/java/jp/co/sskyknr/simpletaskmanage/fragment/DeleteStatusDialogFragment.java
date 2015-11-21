package jp.co.sskyknr.simpletaskmanage.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import jp.co.sskyknr.simpletaskmanage.db.StatusDbEntity;

/**
 * タスク追加ダイアログ
 */
public class DeleteStatusDialogFragment extends DialogFragment{
    public static final String TAG = DeleteStatusDialogFragment.class.getCanonicalName();

    /** デフォルトコンストラクタ */
    public DeleteStatusDialogFragment() {}

    /** 消去対象アイテム */
    private StatusDbEntity mDeleteItem;

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
        builder.setTitle("状態の消去");
        builder.setMessage("選択した状態を消去しますか？");
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
        if (null != getDialog()) {
            dismiss();
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // public メソッド
    // ////////////////////////////////////////////////////////////////////////////////////////////
    public void setDeleteItem(StatusDbEntity entity) {
        mDeleteItem = entity;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // interface
    // ////////////////////////////////////////////////////////////////////////////////////////////
    public interface deleteDialogCallback {
        void onOkClick(StatusDbEntity entity);
    }
}
