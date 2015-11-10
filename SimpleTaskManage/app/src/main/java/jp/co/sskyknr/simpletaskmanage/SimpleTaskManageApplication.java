package jp.co.sskyknr.simpletaskmanage;

import android.app.Application;
import android.content.SharedPreferences;

import jp.co.sskyknr.simpletaskmanage.db.StatusDbDao;
import jp.co.sskyknr.simpletaskmanage.db.TaskDbHelper;
import jp.co.sskyknr.simpletaskmanage.util.PreferenceUtil;

/**
 * アプリケーションクラス
 */
public class SimpleTaskManageApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
