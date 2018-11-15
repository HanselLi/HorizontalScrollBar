package sogou.mobile.custombar;

import android.app.Application;

/**
 * Created by liyangos3323 on 2018/11/15.
 */

public class BrowserApp extends Application {

    public static BrowserApp mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }

    public static BrowserApp getBrowserApp() {
        return mApplication;
    }
}
