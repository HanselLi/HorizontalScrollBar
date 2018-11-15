package sogou.mobile.custombar;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by liyangos3323 on 2018/11/15.
 */

public class SpUtil {

    private static final String HANSEL_UTIL = "hansel_sp_util";
    private static final String CURRENT_RADIUS = "current_radius";
    private static final String CURRENT_TEXTSIZE = "current_textsize";
    private final SharedPreferences sp;

    private SpUtil() {
        sp = BrowserApp.getBrowserApp().getSharedPreferences(HANSEL_UTIL, Context.MODE_PRIVATE);
    }

    public static SpUtil getInstance() {
        return SingletonHolder.mSpUtil;
    }

    public void setCurrentTextSize(int textSize) {
        sp.edit().putInt(CURRENT_TEXTSIZE, textSize).commit();
    }

    /**
     * 默认的TextSize为19
     */
    public int getCurrentTextSize() {
        return sp.getInt(CURRENT_TEXTSIZE, HorizontalScrollBall.mWebTextSizeZoom100);
    }

    private static final class SingletonHolder {
        static SpUtil mSpUtil = new SpUtil();
    }

    public void setCurrentRadius(int radius) {
        sp.edit().putInt(CURRENT_RADIUS, radius).apply();
    }

    public int getCurrentRadius() {
        return sp.getInt(CURRENT_RADIUS, 10);
    }

}
