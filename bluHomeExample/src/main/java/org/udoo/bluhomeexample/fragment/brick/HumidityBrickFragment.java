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

public class HumidityBrickFragment extends ManagerBrickFragment {

    public static UdooFragment Builder(String address){
        return Builder(new HumidityBrickFragment(), address);
    }

    @Override
    public void setBrickModel() {
        mBrickModel = BrickModel.Builder("Humidity", getResources().getDrawable(R.drawable.ic_humidity), "");
    }

    @Override
    public void subscribeOnStart() {
        mUdooBluManager.subscribeNotificationHumidity(mBluAddress, iNotificationListener);
    }

    @Override
    public void unSubscribeOnDestroy() {
        mUdooBluManager.unSubscribeNotificationHumidity(mBluAddress, onBluOperationResult);
    }

    @Override
    public String conversionByte(byte[] value) {
        return "" + UDOOBLESensor.HUMIDITY.convertHumidity(value);
    }
}
