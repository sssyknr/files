package jp.co.sskyknr.simpletaskmanage.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import jp.co.sskyknr.simpletaskmanage.R;
import jp.co.sskyknr.simpletaskmanage.dto.TaskListItemDto;

/**
 * タスク詳細ダイアログ
 */
public class TaskDetailDialog extends DialogFragment{
    public static final String TAG = "TaskDetailDialog";

    private static final String KEY_TASK = "task";
    private static final String KEY_COLOR = "color";
    private static final String KEY_STATUS = "status";

    public TaskDetailDialog(){

    }

    public static TaskDetailDialog newInstance(TaskListItemDto dto) {
        TaskDetailDialog dialog = new TaskDetailDialog();
        Bundle bundle = new Bundle();
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
        TextView status = (TextView) view.findViewById(R.id.task_detail_status);
        status.setText(getArguments().getString(KEY_STATUS));
        status.setBackgroundResource(getArguments().getInt(KEY_COLOR));
        TextView task = (TextView) view.findViewById(R.id.task_detail_text);
        task.setText(getArguments().getString(KEY_TASK));
        Button close = (Button) view.findViewById(R.id.task_detail_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
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
}
