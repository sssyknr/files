package jp.co.sskyknr.simpletaskmanage;

import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import jp.co.sskyknr.simpletaskmanage.adapter.MainTaskListAdapter;
import jp.co.sskyknr.simpletaskmanage.db.StatusDbDao;
import jp.co.sskyknr.simpletaskmanage.db.StatusDbEntity;
import jp.co.sskyknr.simpletaskmanage.db.TaskDbDao;
import jp.co.sskyknr.simpletaskmanage.db.TaskDbEntity;
import jp.co.sskyknr.simpletaskmanage.db.TaskDbHelper;
import jp.co.sskyknr.simpletaskmanage.dto.TaskListItemDto;
import jp.co.sskyknr.simpletaskmanage.fragment.DeleteTaskDialogFragment;
import jp.co.sskyknr.simpletaskmanage.util.Common;
import jp.co.sskyknr.simpletaskmanage.util.PreferenceUtil;
import jp.co.sskyknr.simpletaskmanage.util.colorUtil;

public class MainActivity extends BaseActivity implements View.OnClickListener, DeleteTaskDialogFragment.deleteDialogCallback {
    /** 自インスタンス */
    private final MainActivity THIS = this;
    /** ステータスの取得 */
    private static final int QUERY_STATUS = 0;
    /** タスクの取得 */
    private static final int QUERY_TASK = 1;
    /** リクエストコード:ステータス設定 */
    private static final int REQUEST_STATUS_SETTING = 0;
    /** タスクリストアダプター */
    private MainTaskListAdapter mAdapter;
    /** ステータス管理リスト */
    public ArrayList<StatusDbEntity> mStatusList = new ArrayList<>();
    /** ナビゲーションドロワー */
    private DrawerLayout mDrawerLayout;
    // ////////////////////////////////////////////////////////////////////////////////////////////
    // Override メソッド
    // ////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        SharedPreferences pref = PreferenceUtil.getPreference(this);
        Boolean isFirst = pref.getBoolean(PreferenceUtil.KEY_FIRST, true);
        if (isFirst) {
            // 初回起動時、DBに初期ステータス書き込み
            TaskDbHelper helper = new TaskDbHelper(this);
            StatusDbDao dao = new StatusDbDao(helper.getWritableDatabase());
            dao.insert(this, "新規", 1, colorUtil.WHITE);
            dao.insert(this, "進行中", 2, colorUtil.YELLOW);
            dao.insert(this, "終了", 3, colorUtil.GREY);
            PreferenceUtil.writeFlag(this, PreferenceUtil.KEY_FIRST, false);
        }

