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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import jp.co.sskyknr.simpletaskmanage.adapter.MainTaskListAdapter;
import jp.co.sskyknr.simpletaskmanage.adapter.TaskListDateAscendingComparator;
import jp.co.sskyknr.simpletaskmanage.adapter.TaskListDateDescendingComparator;
import jp.co.sskyknr.simpletaskmanage.adapter.TaskListStatusAscendingComparator;
import jp.co.sskyknr.simpletaskmanage.adapter.TaskListStatusDescendingComparator;
import jp.co.sskyknr.simpletaskmanage.db.StatusDbDao;
import jp.co.sskyknr.simpletaskmanage.db.StatusDbEntity;
import jp.co.sskyknr.simpletaskmanage.db.TaskDbDao;
import jp.co.sskyknr.simpletaskmanage.db.TaskDbHelper;
import jp.co.sskyknr.simpletaskmanage.dto.TaskListItemDto;
import jp.co.sskyknr.simpletaskmanage.fragment.DeleteTaskDialogFragment;
import jp.co.sskyknr.simpletaskmanage.fragment.SortTaskSettingDialog;
import jp.co.sskyknr.simpletaskmanage.fragment.TaskDetailDialog;
import jp.co.sskyknr.simpletaskmanage.util.Common;
import jp.co.sskyknr.simpletaskmanage.util.GAUtil;
import jp.co.sskyknr.simpletaskmanage.util.PreferenceUtil;
import jp.co.sskyknr.simpletaskmanage.util.colorUtil;

public class MainActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, DeleteTaskDialogFragment.deleteDialogCallback, SortTaskSettingDialog.clickButton, TaskDetailDialog.TaskDetailDialogCallabck{
    /** 自インスタンス */
    private final MainActivity THIS = this;
    /** ステータスの取得 */
    private static final int QUERY_STATUS = 0;
    /** タスクの取得（全件） */
    private static final int QUERY_ALL_TASK = 1;
    /** タスクの取得（一部） */
    /** リクエストコード:ステータス設定 */
    private static final int QUERY_SELECT_TASK = 2;
    private static final int REQUEST_STATUS_SETTING = 0;
    /** リクエストコード:絞り込み設定 */
    private static final int REQUEST_REFINE_SETTING = 1;
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

