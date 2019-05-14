package id.sentuh.digitalsignage.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by sony on 2/15/2018.
 */

public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        FlowManager.init(new FlowConfig.Builder(this).build());
        FlowManager.init(this);
    }
}
