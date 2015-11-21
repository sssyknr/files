package jp.co.sskyknr.simpletaskmanage;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

import jp.co.sskyknr.simpletaskmanage.adapter.StatusListAdapter;
import jp.co.sskyknr.simpletaskmanage.db.StatusDbDao;
import jp.co.sskyknr.simpletaskmanage.db.StatusDbEntity;
import jp.co.sskyknr.simpletaskmanage.db.TaskDbHelper;
import jp.co.sskyknr.simpletaskmanage.fragment.CreateStatusDialog;
import jp.co.sskyknr.simpletaskmanage.fragment.DeleteStatusDialogFragment;
import jp.co.sskyknr.simpletaskmanage.fragment.DeleteTaskDialogFragment;
import jp.co.sskyknr.simpletaskmanage.ga.MeasurementGAManager;
import jp.co.sskyknr.simpletaskmanage.util.Constants;
import jp.co.sskyknr.simpletaskmanage.util.GAUtil;
import jp.co.sskyknr.simpletaskmanage.view.SortableListView;

public class StatusSettingActivity extends BaseActivity implements View.OnClickListener, DeleteStatusDialogFragment.deleteDialogCallback{
    /** 自インスタンス */
    private final StatusSettingActivity THIS = this;
    /** ステータス管理リスト */
    public ArrayList<StatusDbEntity> mStatusList = new ArrayList<>();
    /** リストビュー */
    private SortableListView mStatusListView;
    /** ステータスリストアダプター */
    public StatusListAdapter mAdapter;
    /** ドラックのポジション */
    private int mDraggingPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        // ステータス取得
        getSupportLoaderManager().initLoader(0, null, statusQueryCallback);

        // スクリーン名送信
        GAUtil.sendGAEventOfScreen(THIS, GAUtil.SCREEN_STATUS);
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
        toolbar.setTitle(R.string.label_menu_status);
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
        TaskDbHelper helper = new TaskDbHelper(THIS);
        final StatusDbDao dao = new StatusDbDao(helper.getWritableDatabase());
        mStatusListView = (SortableListView) findViewById(R.id.status_setting_list);
        mStatusListView.setSortable(true);
        mStatusListView.setDragListener(new SortableListView.DragListener() {
            @Override
            public int onStartDrag(int position) {
                mDraggingPosition = position;
                mStatusListView.invalidateViews();

                return 0;
            }

            @Override
            public int onDuringDrag(int positionFrom, int positionTo) {
                if (positionFrom < 0 || positionTo < 0 || mDraggingPosition == positionTo) {
                    return positionFrom;
                }

                // シーケンスID入れ替え(アダプター)
                StatusDbEntity dragItem = (StatusDbEntity) mAdapter.getItem(mDraggingPosition);
                StatusDbEntity changeItem = (StatusDbEntity) mAdapter.getItem(positionTo);
                mAdapter.changeItem(mDraggingPosition, positionTo);
                // シーケンスID入れ替え(ステータスリスト)
                StatusDbEntity fromItem = mStatusList.get(mDraggingPosition);
                StatusDbEntity toItem = mStatusList.get(positionTo);
                mStatusList.set(mDraggingPosition, toItem);
                mStatusList.set(positionTo, fromItem);
                // シーケンスID入れ替え(DB)
                dao.updateSequence(THIS, dragItem.getId(), dragItem.getSequenceId());
                dao.updateSequence(THIS, changeItem.getId(), changeItem.getSequenceId());
                mDraggingPosition = positionTo;
                mStatusListView.invalidateViews();
                return 0;
            }

            @Override
            public boolean onStopDrag(int positionFrom, int positionTo) {
                mStatusListView.invalidateViews();
                // 並べ替えイベント送信
                GAUtil.sendGAEventOfAction(THIS, GAUtil.CATEGORY_STATUS, GAUtil.ACTION_BUTTON, GAUtil.LABEL_SORT);

                return false;
            }
        });
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
        mStatusListView.setAdapter(mAdapter);
    }

    private void sortStatusListBySequenceId() {
        ArrayList<StatusDbEntity> tmp = new ArrayList<>();
        for (int i = 1; i <= mStatusList.size() ; i++ ) {
            for (int j = 0; j < mStatusList.size(); j++) {
                if (mStatusList.get(j).getSequenceId() == i) {
                    tmp.add(mStatusList.get(j));
                    break;
                }
            }
        }
        mStatusList.clear();
        mStatusList.addAll(tmp);
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

                cursor.close();

                // アダプターに設定
                sortStatusListBySequenceId();
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

        // リストから消去
        mStatusList.remove(entity);
        mAdapter.remove(entity);

        // ステータスIDの更新
        for (int i = 0; i < mStatusList.size(); i++) {
            StatusDbEntity tmp = mStatusList.get(i);
            if (tmp.getSequenceId() > seqId) {
                int newSeqId = tmp.getSequenceId() - 1;
                tmp.setSequenceId(newSeqId);
                dao.updateSequence(THIS, tmp.getId(), tmp.getSequenceId());
                mStatusList.set(i, tmp);
            }
        }

        // 状態消去GA送信
        GAUtil.sendGAEventOfAction(THIS, GAUtil.CATEGORY_STATUS, GAUtil.ACTION_BUTTON, GAUtil.LABEL_DELETE_STATUS);

    }
}