        // ステータス取得
        getSupportLoaderManager().initLoader(QUERY_STATUS, null, statusQueryCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_STATUS_SETTING:
                // ステータス更新
                mStatusList = null;
                mStatusList = (ArrayList<StatusDbEntity>) data.getSerializableExtra(StatusDbEntity.class.getCanonicalName());
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    // ドロワーが開いていれば閉じる
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_task_add_button:
                // タスク追加ボタン
                EditText taskText = (EditText) findViewById(R.id.main_task_add_text);
                String task = taskText.getText().toString();
                if (Common.isBlank(task)) {
                    // テキストに入力されていない場合何もしない
                    return;
                }

                // DBに追加
                TaskDbHelper helper = new TaskDbHelper(THIS);
                TaskDbDao taskDao = new TaskDbDao(helper.getWritableDatabase());
                Uri row = taskDao.insert(THIS, task, 1);

                // リストに追加
                TaskListItemDto entity = new TaskListItemDto();
                entity.setTaskId((int) ContentUris.parseId(row));
                entity.setTask(task);
                for (StatusDbEntity status : mStatusList) {
                    if (status.getId() == 1) {
                        entity.setStasus(status.getName());
                        entity.setSequenceId(status.getSequenceId());
                        entity.setBgImage(THIS, status.getColor());
                        break;
                    }
                }
                mAdapter.add(entity);

                // テキストのクリア
                taskText.getEditableText().clear();

                break;
            case R.id.menu_status_setting:
                Intent intent = new Intent(THIS, StatusSettingActivity.class);
                startActivityForResult(intent, REQUEST_STATUS_SETTING);
                break;
        }
    }
    // ////////////////////////////////////////////////////////////////////////////////////////////
    // Private メソッド
    // ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 初期化
     */
    private void init() {
        setContentView(R.layout.activity_main);

        // ナビゲーションドロワー
        mDrawerLayout = ((DrawerLayout) findViewById(R.id.main_drawer_layout));

        // ツールバー
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitle("ここに画面タイトル出す");
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                }
                return true;
            }
        });

        ListView taskList = (ListView) findViewById(R.id.main_task_list);
        mAdapter = new MainTaskListAdapter(THIS, new MainTaskListAdapter.onItemButtonListener() {
            @Override
            public void onTrashClick(TaskListItemDto values) {
                // ゴミ箱ボタンクリック時
                // 消去確認ダイアログ表示
                DeleteTaskDialogFragment dialogFragment = new DeleteTaskDialogFragment();
                dialogFragment.setDeleteItem(values);
                dialogFragment.show(getSupportFragmentManager(), DeleteTaskDialogFragment.TAG);
            }
        });
        taskList.setAdapter(mAdapter);

        // タスク追加ボタン
        Button button = (Button) findViewById(R.id.main_task_add_button);
        button.setOnClickListener(THIS);
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // コールバック
    // ////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * タスク全件取得コールバック
     */
    LoaderManager.LoaderCallbacks allQueryCallback = new LoaderManager.LoaderCallbacks() {
        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            // 全件取得
            return new CursorLoader(THIS, TaskDbDao.CONTENT_URI, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader loader, Object data) {
            if (data instanceof Cursor) {
                // Cursorだった場合、リストに追加
                List<TaskListItemDto> entityList = new ArrayList<>();
                Cursor cursor = (Cursor) data;
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    TaskListItemDto entity = new TaskListItemDto();
                    entity.setTaskId(cursor.getInt(0));
                    entity.setTask(cursor.getString(1));
                    for (StatusDbEntity status : mStatusList) {
                        int statusId = cursor.getInt(2);
                        if (status.getId() == statusId) {
                            entity.setStasus(status.getName());
                            entity.setSequenceId(status.getSequenceId());
                            entity.setBgImage(THIS, status.getColor());
                            break;
                        }
                    }
                    entityList.add(entity);
                    cursor.moveToNext();
                }

                // アダプターに追加
                mAdapter.clear();
                mAdapter.addAll(entityList);
            }
        }

        @Override
        public void onLoaderReset(Loader loader) {

        }
    };

    @Override
    public void onOkClick(TaskListItemDto entity) {
        // 消去ダイアログ「OK」
        // DBから消去
        TaskDbHelper helper = new TaskDbHelper(THIS);
        TaskDbDao dao = new TaskDbDao(helper.getWritableDatabase());
        dao.delete(THIS, entity.getTaskId());

        // リストから消去
        mAdapter.remove(entity);
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
                mStatusList.clear();
                // Cursorだった場合、リストに追加
                Cursor cursor = (Cursor) data;
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    StatusDbEntity entity = new StatusDbEntity();
                    entity.setId(cursor.getInt(0));
                    entity.setSequenceId(cursor.getInt(1));
                    entity.setName(cursor.getString(2));
                    entity.setColor(cursor.getString(3));
                    mStatusList.add(entity);
                    cursor.moveToNext();
                }

                // DBからタスクを取得
                getSupportLoaderManager().initLoader(QUERY_TASK, null, allQueryCallback);
            }
        }

        @Override
        public void onLoaderReset(Loader loader) {

        }
    };
}