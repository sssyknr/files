package jp.co.sskyknr.simpletaskmanage;

import android.content.ContentUris;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
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
import jp.co.sskyknr.simpletaskmanage.db.TaskDbDao;
import jp.co.sskyknr.simpletaskmanage.db.TaskDbEntity;
import jp.co.sskyknr.simpletaskmanage.db.TaskDbHelper;
import jp.co.sskyknr.simpletaskmanage.fragment.DeleteTaskDialogFragment;
import jp.co.sskyknr.simpletaskmanage.util.Common;
import jp.co.sskyknr.simpletaskmanage.util.PreferenceUtil;

public class MainActivity extends BaseActivity implements View.OnClickListener, DeleteTaskDialogFragment.deleteDialogCallback {

    /**
     * 自インスタンス
     */
    private final MainActivity THIS = this;

    /**
     * ViewPager
     */
    private ViewPager mViewPager;
    /**
     * ViewPagerアダプター
     */
    private MainTaskListAdapter mAdapter;
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
            dao.insert(this, "新規", 1, "255000000");
            dao.insert(this, "終了", 2, "000255000");
            PreferenceUtil.writeFlag(this, PreferenceUtil.KEY_FIRST, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // DBから取得
        getSupportLoaderManager().initLoader(0, null, allQueryCallback);
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
                TaskDbEntity entity = new TaskDbEntity((int) ContentUris.parseId(row), task, 1);
                mAdapter.add(entity);

                // テキストのクリア
                taskText.getEditableText().clear();

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

        // ツールバー
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitle("ここに画面タイトル出す");
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        // タスク表示リスト
        ListView taskList = (ListView) findViewById(R.id.main_task_list);
        mAdapter = new MainTaskListAdapter(THIS, R.layout.main_task_list_item, new MainTaskListAdapter.onItemButtonListener() {
            @Override
            public void onTrashClick(TaskDbEntity values) {
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
        TaskDbHelper helper = new TaskDbHelper(THIS);
        TaskDbDao dao = new TaskDbDao(helper.getWritableDatabase());
        dao.delete(THIS, entity.getRowId());

        // リストから消去
        mAdapter.remove(entity);
    }
}