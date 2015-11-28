package jp.co.sskyknr.simpletaskmanage.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import jp.co.sskyknr.simpletaskmanage.R;
import jp.co.sskyknr.simpletaskmanage.StatusSettingActivity;
import jp.co.sskyknr.simpletaskmanage.db.StatusDbDao;
import jp.co.sskyknr.simpletaskmanage.db.StatusDbEntity;
import jp.co.sskyknr.simpletaskmanage.db.TaskDbHelper;
import jp.co.sskyknr.simpletaskmanage.util.Common;
import jp.co.sskyknr.simpletaskmanage.util.GAUtil;
import jp.co.sskyknr.simpletaskmanage.util.colorUtil;

/**
 * ステータスを作成するダイアログ
 */
public class CreateStatusDialog extends DialogFragment implements View.OnClickListener{
    /** ダイアログ */
    public static final String TAG = "CreateStatusDialog";
    /** 選択されている色 */
    private String mSelectedColor;
    /** 選択されている色反映用 */
    TextView mSelectedLeft;
    /** 選択されている色反映用 */
    TextView mSelectedRight;
    /** ステータステキスト */
    EditText mStatusText;
    public CreateStatusDialog() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_create_status, null);
        mSelectedLeft = (TextView) view.findViewById(R.id.selected_color_left);
        mSelectedRight = (TextView) view.findViewById(R.id.selected_color_right);
        mStatusText = (EditText) view.findViewById(R.id.edit_status_name);
        Button createButton = (Button) view.findViewById(R.id.create_status_button);
        createButton.setText("追加");
        createButton.setOnClickListener(this);

        view.findViewById(R.id.image_red).setOnClickListener(this);
        view.findViewById(R.id.image_black).setOnClickListener(this);
        view.findViewById(R.id.image_amber).setOnClickListener(this);
        view.findViewById(R.id.image_blue).setOnClickListener(this);
        view.findViewById(R.id.image_blue_grey).setOnClickListener(this);
        view.findViewById(R.id.image_brown).setOnClickListener(this);
        view.findViewById(R.id.image_cyan).setOnClickListener(this);
        view.findViewById(R.id.image_deep_orange).setOnClickListener(this);
        view.findViewById(R.id.image_deep_purple).setOnClickListener(this);
        view.findViewById(R.id.image_green).setOnClickListener(this);
        view.findViewById(R.id.image_grey).setOnClickListener(this);
        view.findViewById(R.id.image_indigo).setOnClickListener(this);
        view.findViewById(R.id.image_light_blue).setOnClickListener(this);
        view.findViewById(R.id.image_light_green).setOnClickListener(this);
        view.findViewById(R.id.image_lime).setOnClickListener(this);
        view.findViewById(R.id.image_orange).setOnClickListener(this);
        view.findViewById(R.id.image_pink).setOnClickListener(this);
        view.findViewById(R.id.image_purple).setOnClickListener(this);
        view.findViewById(R.id.image_teal).setOnClickListener(this);
        view.findViewById(R.id.image_white).setOnClickListener(this);
        view.findViewById(R.id.image_yellow).setOnClickListener(this);

        // 初期職は赤
        mSelectedColor = colorUtil.RED;
        mSelectedLeft.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
        mSelectedRight.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_amber:
                mSelectedColor = colorUtil.AMBER;
                mSelectedLeft.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                mSelectedRight.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                break;
            case R.id.image_black:
                mSelectedColor = colorUtil.BLACK;
                mSelectedLeft.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                mSelectedRight.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                break;
            case R.id.image_blue:
                mSelectedColor = colorUtil.BLUE;
                mSelectedLeft.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                mSelectedRight.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                break;
            case R.id.image_blue_grey:
                mSelectedColor = colorUtil.BLUE_GREY;
                mSelectedLeft.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                mSelectedRight.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                break;
            case R.id.image_brown:
                mSelectedColor = colorUtil.BROWN;
                mSelectedLeft.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                mSelectedRight.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                break;
            case R.id.image_cyan:
                mSelectedColor = colorUtil.CYAN;
                mSelectedLeft.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                mSelectedRight.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                break;
            case R.id.image_deep_orange:
                mSelectedColor = colorUtil.DEEP_ORANGE;
                mSelectedLeft.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                mSelectedRight.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                break;
            case R.id.image_deep_purple:
                mSelectedColor = colorUtil.DEEP_PURPLE;
                mSelectedLeft.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                mSelectedRight.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                break;
            case R.id.image_indigo:
                mSelectedColor = colorUtil.INDIGO;
                mSelectedLeft.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                mSelectedRight.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                break;
            case R.id.image_light_blue:
                mSelectedColor = colorUtil.LIGHT_BLUE;
                mSelectedLeft.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                mSelectedRight.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                break;
            case R.id.image_light_green:
                mSelectedColor = colorUtil.LIGHT_GREEN;
                mSelectedLeft.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                mSelectedRight.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                break;
            case R.id.image_lime:
                mSelectedColor = colorUtil.LIME;
                mSelectedLeft.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                mSelectedRight.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                break;
            case R.id.image_orange:
                mSelectedColor = colorUtil.ORANGE;
                mSelectedLeft.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                mSelectedRight.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                break;
            case R.id.image_pink:
                mSelectedColor = colorUtil.PINK;
                mSelectedLeft.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                mSelectedRight.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                break;
            case R.id.image_purple:
                mSelectedColor = colorUtil.PURPLE;
                mSelectedLeft.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                mSelectedRight.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                break;
            case R.id.image_red:
                mSelectedColor = colorUtil.RED;
                mSelectedLeft.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                mSelectedRight.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                break;
            case R.id.image_teal:
                mSelectedColor = colorUtil.TEAL;
                mSelectedLeft.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                mSelectedRight.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                break;
            case R.id.image_white:
                mSelectedColor = colorUtil.WHITE;
                mSelectedLeft.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                mSelectedRight.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                break;
            case R.id.image_yellow:
                mSelectedColor = colorUtil.YELLOW;
                mSelectedLeft.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                mSelectedRight.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                break;
            case R.id.image_grey:
                mSelectedColor = colorUtil.GREY;
                mSelectedLeft.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                mSelectedRight.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                break;
            case R.id.image_green:
                mSelectedColor = colorUtil.GREEN;
                mSelectedLeft.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                mSelectedRight.setBackgroundResource(colorUtil.setBgImage(getActivity(), mSelectedColor));
                break;
            case R.id.create_status_button:
                String input = mStatusText.getText().toString();
                if (Common.isBlank(input)) {
                    return;
                }
                StatusSettingActivity activity = (StatusSettingActivity) getActivity();
                int last = 0;
                for (StatusDbEntity entity : activity.mStatusList) {
                    if (last < entity.getSequenceId()) {
                        last = entity.getSequenceId();
                    }
                }
                // DBに新ステータスを登録
                TaskDbHelper helper = new TaskDbHelper(getActivity());
                StatusDbDao dao = new StatusDbDao(helper.getWritableDatabase());
                Uri row = dao.insert(getActivity(), input, last + 1, mSelectedColor);

                // リストに追加
                int id = (int) ContentUris.parseId(row);
                StatusDbEntity entity = new StatusDbEntity(id, input, last + 1, mSelectedColor);
                activity.mStatusList.add(entity);
                activity.mAdapter.add(entity);

                // 状態追加GA送信
                GAUtil.sendGAEventOfAction(getActivity(), GAUtil.CATEGORY_STATUS, GAUtil.ACTION_ADD_STATUS, entity.getColor());

                dismiss();
                break;
        }
    }
}
