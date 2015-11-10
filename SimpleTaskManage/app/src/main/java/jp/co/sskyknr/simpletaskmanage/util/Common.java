package jp.co.sskyknr.simpletaskmanage.util;

public class Common {

    /**
     * 文字列がnull/空文字の場合true
     */
    public static boolean isBlank(String text) {
        if (text == null || text.length() == 0) {
            return true;
        }
        return false;
    }

}
