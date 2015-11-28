package jp.co.sskyknr.simpletaskmanage;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import jp.co.sskyknr.simpletaskmanage.adapter.TaskRefineSettingAdapter;
import jp.co.sskyknr.simpletaskmanage.db.StatusDbDao;
import jp.co.sskyknr.simpletaskmanage.db.StatusDbEntity;
import jp.co.sskyknr.simpletaskmanage.util.Constants;
import jp.co.sskyknr.simpletaskmanage.util.GAUtil;

/**
 * 絞り込み設定画面
 */
public class TaskRefineSettingActivity extends BaseActivity implements View.OnClickListener{
    /** 自インスタンス */
    private final TaskRefineSettingActivity THIS = this;
    /** アダプター */
    private TaskRefineSettingAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        getSupportLoaderManager().initLoader(0, null, statusQueryCallback);

        // 絞り込み画面訪問GA送信
        GAUtil.sendGAEventOfScreen(THIS, GAUtil.SCREEN_REFINE);
    }

    private void init() {
        setContentView(R.layout.activity_task_refine);

        // ツールバー
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitle(R.string.label_menu_refine);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 戻るボタン
                onBackPressed();
            }
        });

        // 状態リスト
        ListView statusList = (ListView) findViewById(R.id.task_refine_status_list);
        mAdapter = new TaskRefineSettingAdapter(THIS, R.layout.status_refine_list_item);
        statusList.setAdapter(mAdapter);

        // 完了ボタン
        Button complete = (Button) findViewById(R.id.task_refine_complete_button);
        complete.setOnClickListener(THIS);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.task_refine_complete_button:
                // 完了ボタン
                String[] selectedStatus = mAdapter.getCheckedItem();
                Bundle bundle = new Bundle();
                bundle.putStringArray("sql", selectedStatus);
                Intent intent = new Intent();
                intent.putExtra("bundle", bundle);
                setResult(Constants.RESULT_OK, intent);

                // 絞り込みGA送信
                GAUtil.sendGAEventOfAction(THIS, GAUtil.CATEGORY_REFINE, GAUtil.ACTION_BUTTON, GAUtil.LABEL_REFINE);
                finish();
        }
    }

    /**
     * ステータス取得
     */
    LoaderManager.LoaderCallbacks statusQueryCallback = new LoaderManager.LoaderCallbacks() {
        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            return new CursorLoader(THIS, StatusDbDao.CONTENT_URI, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader loader, Object data) {
            if (data instanceof Cursor) {
                ArrayList<StatusDbEntity> statusList = new ArrayList<>();
                // Cursorだった場合、リストに追加
                Cursor cursor = (Cursor) data;
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    StatusDbEntity entity = new StatusDbEntity();
                    entity.setId(cursor.getInt(0));
                    entity.setSequenceId(cursor.getInt(1));
                    entity.setName(cursor.getString(2));
                    entity.setColor(cursor.getString(3));
                    statusList.add(entity);
                    cursor.moveToNext();
                }

                cursor.close();

                // リストに追加
                mAdapter.addAll(statusList);
            }
        }

        @Override
        public void onLoaderReset(Loader loader) {

        }
    };

}
