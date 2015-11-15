package jp.co.sskyknr.simpletaskmanage;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import jp.co.sskyknr.simpletaskmanage.adapter.StatusListAdapter;
import jp.co.sskyknr.simpletaskmanage.db.StatusDbDao;
import jp.co.sskyknr.simpletaskmanage.db.StatusDbEntity;
import jp.co.sskyknr.simpletaskmanage.db.TaskDbHelper;
import jp.co.sskyknr.simpletaskmanage.fragment.CreateStatusDialog;
import jp.co.sskyknr.simpletaskmanage.fragment.DeleteStatusDialogFragment;
import jp.co.sskyknr.simpletaskmanage.fragment.DeleteTaskDialogFragment;
import jp.co.sskyknr.simpletaskmanage.util.Constants;

public class StatusSettingActivity extends AppCompatActivity implements View.OnClickListener, DeleteStatusDialogFragment.deleteDialogCallback{
    /** 自インスタンス */
    private final StatusSettingActivity THIS = this;
    /** ステータス管理リスト */
    public ArrayList<StatusDbEntity> mStatusList = new ArrayList<>();
    /** ステータスリストアダプター */
    public StatusListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        // ステータス取得
        getSupportLoaderManager().initLoader(0, null, statusQueryCallback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_status_setting, menu);
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
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(StatusDbEntity.class.getCanonicalName(), mStatusList);
        setResult(Constants.RESULT_OK, intent);
        finish();
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // private メソッド
    // ////////////////////////////////////////////////////////////////////////////////////////////

    private void init() {
        setContentView(R.layout.activity_status_setting);

        // ツールバー
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitle("ステータス設定");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // アクションボタン
        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.status_setting_add);
        addButton.setOnClickListener(THIS);

        // リストビュー
        ListView statusList = (ListView) findViewById(R.id.status_setting_list);
        mAdapter = new StatusListAdapter(THIS, new StatusListAdapter.onItemButtonListener() {
            @Override
            public void onTrashClick(StatusDbEntity values) {
                // ゴミ箱ボタンクリック時
                // 消去確認ダイアログ表示
                DeleteStatusDialogFragment dialogFragment = new DeleteStatusDialogFragment();
                dialogFragment.setDeleteItem(values);
                dialogFragment.show(getSupportFragmentManager(), DeleteTaskDialogFragment.TAG);
            }
        });
        statusList.setAdapter(mAdapter);
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////
    // コールバック
    // ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.status_setting_add:
                CreateStatusDialog dialog = new CreateStatusDialog();
                dialog.show(getSupportFragmentManager(), CreateStatusDialog.TAG);
                break;
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

                // アダプターに設定
                mAdapter.addAll(mStatusList);
            }
        }

        @Override
        public void onLoaderReset(Loader loader) {

        }
    };

    @Override
    public void onOkClick(StatusDbEntity entity) {
        // DBから消去
        TaskDbHelper helper = new TaskDbHelper(THIS);
        StatusDbDao dao = new StatusDbDao(helper.getWritableDatabase());
        int seqId = entity.getSequenceId();
        dao.delete(THIS, entity.getId());

        for (StatusDbEntity tmp : mStatusList) {
            if (tmp.getSequenceId() > seqId) {
                dao.updateSequence(THIS, tmp.getId(), tmp.getSequenceId() - 1);
            }
        }

        // リストから消去
        mStatusList.remove(entity);
        mAdapter.remove(entity);
    }
}
