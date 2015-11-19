package jp.co.sskyknr.simpletaskmanage.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

import jp.co.sskyknr.simpletaskmanage.R;
import jp.co.sskyknr.simpletaskmanage.db.StatusDbEntity;
import jp.co.sskyknr.simpletaskmanage.util.colorUtil;

/**
 * 絞り込み対象設定リストアダプター
 */
public class TaskRefineSettingAdapter extends ArrayAdapter<StatusDbEntity>{
    /** コンテキスト */
    private Context mContext;
    /** チェック状態保持リスト */
    private ArrayList<Boolean> mCheckList = new ArrayList<>();

    public TaskRefineSettingAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View ret = convertView;
        if (ret == null) {
            ret = View.inflate(mContext, R.layout.status_refine_list_item, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.statusColor = (TextView) ret.findViewById(R.id.status_refine_item_color);
            viewHolder.statusName = (TextView) ret.findViewById(R.id.status_refine_item_text);
            viewHolder.statusImage = (ImageView) ret.findViewById(R.id.status_color_image);
            viewHolder.checkBox = (CheckBox) ret.findViewById(R.id.status_refine_item_check);
            ret.setTag(viewHolder);
        }
        Object obj = ret.getTag();
        if (obj instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) obj;
            int colorId = colorUtil.setBgImage(mContext, getItem(position).getColor());
            viewHolder.statusColor.setBackgroundResource(colorId);
            viewHolder.statusName.setText(getItem(position).getName());
            viewHolder.statusImage.setImageResource(colorId);
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mCheckList.set(position, isChecked);
                }
            });
            viewHolder.checkBox.setChecked(mCheckList.get(position));
        }
        return ret;
    }

    @Override
    public void addAll(Collection<? extends StatusDbEntity> collection) {
        super.addAll(collection);
        for (StatusDbEntity entity : collection) {
            mCheckList.add(false);
        }
    }

    public String[] getCheckedItem() {
        int size = 0;
        int max = 0;
        ArrayList<Integer> selectedStatus = new ArrayList<>();
        for (Boolean isCheck : mCheckList) {
            if (isCheck) {
                selectedStatus.add(getItem(size).getId());
                max++;
            }
            size++;
        }

        String[] selected = new String[selectedStatus.size()];
        for (int i = 0; i < selectedStatus.size(); i++) {
            selected[i] = "" + selectedStatus.get(i);
        }
        return selected;
    }

    private class ViewHolder {
        TextView statusColor;
        TextView statusName;
        ImageView statusImage;
        CheckBox checkBox;
    }
}
