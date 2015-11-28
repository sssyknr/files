package jp.co.sskyknr.simpletaskmanage.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import jp.co.sskyknr.simpletaskmanage.R;
import jp.co.sskyknr.simpletaskmanage.ga.MeasurementGAManager;
import jp.co.sskyknr.simpletaskmanage.util.GAUtil;

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

        // 初期チェック
        final MeasurementGAManager application = (MeasurementGAManager) getActivity().getApplication();
        RadioButton button = (RadioButton) dialogView.findViewById(application.sortCheckId);
        button.setChecked(true);
        Button okButton = (Button) dialogView.findViewById(R.id.sort_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedButtonId = radioGroup.getCheckedRadioButtonId();
                application.sortCheckId = selectedButtonId;
                RadioButton selectedButton = (RadioButton) dialogView.findViewById(selectedButtonId);
                String select = (String) selectedButton.getText();
                if (getActivity() instanceof clickButton) {
                    ((clickButton) getActivity()).onOk(select);
                }
                dismiss();
            }
        });
        Button cancelButton = (Button) dialogView.findViewById(R.id.sort_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof clickButton) {
                    ((clickButton) getActivity()).onCancel();
                }
                dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);

        // 並べ替えダイアログGA訪問送信
        GAUtil.sendGAEventOfScreen(getActivity(), GAUtil.SCREEN_SORT);
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
