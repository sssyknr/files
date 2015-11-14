package jp.co.sskyknr.simpletaskmanage.util;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * 色定数クラス
 */
public class colorUtil {
    public static final String RED = "red";
    public static final String PINK = "pink";
    public static final String PURPLE = "purple";
    public static final String DEEP_PURPLE = "deep_purple";
    public static final String INDIGO = "indigo";
    public static final String BLUE = "blue";
    public static final String LIGHT_BLUE = "light_blue";
    public static final String CYAN = "cyan";
    public static final String TEAL = "teal";
    public static final String GREEN = "green";
    public static final String LIGHT_GREEN = "light_green";
    public static final String LIME = "lime";
    public static final String YELLOW = "yellow";
    public static final String AMBER = "amber";
    public static final String ORANGE = "orange";
    public static final String DEEP_ORANGE = "deep_orange";
    public static final String BROWN = "brown";
    public static final String GREY = "grey";
    public static final String BLUE_GREY = "blue_grey";
    public static final String BLACK = "black";
    public static final String WHITE = "white";

    /**
     * 色コードからバックグラウンドリソースを返す
     *
     * @param color
     * @return
     */
    public static int setBgImage(Context context, String color) {
        return context.getResources().getIdentifier(color, "drawable", context.getPackageName());
    }
}
