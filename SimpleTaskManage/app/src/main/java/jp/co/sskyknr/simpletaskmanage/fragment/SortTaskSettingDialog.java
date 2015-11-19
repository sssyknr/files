package jp.co.sskyknr.simpletaskmanage.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import jp.co.sskyknr.simpletaskmanage.R;

/**
 * タスク並べ替え設定ダイアログ
 */
public class SortTaskSettingDialog extends DialogFragment{
    public static final String TAG = "SortTaskSettingDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_sort_task_setting, null);
        final RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.sort_dialog_radio_group);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedButtonId = radioGroup.getCheckedRadioButtonId();
                RadioButton selectedButton = (RadioButton) dialogView.findViewById(selectedButtonId);
                String select = (String) selectedButton.getText();
                if (getActivity() instanceof clickButton) {
                    ((clickButton) getActivity()).onOk(select);
                }
            }
        });
        builder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getActivity() instanceof clickButton) {
                    ((clickButton) getActivity()).onCancel();
                }
            }
        });

        return builder.create();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (null != getActivity()) {
            dismiss();
        }
    }

    public interface clickButton {
        void onOk(String selected);
        void onCancel();
    }
}
