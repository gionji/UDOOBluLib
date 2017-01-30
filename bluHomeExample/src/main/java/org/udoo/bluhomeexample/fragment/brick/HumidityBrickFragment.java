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

    /**
     * 0 - 100 % RH
     */

    private static final int MIN = 0;
    private static final int MAX = 100;

    private final static int SAMPLES = 100;

    public static UdooFragment Builder(String address) {
        return Builder(new HumidityBrickFragment(), address);
    }

    @Override
    public void setBrickModel() {
        mBrickModel = BrickModel.Builder("Humidity", getResources().getDrawable(R.drawable.ic_humidity), getString(R.string.url_shop_brick_humidity));
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

    @Override
    public int getProgressValue(String value) {
        int iValue = Integer.parseInt(value);
        if (iValue < MIN) iValue = MIN;
        else if (iValue > MAX) iValue = MAX;
        return iValue;
    }

    @Override
    public int getProgressMax() {
        return SAMPLES;
    }
}