            // 初回起動GA送信
            GAUtil.sendGAEventOfScreen(THIS, GAUtil.SCREEN_MAIN_FIRST);
        }

        // 起動GA送信
        GAUtil.sendGAEventOfScreen(THIS, GAUtil.SCREEN_MAIN);
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
            case REQUEST_REFINE_SETTING:
                // 絞り込み
                if (data == null) {
                    return;
                }
                Bundle bundle = data.getBundleExtra("bundle");
                getSupportLoaderManager().restartLoader(QUERY_SELECT_TASK, bundle, allQueryCallback);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
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
                long time = System.currentTimeMillis();
                int firstStatus = 0;
                for (StatusDbEntity entity : mStatusList) {
                    if (entity.getSequenceId() == 1) {
                        firstStatus = entity.getId();
                    }
                }
                Uri row = taskDao.insert(THIS, task, firstStatus, time);

                // リストに追加
                TaskListItemDto entity = new TaskListItemDto();
                entity.setTaskId((int) ContentUris.parseId(row));
                entity.setTask(task);
                for (StatusDbEntity status : mStatusList) {
                    if (status.getId() == firstStatus) {
                        entity.setStasus(status.getName());
                        entity.setSequenceId(status.getSequenceId());
                        entity.setBgImage(THIS, status.getColor());
                        entity.createAt = time;
                        break;
                    }
                }
                mAdapter.add(entity);

                // テキストのクリア
                taskText.getEditableText().clear();

                // GAタスク追加アクション送信
                GAUtil.sendGAEventOfAction(THIS, GAUtil.CATEGORY_MAIN, GAUtil.ACTION_BUTTON, GAUtil.LABEL_ADD_TASK);
                break;
            case R.id.menu_status_setting:
                // ステータス設定
                intent = new Intent(THIS, StatusSettingActivity.class);
                startActivityForResult(intent, REQUEST_STATUS_SETTING);
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
                // GAタスク追加アクション送信
                GAUtil.sendGAEventOfAction(THIS, GAUtil.CATEGORY_MAIN, GAUtil.ACTION_BUTTON, GAUtil.LABEL_STATUS);
                break;
            case R.id.menu_sort:
                // 並べ替え
                SortTaskSettingDialog sortSettingDialog = new SortTaskSettingDialog();
                sortSettingDialog.show(getSupportFragmentManager(), SortTaskSettingDialog.TAG);
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
                // GAタスク追加アクション送信
                GAUtil.sendGAEventOfAction(THIS, GAUtil.CATEGORY_MAIN, GAUtil.ACTION_BUTTON, GAUtil.LABEL_SORT);
                break;
            case R.id.menu_refine:
                // 絞り込み
                intent = new Intent(THIS, TaskRefineSettingActivity.class);
                startActivityForResult(intent, REQUEST_REFINE_SETTING);
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
                // GAタスク追加アクション送信
                GAUtil.sendGAEventOfAction(THIS, GAUtil.CATEGORY_MAIN, GAUtil.ACTION_BUTTON, GAUtil.LABEL_REFINE);
                break;
            case R.id.menu_about:
                // AboutMe
                intent = new Intent(THIS, AboutMeActivity.class);
                startActivity(intent);
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
                // GAタスク追加アクション送信
                GAUtil.sendGAEventOfAction(THIS, GAUtil.CATEGORY_MAIN, GAUtil.ACTION_BUTTON, GAUtil.LABEL_ABOUT_ME);
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
        // IME非表示
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.activity_main);

        // ナビゲーションドロワー
        mDrawerLayout = ((DrawerLayout) findViewById(R.id.main_drawer_layout));

        // ツールバー
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                    // ドロワークローズイベント送信
                    GAUtil.sendGAEventOfAction(THIS, GAUtil.CATEGORY_MAIN, GAUtil.ACTION_BUTTON, GAUtil.LABEL_DRAWER_CLOSE);
                } else {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    // ドロワーオープンイベント送信
                    GAUtil.sendGAEventOfAction(THIS, GAUtil.CATEGORY_MAIN, GAUtil.ACTION_BUTTON, GAUtil.LABEL_DRAWER_OPEN);
                }
                return true;
            }
        });

        ListView taskList = (ListView) findViewById(R.id.main_task_list);
        taskList.setOnItemClickListener(THIS);
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

        // 広告
        AdView adView = (AdView)this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
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
            switch (id) {
                case QUERY_ALL_TASK:
                    // 全件取得
                    return new CursorLoader(THIS, TaskDbDao.CONTENT_URI, null, null, null, null);
                case QUERY_SELECT_TASK:
                    // 一部取得
                    String[] sql = args.getStringArray("sql");
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < sql.length; i++) {
                        builder.append(TaskDbDao.COLUMN_STATUS + "=?");
                        if (i != sql.length -1) {
                            builder.append(" OR ");
                        }
                    }
                    return new CursorLoader(THIS, TaskDbDao.CONTENT_URI, null, builder.toString(), sql, null);
            }
            return null;
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
                            entity.createAt = cursor.getLong(3);
                            break;
                        }
                    }
                    entityList.add(entity);
                    cursor.moveToNext();
                }
                cursor.close();

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

        // ゴミ箱ボタンクリックイベント送信
        GAUtil.sendGAEventOfAction(THIS, GAUtil.CATEGORY_MAIN, GAUtil.ACTION_BUTTON, GAUtil.LABEL_DELETE_TASK);
    }

    /**
     * ステータス全取得
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

                cursor.close();

                // DBからタスクを取得
                getSupportLoaderManager().initLoader(QUERY_ALL_TASK, null, allQueryCallback);
            }
        }

        @Override
        public void onLoaderReset(Loader loader) {

        }
    };

    @Override
    public void onOk(String selected) {
        final String statusAscending = getString(R.string.label_sort_status_ascending);
        final String statusDescending = getString(R.string.label_sort_status_descending);
        final String dateAscending = getString(R.string.label_sort_date_ascending);
        final String dateDescending = getString(R.string.label_sort_date_descending);
        if (dateAscending.equals(selected)) {
            mAdapter.sort(new TaskListDateAscendingComparator());
            // ソートGA送信
            GAUtil.sendGAEventOfAction(THIS, GAUtil.CATEGORY_SORT, GAUtil.ACTION_BUTTON, GAUtil.LABEL_DATE_UP);
        } else if (dateDescending.equals(selected)) {
            mAdapter.sort(new TaskListDateDescendingComparator());
            // ソートGA送信
            GAUtil.sendGAEventOfAction(THIS, GAUtil.CATEGORY_SORT, GAUtil.ACTION_BUTTON, GAUtil.LABEL_DATE_DOWN);
        } else if (statusAscending.equals(selected)) {
            mAdapter.sort(new TaskListStatusAscendingComparator());
            // ソートGA送信
            GAUtil.sendGAEventOfAction(THIS, GAUtil.CATEGORY_SORT, GAUtil.ACTION_BUTTON, GAUtil.LABEL_STATUS_UP);
        } else if (statusDescending.equals(selected)) {
            mAdapter.sort(new TaskListStatusDescendingComparator());
            // ソートGA送信
            GAUtil.sendGAEventOfAction(THIS, GAUtil.CATEGORY_SORT, GAUtil.ACTION_BUTTON, GAUtil.LABEL_STATUS_DOWN);
        }
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TaskDetailDialog dialog = TaskDetailDialog.newInstance((TaskListItemDto) mAdapter.getItem(position));
        dialog.show(getSupportFragmentManager(), TaskDetailDialog.TAG);

        // 詳細GA送信
        GAUtil.sendGAEventOfAction(THIS, GAUtil.CATEGORY_MAIN, GAUtil.ACTION_BUTTON, GAUtil.LABEL_DETAIL_TASK);
    }

    @Override
    public void onCloseClick(boolean isEdit) {
        if (isEdit) {
            // 編集が行われていた場合
            getSupportLoaderManager().restartLoader(QUERY_ALL_TASK, null, allQueryCallback);
            // タスク編集GA送信
            GAUtil.sendGAEventOfAction(THIS, GAUtil.CATEGORY_MAIN, GAUtil.ACTION_BUTTON, GAUtil.LABEL_EDIT_TASK);
        }
    }
}