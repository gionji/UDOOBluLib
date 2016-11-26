package org.udoo.bluhomeexample.fragment.brick;

/**
 * Created by harlem88 on 26/11/16.
 */

import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.fragment.ManagerBrickFragment;
import org.udoo.bluhomeexample.fragment.UdooFragment;
import org.udoo.bluhomeexample.model.BrickModel;
import org.udoo.udooblulib.sensor.UDOOBLESensor;

/**
 * Created by harlem88 on 26/11/16.
 */

public class AmbientLuxBrickFragment extends ManagerBrickFragment {

    public static UdooFragment Builder(String address){
        return Builder(new AmbientLuxBrickFragment(), address);
    }

    @Override
    public void setBrickModel() {
        mBrickModel = BrickModel.Builder("AmbientLight", getResources().getDrawable(R.drawable.ic_light), "");
    }

    @Override
    public void subscribeOnStart() {
        mUdooBluManager.subscribeNotificationAmbientLight(mBluAddress, iNotificationListener);
    }

    @Override
    public void unSubscribeOnDestroy() {
        mUdooBluManager.unSubscribeNotificationAmbientLight(mBluAddress, onBluOperationResult);
    }

    @Override
    public String conversionByte(byte[] value) {
        return "" + UDOOBLESensor.AMBIENT_LIGHT.convertAmbientLight(value);
    }
}