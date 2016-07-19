package org.udoo.udoobluwearexample.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.udoo.udooblulib.exceptions.UdooBluException;
import org.udoo.udooblulib.interfaces.INotificationListener;
import org.udoo.udooblulib.interfaces.OnBluOperationResult;
import org.udoo.udooblulib.model.XYZSensor;
import org.udoo.udooblulib.sensor.UDOOBLESensor;
import org.udoo.udooblulib.utils.Point3D;
import org.udoo.udoobluwearexample.databinding.FragmentHomeSensorBinding;

import java.text.DecimalFormat;

/**
 * Created by harlem88 on 19/07/16.
 */

public class MagnetometerFragment extends BaseSensorCardFragment {
    private static final String TAG = "MagnetometerFragment";
    private FragmentHomeSensorBinding mViewBinding;

    public static BaseSensorCardFragment Builder(String address){
        return Builder(new MagnetometerFragment(), address);
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateContentView(inflater, container, savedInstanceState);
        mViewBinding = FragmentHomeSensorBinding.inflate(inflater, container, false);
        return mViewBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        mUdooBluManager.subscribeNotificationMagnetometer(mBluAddress, new INotificationListener<byte[]>() {
            @Override
            public void onNext(byte[] rawValue) {
                DecimalFormat df = new DecimalFormat("#.##");
                Point3D v = UDOOBLESensor.MAGNETOMETER.convert(rawValue);
                XYZSensor xyzSensor = new XYZSensor();
                xyzSensor.name = "Magnetometer";
                xyzSensor.x = String.valueOf(df.format(v.x)) + " uT";
                xyzSensor.y = String.valueOf(df.format(v.y)) + " uT";
                xyzSensor.z = String.valueOf(df.format(v.z)) + " uT";
                mViewBinding.setSensor(xyzSensor);
            }

            @Override
            public void onError(UdooBluException runtimeException) {
                Log.e(TAG, "onError: " + runtimeException.getReason());
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mUdooBluManager.unSubscribeNotificationMagnetometer(mBluAddress, new OnBluOperationResult<Boolean>() {
            public void onSuccess(Boolean aBoolean) {
                Log.i(TAG, "onSuccess unsub" + aBoolean);
            }

            @Override
            public void onError(UdooBluException runtimeException) {
                Log.e(TAG, "onError unsub" + runtimeException.getReason());
            }
        });
    }
}