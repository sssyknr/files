package jp.co.sskyknr.simpletaskmanage.util;

import android.content.Context;

import jp.co.sskyknr.simpletaskmanage.ga.MeasurementGAManager;

/**
 * GA送信Util
 */
public class GAUtil {

    // ----------------------------------------------------
    // スクリーン名
    // ----------------------------------------------------
    /** メインアクティビティ初期訪問回数 */
    public static String SCREEN_MAIN_FIRST = "MainActivityF";
    /** メインアクティビティ訪問回数 */
    public static String SCREEN_MAIN = "MainActivity";
    /** 状態設定画面 */
    public static String SCREEN_STATUS = "statusSettingActivity";
    /** 並べ替えダイアログ */
    public static String SCREEN_SORT = "sortDialog";
    /** 絞り込み画面 */
    public static String SCREEN_REFINE = "refineActivity";
    // ----------------------------------------------------
    // カテゴリー
    // ----------------------------------------------------
    /** メインアクティビティ */
    public static String CATEGORY_MAIN = "MainActivity";
    /** 状態設定 */
    public static String CATEGORY_STATUS = "StatusSettingActivity";
    /** 並べ替え */
    public static String CATEGORY_SORT = "SortDialog";
    /** 絞り込み */
    public static String CATEGORY_REFINE = "refineActivity";
    // ----------------------------------------------------
    // アクション
    // ----------------------------------------------------
    /** タッチスクリーン */
    public static String ACTION_BUTTON = "buttonPress";
    /** 状態追加（ラベルで色を送信） */
    public static String ACTION_ADD_STATUS = "addStatus";
    // ----------------------------------------------------
    // ラベル
    // ----------------------------------------------------
    /** タスク追加 */
    public static String LABEL_ADD_TASK = "addTask";
    /** 状態設定 */
    public static String LABEL_STATUS = "statusSetting";
    /** 並べ替え */
    public static String LABEL_SORT = "sort";
    /** 絞り込み */
    public static String LABEL_REFINE = "refine";
    /** AboutMe */
    public static String LABEL_ABOUT_ME = "aboutMe";
    /** ドロワーオープン */
    public static String LABEL_DRAWER_OPEN = "drawerOpen";
    /** ドロワークローズ */
    public static String LABEL_DRAWER_CLOSE = "drawerClose";
    /** ゴミ箱ボタンタップ */
    public static String LABEL_DELETE_TASK = "deleteTask";
    /** 並べ替え（日付昇順） */
    public static String LABEL_DATE_UP = "dateUp";
    /** 並べ替え（日付降順） */
    public static String LABEL_DATE_DOWN = "dateDown";
    /** 並べ替え（状態昇順） */
    public static String LABEL_STATUS_UP = "statusUp";
    /** 並べ替え（状態降順） */
    public static String LABEL_STATUS_DOWN = "statusDown";
    /** タスク詳細 */
    public static String LABEL_DETAIL_TASK = "detailTask";
    /** タスク編集 */
    public static String LABEL_EDIT_TASK = "editTask";
    /** タスク状態変更 */
    public static String LABEL_CHANGE_STATUS = "changeStatus";
    /** 状態追加 */
    public static String LABEL_ADD_STATUS = "addStatus";
    /** 状態消去 */
    public static String LABEL_DELETE_STATUS = "deleteStatus";

    /**
     * スクリーン計測
     *
     * @param context
     * @param screenName
     */
    public static void sendGAEventOfScreen(Context context, String screenName) {
        MeasurementGAManager.sendGAScreen(context, screenName);
    }

    /**
     * イベント計測
     *
     * @param context
     * @param category
     * @param action
     * @param label
     */
    public static void sendGAEventOfAction(Context context, String category, String action, String label) {
        MeasurementGAManager.sendGAEvent(context, category, action, label);
    }
}
