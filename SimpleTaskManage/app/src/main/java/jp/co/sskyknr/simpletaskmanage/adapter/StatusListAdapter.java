package jp.co.sskyknr.simpletaskmanage.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

import jp.co.sskyknr.simpletaskmanage.R;
import jp.co.sskyknr.simpletaskmanage.db.StatusDbEntity;
import jp.co.sskyknr.simpletaskmanage.dto.TaskListItemDto;
import jp.co.sskyknr.simpletaskmanage.util.colorUtil;

/**
 * ステータス設定用アダプター
 */
public class StatusListAdapter extends BaseAdapter{
    /** 表示リスト */
    private ArrayList<StatusDbEntity> mList = new ArrayList<>();
    /** アクティビティ */
    private Activity mActivity;
    /** リスナー */
    private onItemButtonListener mListener;


    public StatusListAdapter(Activity activity, onItemButtonListener listener) {
        mActivity = activity;
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View ret = convertView;
        if (ret == null) {
            ret = View.inflate(mActivity, R.layout.status_list_item, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.labelColor = (TextView) ret.findViewById(R.id.status_label_color);
            viewHolder.statusName = (TextView) ret.findViewById(R.id.status_name);
            viewHolder.colorImage = (ImageView) ret.findViewById(R.id.status_color_image);
            viewHolder.trashButton = (ImageView) ret.findViewById(R.id.status_list_item_trash);
            ret.setTag(viewHolder);
        }

        Object obj = ret.getTag();
        if (obj instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) obj;
            viewHolder.statusName.setText(mList.get(position).getName());
            int resId = colorUtil.setBgImage(mActivity, mList.get(position).getColor());
            viewHolder.labelColor.setBackgroundResource(resId);
            viewHolder.colorImage.setImageResource(resId);
            viewHolder.trashButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // ゴミ箱ボタン
                    StatusDbEntity values = mList.get(position);
                    if (mListener != null) {
                        mListener.onTrashClick(values);
                    }
                }
            });
        }
        return ret;
    }

    public void add(StatusDbEntity object) {
        mList.add(object);
        notifyDataSetChanged();
    }

    public void addAll(@NonNull Collection<? extends StatusDbEntity> collection) {
        mList.addAll(collection);
        notifyDataSetChanged();
    }

    public void remove(StatusDbEntity object) {
        mList.remove(object);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView labelColor;
        TextView statusName;
        ImageView colorImage;
        ImageView trashButton;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // interface
    // ////////////////////////////////////////////////////////////////////////////////////////////
    public interface onItemButtonListener {
        void onTrashClick(StatusDbEntity values);
    }
}
