package org.udoo.bluneocar;

import android.app.Application;

import org.udoo.udooblulib.manager.UdooBluManager;

/**
 * Created by harlem88 on 24/03/16.
 */
public class BluNeoCarApplication extends Application {
    private UdooBluManager mUdooBluManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mUdooBluManager = new UdooBluManager(this);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //mUdooBluManager.stop();
        //mUdooBluManager.clear();
    }

    public UdooBluManager getBluManager(){
        return mUdooBluManager;
    }
}
