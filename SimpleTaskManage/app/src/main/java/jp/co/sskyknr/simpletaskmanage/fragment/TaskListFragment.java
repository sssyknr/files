package jp.co.sskyknr.simpletaskmanage.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import jp.co.sskyknr.simpletaskmanage.MainActivity;
import jp.co.sskyknr.simpletaskmanage.R;
import jp.co.sskyknr.simpletaskmanage.adapter.FragmentTaskListAdapter;
import jp.co.sskyknr.simpletaskmanage.db.StatusDbEntity;
import jp.co.sskyknr.simpletaskmanage.db.TaskDbDao;
import jp.co.sskyknr.simpletaskmanage.db.TaskDbEntity;
import jp.co.sskyknr.simpletaskmanage.db.TaskDbHelper;

/**
 * リスト表示用フラグメント
 */
public class TaskListFragment extends Fragment implements DeleteTaskDialogFragment.deleteDialogCallback {
    private final TaskListFragment THIS = this;

    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_SEQUENCE_ID = "sequenceId";
    public static final String KEY_COLOR = "color";
    public static final int ALL_STATUS = 0;
    public static final int NEW_STATUS = 1;
    public static final int FINISH_STATUS = 2;

    public static TaskListFragment newInstance(StatusDbEntity entity) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ID, entity.getId());
        bundle.putString(KEY_NAME, entity.getName());
        bundle.putInt(KEY_SEQUENCE_ID, entity.getSequenceId());
        bundle.putString(KEY_COLOR, entity.getColor());
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 全件取得用
     *
     * @return
     */
    public static TaskListFragment newInstance() {
        TaskListFragment fragment = new TaskListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ID, ALL_STATUS);
        bundle.putString(KEY_NAME, "全件表示");
        bundle.putInt(KEY_SEQUENCE_ID, 0);
        bundle.putString(KEY_COLOR, "255255255");
        fragment.setArguments(bundle);
        return fragment;
    }
    // ////////////////////////////////////////////////////////////////////////////////////////////
    // private フィールド
    // ////////////////////////////////////////////////////////////////////////////////////////////
    /** タスクリストのアダプター */
    public FragmentTaskListAdapter mAdapter;
    // ////////////////////////////////////////////////////////////////////////////////////////////
    // Override メソッド
    // ////////////////////////////////////////////////////////////////////////////////////////////

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_task_list, null);

        // ツールバー
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.fragment_toolbar);
        toolbar.setTitle(getArguments().getString(KEY_NAME));

        // タスク表示リスト
        ListView taskList = (ListView) view.findViewById(R.id.fragment_task_list);
        mAdapter = new FragmentTaskListAdapter(getActivity(), R.layout.fragment_task_list_item, new FragmentTaskListAdapter.onItemButtonListener() {
            @Override
            public void onTrashClick(TaskDbEntity values) {
                // ゴミ箱ボタンクリック時
                // 消去確認ダイアログ表示
                DeleteTaskDialogFragment dialogFragment = new DeleteTaskDialogFragment();
                dialogFragment.setDeleteItem(values);
                dialogFragment.show(getActivity().getSupportFragmentManager(), DeleteTaskDialogFragment.TAG);
            }
        });
        taskList.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(this.getClass().getCanonicalName(), "onResume()");

        // DBからタスクを取得
        getActivity().getSupportLoaderManager().initLoader(getArguments().getInt(KEY_ID), null, allQueryCallback);
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // コールバック
    // ////////////////////////////////////////////////////////////////////////////////////////////
    private LoaderManager.LoaderCallbacks allQueryCallback = new LoaderManager.LoaderCallbacks() {
        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            switch (id) {
                case ALL_STATUS:
                    // 全データ取得
                    Log.d("onCreateLoader", "ALL_STATUS");
                    return new CursorLoader(getActivity(), TaskDbDao.CONTENT_URI, null, null, null, null);
                case FINISH_STATUS:
                    // 終了ステータスのタスクを取得
                    String finishStatus = "statusId=2";
                    Log.d("onCreateLoader", "FINISH_STATUS");
                    return new CursorLoader(getActivity(), TaskDbDao.CONTENT_URI, null, finishStatus, null, null);
                case NEW_STATUS:
                    // 新規ステータスのタスクを取得
                    String newStatus = "statusId=1";
                    Log.d("onCreateLoader", "NEW_STATUS");
                    return new CursorLoader(getActivity(), TaskDbDao.CONTENT_URI, null, newStatus, null, null);
                default:
                    String inputStatus = "statusId=" + getArguments().getInt(KEY_ID);
                    Log.d("onCreateLoader", "default");
                    return new CursorLoader(getActivity(), TaskDbDao.CONTENT_URI, null, inputStatus, null, null);
            }
        }

        @Override
        public void onLoadFinished(Loader loader, Object data) {
            if (data instanceof Cursor) {
                // Cursorだった場合、リストに追加
                List<TaskDbEntity> entityList = new ArrayList<>();
                Cursor cursor = (Cursor) data;
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    TaskDbEntity entity = new TaskDbEntity();
                    entity.setRowId(cursor.getInt(0));
                    entity.setTask(cursor.getString(1));
                    entity.setStatusId(cursor.getInt(2));
                    entity.setCreatedAt(cursor.getInt(3));
                    entityList.add(entity);
                    cursor.moveToNext();
                }

                // アダプターに追加
                mAdapter.addAll(entityList);
            }
        }

        @Override
        public void onLoaderReset(Loader loader) {

        }
    };

    @Override
    public void onOkClick(TaskDbEntity entity) {
        // 消去ダイアログ「OK」
        // DBから消去
        TaskDbHelper helper = new TaskDbHelper(getActivity());
        TaskDbDao dao = new TaskDbDao(helper.getWritableDatabase());
        dao.delete(getActivity(), entity.getRowId());

        // リストから消去
        mAdapter.remove(entity);

        // ViewPagerに生成済みのフラグメント更新
        if (getActivity() instanceof MainActivity) {
            ((MainActivity)getActivity()).mAdapter.notifyDataSetChanged();
        }
    }
}
