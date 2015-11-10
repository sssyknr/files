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

import jp.co.sskyknr.simpletaskmanage.adapter.TaskListPagerAdapter;
import jp.co.sskyknr.simpletaskmanage.db.StatusDbDao;
import jp.co.sskyknr.simpletaskmanage.db.StatusDbEntity;
import jp.co.sskyknr.simpletaskmanage.db.TaskDbDao;
import jp.co.sskyknr.simpletaskmanage.db.TaskDbEntity;
import jp.co.sskyknr.simpletaskmanage.db.TaskDbHelper;
import jp.co.sskyknr.simpletaskmanage.fragment.TaskListFragment;
import jp.co.sskyknr.simpletaskmanage.util.Common;
import jp.co.sskyknr.simpletaskmanage.util.PreferenceUtil;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    /** 自インスタンス */
    private final MainActivity THIS = this;

    /** ViewPager */
    private ViewPager mViewPager;
    /** ViewPagerアダプター */
    public TaskListPagerAdapter mAdapter;
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
        getSupportLoaderManager().initLoader(0, null, statusQueryCallback);
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
                TaskDbEntity entity = new TaskDbEntity();
                entity.setRowId((int) ContentUris.parseId(row));
                entity.setTask(task);
                entity.setStatusId(1);
                int time = (int) System.currentTimeMillis();
                entity.setCreatedAt(time);
                TaskListFragment fragment = (TaskListFragment) mAdapter.findFragmentByPosition(mViewPager, mViewPager.getCurrentItem());
                fragment.mAdapter.add(entity);

                // テキストのクリア
                taskText.getEditableText().clear();

                // fragment更新
                mAdapter.notifyDataSetChanged();
                break;
        }
    }
    // ////////////////////////////////////////////////////////////////////////////////////////////
    // Private メソッド
    // ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 初期化
     */
    private void init(){
        setContentView(R.layout.activity_main);

        // ツールバー
        Toolbar toolbar = (Toolbar) findViewById(R.id.fragment_toolbar);
        toolbar.setTitle("ここに画面タイトル出す");

        mViewPager = (ViewPager) findViewById(R.id.main_task_list_view_pager);
        mAdapter = new TaskListPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        // タスク追加ボタン
        Button button = (Button) findViewById(R.id.main_task_add_button);
        button.setOnClickListener(THIS);
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // コールバック
    // ////////////////////////////////////////////////////////////////////////////////////////////
    LoaderManager.LoaderCallbacks statusQueryCallback = new LoaderManager.LoaderCallbacks() {
        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            return new CursorLoader(THIS, StatusDbDao.CONTENT_URI, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader loader, Object data) {
            TaskListFragment all = TaskListFragment.newInstance();
            mAdapter.add(all);
            if (data instanceof Cursor) {
                Cursor cursor = (Cursor) data;
                while (cursor.moveToNext()) {
                    StatusDbEntity entity = new StatusDbEntity();
                    entity.setId(cursor.getInt(0));
                    entity.setSequenceId(cursor.getInt(1));
                    entity.setName(cursor.getString(2));
                    entity.setColor(cursor.getString(3));
                    TaskListFragment fragment = TaskListFragment.newInstance(entity);
                    mAdapter.add(fragment);
                }
            }
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader loader) {

        }
    };
}