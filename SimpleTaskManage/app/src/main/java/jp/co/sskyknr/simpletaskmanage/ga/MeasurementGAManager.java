package jp.co.sskyknr.simpletaskmanage.ga;

/**
 * GA管理クラス
 */
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.GoogleAnalytics;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.util.Log;

import java.util.HashMap;

import jp.co.sskyknr.simpletaskmanage.R;


public class MeasurementGAManager extends Application {

    /** 画面遷移回数保持（広告表示指標） */
    public int counter = 0;
    /** 並べ替えチェック位置 */
    public int sortCheckId = R.id.sort_dialog_date_ascending;

    private enum TrackerName {
        APP_TRACKER,
        GLOBAL_TRACKER,
        ECOMMERCE_TRACKER,
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
    public MeasurementGAManager() {
        super();
    }
    private synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(
                    R.xml.global_tracker)
                    : analytics.newTracker(R.xml.ecommerce_tracker);
            t.enableAdvertisingIdCollection(true);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

    public static void sendGAScreen(Context context, String screenName) {
        Tracker t = ((MeasurementGAManager) context.getApplicationContext()).getTracker(MeasurementGAManager.TrackerName.APP_TRACKER);
        t.setScreenName(screenName);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    public static void sendGAEvent(Context context, String category, String action, String label) {
        if(label.length() == 0){
            label = "-";
        }
        Tracker t = ((MeasurementGAManager) context.getApplicationContext()).getTracker(TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .setValue(0)
                .build());
    }
}