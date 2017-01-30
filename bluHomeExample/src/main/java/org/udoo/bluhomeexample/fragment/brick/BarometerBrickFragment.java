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

    /**
     *   20 - 110 kPa
     * */

    private static final int MIN = 20000;
    private static final int MAX = 110000;

    private final static int SAMPLES = 100;

    public static UdooFragment Builder(String address){
        return Builder(new BarometerBrickFragment(), address);
    }

    @Override
    public void setBrickModel() {
        mBrickModel = BrickModel.Builder("Barometer", getResources().getDrawable(R.drawable.barometer), getString(R.string.url_shop_brick_barometer));
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
