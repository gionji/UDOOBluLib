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

public class BarometerBrickFragment extends ManagerBrickFragment {

    public static UdooFragment Builder(String address){
        return Builder(new BarometerBrickFragment(), address);
    }

    @Override
    public void setBrickModel() {
        mBrickModel = BrickModel.Builder("Barometer", getResources().getDrawable(R.drawable.barometer), "");
    }

    @Override
    public void subscribeOnStart() {
        mUdooBluManager.subscribeNotificationBarometer(mBluAddress, iNotificationListener);
    }

    @Override
    public void unSubscribeOnDestroy() {
        mUdooBluManager.unSubscribeNotificationBarometer(mBluAddress, onBluOperationResult);
    }

    @Override
    public String conversionByte(byte[] value) {
        return "" + UDOOBLESensor.BAROMETER_P.convertBar(value);
    }
}
