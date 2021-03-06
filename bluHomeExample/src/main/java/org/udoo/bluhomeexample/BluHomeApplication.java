package org.udoo.bluhomeexample;

import android.app.Application;

import org.udoo.udooblulib.manager.UdooBluManager;
import org.udoo.udooblulib.manager.UdooBluManagerImpl;

/**
 * Created by harlem88 on 24/03/16.
 */
public class BluHomeApplication extends Application {
    private UdooBluManager mUdooBluManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mUdooBluManager = new UdooBluManagerImpl(this);

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
