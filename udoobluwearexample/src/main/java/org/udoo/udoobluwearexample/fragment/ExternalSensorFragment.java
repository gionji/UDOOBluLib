package org.udoo.udoobluwearexample.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.udoo.udooblulib.exceptions.UdooBluException;
import org.udoo.udooblulib.interfaces.INotificationListener;
import org.udoo.udooblulib.interfaces.OnBluOperationResult;
import org.udoo.udooblulib.manager.UdooBluManager;
import org.udoo.udooblulib.sensor.UDOOBLESensor;
import org.udoo.udoobluwearexample.databinding.ExternalLayoutSensorBinding;
import org.udoo.udoobluwearexample.model.Barometer;
import org.udoo.udoobluwearexample.model.Temperature;


/**
 * Created by harlem88 on 03/03/16.
 */
public class ExternalSensorFragment extends BaseSensorCardFragment {
    private static final String TAG = "ExternalSensorFragment";
    private ExternalLayoutSensorBinding mViewBinding;
    private Barometer mBarometer = new Barometer();
    private Temperature mTemperature = new Temperature();

    public static BaseSensorCardFragment Builder(String address){
        return Builder(new ExternalSensorFragment(), address);
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateContentView(inflater, container, savedInstanceState);
        mViewBinding = ExternalLayoutSensorBinding.inflate(inflater, container, false);
        return mViewBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.TEMP)) {
            mTemperature.setDetect(true);
            mUdooBluManager.subscribeNotificationTemperature(mBluAddress, new INotificationListener<byte[]>() {
                @Override
                public void onNext(byte[] value) {
                    mTemperature.setValue("" + UDOOBLESensor.TEMPERATURE.convertTemp(value));
                }

                @Override
                public void onError(UdooBluException runtimeException) {
                    Log.i(TAG, "onErrorTEmp: " + runtimeException.getReason());
                }
            });
        } else {
            mTemperature.setDetect(false);
        }
        if (mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.BAR)) {
            mBarometer.setDetect(true);
            mUdooBluManager.subscribeNotificationBarometer(mBluAddress, new INotificationListener<byte[]>() {
                @Override
                public void onNext(byte[] value) {
                    mBarometer.setValue("" + UDOOBLESensor.BAROMETER_P.convertBar(value));
                }

                @Override
                public void onError(UdooBluException runtimeException) {
                    Log.i(TAG, "onErrorBarometer: " + runtimeException.getReason());
                }
            });
        } else {
            mBarometer.setDetect(false);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.BAR)) {
            mUdooBluManager.unSubscribeNotificationBarometer(mBluAddress, new OnBluOperationResult<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                }

                @Override
                public void onError(UdooBluException runtimeException) {
                }
            });
        }


        if (mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.TEMP)) {
            mUdooBluManager.unSubscribeNotificationTemperature(mBluAddress, new OnBluOperationResult<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                }

                @Override
                public void onError(UdooBluException runtimeException) {
                }
            });
        }
    }
}
