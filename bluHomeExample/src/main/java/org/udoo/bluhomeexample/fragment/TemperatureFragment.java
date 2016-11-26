package org.udoo.bluhomeexample.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.udoo.bluhomeexample.BluHomeApplication;
import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.databinding.ExternalLayoutBinding;
import org.udoo.bluhomeexample.model.BluSensor;
import org.udoo.udooblulib.exceptions.UdooBluException;
import org.udoo.udooblulib.interfaces.INotificationListener;
import org.udoo.udooblulib.interfaces.OnBluOperationResult;
import org.udoo.udooblulib.sensor.UDOOBLESensor;

/**
 * Created by harlem88 on 19/10/16.
 */

public class TemperatureFragment extends UdooFragment {
//
//    private static final String ADDRESS = "addr";
//    private final static String TAG = "Home";
//    private boolean isDisconnected;
//    private ExternalLayoutBinding mViewBinding;
//    private BluSensor mTemperatureSensor;
//    private Handler mHandler;
//
//    public static UdooFragment Builder(String address) {
//        return Builder(new TemperatureFragment(), address);
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mTemperatureSensor = BluSensor.Builder(getString(R.string.temperature), getResources().getDrawable(R.drawable.temperature), true);
//        mHandler = new Handler();
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        super.onCreateView(inflater, container,savedInstanceState);
//        mViewBinding = DataBindingUtil.inflate(inflater, R.layout.external_layout, container, false);
//        return mViewBinding.getRoot();
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mViewBinding.setSensorExt(mTemperatureSensor);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        mUdooBluManager = ((BluHomeApplication) getActivity().getApplication()).getBluManager();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        subscribeNotification();
//    }
//
//    private void subscribeNotification() {
//        mUdooBluManager.subscribeNotificationTemperature(mBluAddress, new INotificationListener<byte[]>() {
//            @Override
//            public void onNext(byte[] value) {
//                mTemperatureSensor.value = UDOOBLESensor.TEMPERATURE.convertTemp(value) + " °";
////                mViewBinding.setSensorExt(mTemperatureSensor);
//            }
//
//            @Override
//            public void onError(UdooBluException runtimeException) {
//                Log.i(TAG, "onErrorTEmp: " + runtimeException.getReason());
//            }
//        });
//    }
//
//    @Override
//    public void onConnect() {
//        super.onConnect();
////        if (isResumed() || isDisconnected) {
////            subscribeNotification();
////            isDisconnected = false;
////        }
//    }
//
//    @Override
//    public void onDisconnect() {
//        super.onDisconnect();
//        isDisconnected = true;
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        mUdooBluManager.unSubscribeNotificationTemperature(mBluAddress, new OnBluOperationResult<Boolean>() {
//            @Override
//            public void onSuccess(Boolean aBoolean) {
//
//            }
//
//            @Override
//            public void onError(UdooBluException runtimeException) {
//
//            }
//        });
//    }
}