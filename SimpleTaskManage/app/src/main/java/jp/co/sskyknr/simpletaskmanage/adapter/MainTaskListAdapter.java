package jp.co.sskyknr.simpletaskmanage.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.co.sskyknr.simpletaskmanage.MainActivity;
import jp.co.sskyknr.simpletaskmanage.R;
import jp.co.sskyknr.simpletaskmanage.db.StatusDbEntity;
import jp.co.sskyknr.simpletaskmanage.db.TaskDbDao;
import jp.co.sskyknr.simpletaskmanage.db.TaskDbHelper;
import jp.co.sskyknr.simpletaskmanage.dto.TaskListItemDto;
import jp.co.sskyknr.simpletaskmanage.util.GAUtil;
import jp.co.sskyknr.simpletaskmanage.util.colorUtil;

/**
 * タスクリストのアダプター
 */
public class MainTaskListAdapter extends BaseAdapter {
    public static final String SORT_TYPE_STATUS = "status";
    public static final  String SORT_TYPE_DATE = "date";

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // Private フィールド
    // ////////////////////////////////////////////////////////////////////////////////////////////
    /** コンテキスト */
    private MainActivity mActivity;

    /** リストアイテム */
    private List<TaskListItemDto> mList;

    /** OKボタンリスナー */
    private onItemButtonListener mListener;

    /**
     * コンストラクタ
     *
     * @param activity
     */
    public MainTaskListAdapter(Activity activity, onItemButtonListener listener) {
        mActivity = (MainActivity) activity;
        mList = new ArrayList<>();
        mListener = listener;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // Override メソッド
    // ////////////////////////////////////////////////////////////////////////////////////////////

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
        return mList.get(position).getTaskId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View ret = convertView;
        if (ret == null) {
            ret = View.inflate(mActivity, R.layout.main_task_list_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.statusButton = (TextView) ret.findViewById(R.id.task_list_item_status);
            viewHolder.taskText = (TextView) ret.findViewById(R.id.task_list_item_text);
            viewHolder.trashButton = (ImageView) ret.findViewById(R.id.task_list_item_trash);

            ret.setTag(viewHolder);
        }

        Object object = ret.getTag();
        if (object instanceof ViewHolder) {
            final TaskListItemDto value = mList.get(position);
            ViewHolder viewHolder = (ViewHolder) object;
            viewHolder.statusButton.setBackgroundResource(value.getBgId());
            viewHolder.statusButton.setText(value.getStasus());
            viewHolder.statusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // ステータスクリック
                    TextView statusButton = (TextView) v;
                    TaskListItemDto values = mList.get(position);
                    boolean isLast = true;
                    for (StatusDbEntity status : mActivity.mStatusList) {
                        if (status.getSequenceId() == values.getSequenceId() + 1) {
                            // 次のシーケンスIDのものに変化
                            TaskListItemDto newValue = new TaskListItemDto();
                            newValue.setTaskId(values.getTaskId());
                            newValue.setTask(values.getTask());
                            newValue.setStasus(status.getName());
                            newValue.setSequenceId(status.getSequenceId());
                            newValue.setBgImage(mActivity, status.getColor());
                            statusButton.setText(newValue.getStasus());
                            statusButton.setBackgroundResource(newValue.getBgId());
                            // リストデータ更新
                            mList.set(position, newValue);

                            // データベースに変更を書き込み
                            TaskDbHelper helper = new TaskDbHelper(mActivity);
                            TaskDbDao dao = new TaskDbDao(helper.getWritableDatabase());
                            dao.updateStatus(mActivity, values.getTaskId(), status.getId());
                            isLast = false;
                            break;
                        }
                    }
                    if (isLast) {
                        for (StatusDbEntity status : mActivity.mStatusList) {
                            if (status.getSequenceId() == 1) {
                                // 次のシーケンスIDのものに変化
                                TaskListItemDto newValue = new TaskListItemDto();
                                newValue.setTaskId(values.getTaskId());
                                newValue.setTask(values.getTask());
                                newValue.setStasus(status.getName());
                                newValue.setSequenceId(status.getSequenceId());
                                newValue.setBgImage(mActivity, status.getColor());
                                statusButton.setText(newValue.getStasus());
                                statusButton.setBackgroundResource(newValue.getBgId());
                                // リストデータ更新
                                mList.set(position, newValue);

                                // データベースに変更を書き込み
                                TaskDbHelper helper = new TaskDbHelper(mActivity);
                                TaskDbDao dao = new TaskDbDao(helper.getWritableDatabase());
                                dao.updateStatus(mActivity, values.getTaskId(), status.getId());
                                break;
                            }
                        }
                    }
                    // タスク状態変更GA送信
                    GAUtil.sendGAEventOfAction(mActivity, GAUtil.CATEGORY_MAIN, GAUtil.ACTION_BUTTON, GAUtil.LABEL_CHANGE_STATUS);
                }
            });
            viewHolder.taskText.setText(value.getTask());
            viewHolder.trashButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // ゴミ箱ボタン
                    if (mListener != null) {
                        TaskListItemDto values = mList.get(position);
                        mListener.onTrashClick(values);
                    }
                }
            });
        }

        return ret;
    }

    public void add(TaskListItemDto object) {
        mList.add(object);
        notifyDataSetChanged();
    }

    public void addAll(@NonNull Collection<? extends TaskListItemDto> collection) {
        mList.addAll(collection);
        notifyDataSetChanged();
    }

    public void remove(TaskListItemDto object) {
        mList.remove(object);
        notifyDataSetChanged();
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    /**
     * ステータス順にソート
     */
    public void sort(Comparator<TaskListItemDto> comparator) {
        Collections.sort(mList, comparator);
        notifyDataSetChanged();
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
        void onTrashClick(TaskListItemDto values);
    }
}
