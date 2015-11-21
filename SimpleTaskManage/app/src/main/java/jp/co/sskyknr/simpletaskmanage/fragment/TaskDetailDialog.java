package jp.co.sskyknr.simpletaskmanage.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import jp.co.sskyknr.simpletaskmanage.R;
import jp.co.sskyknr.simpletaskmanage.db.TaskDbDao;
import jp.co.sskyknr.simpletaskmanage.db.TaskDbHelper;
import jp.co.sskyknr.simpletaskmanage.dto.TaskListItemDto;
import jp.co.sskyknr.simpletaskmanage.util.Common;

/**
 * タスク詳細ダイアログ
 */
public class TaskDetailDialog extends DialogFragment{
    public static final String TAG = "TaskDetailDialog";

    private static final String KEY_TASK_ID = "taskId";
    private static final String KEY_TASK = "task";
    private static final String KEY_COLOR = "color";
    private static final String KEY_STATUS = "status";

    private boolean mIsEdit = false;

    public TaskDetailDialog(){

    }

    public static TaskDetailDialog newInstance(TaskListItemDto dto) {
        TaskDetailDialog dialog = new TaskDetailDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TASK_ID, dto.getTaskId());
        bundle.putString(KEY_TASK, dto.getTask());
        bundle.putString(KEY_STATUS, dto.getStasus());
        bundle.putInt(KEY_COLOR, dto.getBgId());
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.dialog_task_detail, null);
        final TextView status = (TextView) view.findViewById(R.id.task_detail_status);
        status.setText(getArguments().getString(KEY_STATUS));
        status.setBackgroundResource(getArguments().getInt(KEY_COLOR));
        final EditText task = (EditText) view.findViewById(R.id.task_detail_text);
        task.setText(getArguments().getString(KEY_TASK));
        Button close = (Button) view.findViewById(R.id.task_detail_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof TaskDetailDialogCallabck) {
                    ((TaskDetailDialogCallabck) getActivity()).onCloseClick(mIsEdit);
                }

                dismiss();
            }
        });
        final Button edit = (Button) view.findViewById(R.id.task_detail_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (task.isEnabled()) {
                    // editモード解除
                    task.setEnabled(false);
                    edit.setText("編集");

                    String str = task.getText().toString();
                    if (Common.isBlank(str)) {
                        return;
                    }
                    TaskDbHelper helper = new TaskDbHelper(getActivity());
                    TaskDbDao dao = new TaskDbDao(helper.getWritableDatabase());
                    dao.updateTask(getActivity(), getArguments().getInt(KEY_TASK_ID), str);
                    mIsEdit = true;
                } else {
                    // editモード
                    task.setEnabled(true);
                    edit.setText("完了");
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != getDialog()) {
            dismiss();
        }
    }

    public interface TaskDetailDialogCallabck {
        void onCloseClick(boolean isEdit);
    }
}
