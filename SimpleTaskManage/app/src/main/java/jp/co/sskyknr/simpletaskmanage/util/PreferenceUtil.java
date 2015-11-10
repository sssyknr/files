package jp.co.sskyknr.simpletaskmanage.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * プリファレンス書き出しクラス
 */
public class PreferenceUtil {

    /** プリファレンス名 */
    private static final String FILE = "taskManagerPref";
    /** 初回起動フラグ_キー */
    public static final String KEY_FIRST = "firstFlag";

    /**
     * プリファレンスのフラグ操作（書き込み）
     *
     * @param key
     * @param flag
     */
    public static void writeFlag(Context context, String key, boolean flag) {
        SharedPreferences pref = context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, flag);
        editor.commit();
    }

    /**
     * プリファレンスの取得
     *
     * @param context
     * @param key
     * @return
     */
    public static SharedPreferences getPreference(Context context) {
        return context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }
}
