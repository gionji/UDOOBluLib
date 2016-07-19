package org.udoo.udoobluwearexample;

import android.app.Application;

import org.udoo.udooblulib.manager.UdooBluManagerImpl;

/**
 * Created by harlem88 on 19/07/16.
 */

public class BluWearApplication extends Application{
    private UdooBluManagerImpl mUdooBluManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mUdooBluManager = new UdooBluManagerImpl(this);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public UdooBluManagerImpl getBluManager(){
        return mUdooBluManager;
    }
}
