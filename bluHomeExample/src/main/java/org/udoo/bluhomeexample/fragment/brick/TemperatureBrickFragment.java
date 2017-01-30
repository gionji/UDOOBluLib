package org.udoo.bluhomeexample.fragment.brick;

import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.fragment.ManagerBrickFragment;
import org.udoo.bluhomeexample.fragment.UdooFragment;
import org.udoo.bluhomeexample.model.BrickModel;
import org.udoo.udooblulib.sensor.UDOOBLESensor;

/**
 * Created by harlem88 on 26/11/16.
 */

public class TemperatureBrickFragment extends ManagerBrickFragment {

    private static final int MIN = -20;
    private static final int MAX = 85;

    private final static int SAMPLES = 100;

    public static UdooFragment Builder(String address){
        return Builder(new TemperatureBrickFragment(), address);
    }

    @Override
    public void setBrickModel() {
        mBrickModel = BrickModel.Builder("Temperature", getResources().getDrawable(R.drawable.temperature), getString(R.string.url_shop_brick_temperature));
    }

    @Override
    public void subscribeOnStart() {
        mUdooBluManager.subscribeNotificationTemperature(mBluAddress, iNotificationListener);
    }

    @Override
    public void unSubscribeOnDestroy() {
        mUdooBluManager.unSubscribeNotificationTemperature(mBluAddress, onBluOperationResult);
    }

    @Override
    public String conversionByte(byte[] value) {
        return "" + UDOOBLESensor.TEMPERATURE.convertTemp(value);
    }

    @Override
    public int getProgressValue(String value) {
        int iValue = Integer.parseInt(value);
        return iValue + Math.abs(MIN);
    }

    @Override
    public int getProgressMax() {
        return SAMPLES;
    }


}
