package jp.co.sskyknr.simpletaskmanage.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.co.sskyknr.simpletaskmanage.R;
import jp.co.sskyknr.simpletaskmanage.db.TaskDbEntity;

/**
 * タスクリストのアダプター
 */
public class MainTaskListAdapter extends ArrayAdapter<TaskDbEntity> {

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // Private フィールド
    // ////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * チェックの状態を管理
     */
    private List<Integer> mCheckStatusList;


    /** OKボタンリスナー */
    private onItemButtonListener mListener;

    /**
     * コンストラクタ
     *
     * @param context
     * @param resource
     */
    public MainTaskListAdapter(Context context, int resource, onItemButtonListener listener) {
        super(context, resource);
        mCheckStatusList = new ArrayList<>();
        mListener = listener;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // Override メソッド
    // ////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View ret = convertView;
        if (ret == null) {
            ret = View.inflate(getContext(), R.layout.main_task_list_item, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.statusButton = (TextView) ret.findViewById(R.id.task_list_item_status);
            viewHolder.taskText = (TextView) ret.findViewById(R.id.task_list_item_text);
            viewHolder.trashButton = (ImageView) ret.findViewById(R.id.task_list_item_trash);
            viewHolder.trashButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // ゴミ箱ボタン
                    Object item = getItem(position);
                    if (item instanceof TaskDbEntity) {
                        TaskDbEntity values = (TaskDbEntity) item;
                        if (mListener != null) {
                            mListener.onTrashClick(values);
                        }
                    }
                }
            });
//            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    // チェックされた場合、変数に保持
//                    mCheckStatusList.set(position, isChecked);
//                }
//            });
            ret.setTag(viewHolder);
        }

        Object object = ret.getTag();
        if (object instanceof ViewHolder) {
            Object item = getItem(position);
            if (item instanceof TaskDbEntity) {
                TaskDbEntity value = (TaskDbEntity) item;
                ViewHolder viewHolder = (ViewHolder) object;
//                viewHolder.checkBox.setChecked(value.getStatusId());
                viewHolder.taskText.setText(value.getTask());
            }
        }

        return ret;
    }

    @Override
    public void add(TaskDbEntity object) {
        super.add(object);
//        mCheckStatusList.add(false);
    }

    @Override
    public void addAll(@NonNull Collection<? extends TaskDbEntity> collection) {
        super.addAll(collection);
    }

    @Override
    public void remove(TaskDbEntity object) {
        super.remove(object);
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // Inner クラス
    // ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * リストに表示するアイテム群
     */
    private class ViewHolder {
        TextView statusButton;
        TextView taskText;
        ImageView trashButton;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // コールバック
    // ////////////////////////////////////////////////////////////////////////////////////////////
    public interface onItemButtonListener {
        void onTrashClick(TaskDbEntity values);
    }
}
