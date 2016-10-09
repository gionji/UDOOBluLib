package org.udoo.bluhomeexample.fragment;

import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.udoo.bluhomeexample.BluHomeApplication;
import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.adapter.HomeSensorAdapter;

import org.udoo.bluhomeexample.databinding.FragmentSensorHomeBinding;
import org.udoo.bluhomeexample.decoration.MarginDecoration;
import org.udoo.bluhomeexample.model.BluSensor;
import org.udoo.bluhomeexample.model.IntBluSensor;
import org.udoo.bluhomeexample.model.Led;
import org.udoo.udooblulib.exceptions.UdooBluException;
import org.udoo.udooblulib.interfaces.INotificationListener;
import org.udoo.udooblulib.interfaces.OnBluOperationResult;
import org.udoo.udooblulib.manager.UdooBluManager;
import org.udoo.udooblulib.model.XYZSensor;
import org.udoo.udooblulib.sensor.UDOOBLESensor;
import org.udoo.udooblulib.utils.Point3D;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by harlem88 on 28/06/16.
 */

public class ManagerHomeSensorFragment extends UdooFragment {
    private GridLayoutManager mSensorLayoutManager;


    private FragmentSensorHomeBinding mViewHomeBinding;
    private static final String ADDRESS = "addr";
    DecimalFormat df = new DecimalFormat("#.##");
    private final static String TAG = "Home";
    private boolean isDisconnected;
    private List<BluSensor> mBluSensors;
    private HomeSensorAdapter mHomeSensorAdapter;

    public static UdooFragment Builder(String address){
        return Builder(new ManagerHomeSensorFragment(), address);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mViewHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sensor_home, container, false);
        return mViewHomeBinding.getRoot();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewHomeBinding.sensorList.setHasFixedSize(true);


        mSensorLayoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.grid));
        mHomeSensorAdapter = new HomeSensorAdapter(new ArrayList<BluSensor>());
        mViewHomeBinding.sensorList.addItemDecoration(new MarginDecoration(getContext()));
        mSensorLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (position < 4 ? 2 : 1);
            }
        });

        mViewHomeBinding.sensorList.setLayoutManager(mSensorLayoutManager);
        mViewHomeBinding.sensorList.setAdapter(mHomeSensorAdapter);

//        mIFragmentToActivity = (IFragmentToActivity) getActivity();
//        mLedAdapter.setItemCLickListner(new LedAdapter.ItemCLickListner() {
//            @Override
//            public void onItemClickListener(int pos) {

//                Led led = mLeds[pos];
//                byte func = led.onoff.get() ? Constant.LED_OFF : Constant.LED_ON;
//                if (mIBleBrickOp != null && mIBleBrickOp.turnLed(pos + 1, func, 100))
//                    led.onoff.set(func == Constant.LED_ON);
            }
//
//            @Override
//            public void onBlinkListener(int pos, boolean blink) {
//                Led led = mLeds[pos];
//                byte func = led.blink.get() ? Constant.LED_OFF : Constant.BLINK_ON;
//                if (mIBleBrickOp != null && mIBleBrickOp.turnLed(pos + 1, func, 100)) {
//                    if (func == Constant.LED_OFF) {
//                        led.blink.set(false);
//                        led.onoff.set(false);
//                    } else led.blink.set(true);
//                }
//            }
//        });


    @Override
    public void onStart() {
        super.onStart();
        mUdooBluManager = ((BluHomeApplication)getActivity().getApplication()).getBluManager();
        mBluSensors = new ArrayList<>();
        addExtSensors();
        addIntSensors();
        mHomeSensorAdapter.addSensors(mBluSensors);
    }

    private Led[] getLedsDefault() {
//        Led[] leds = new Led[3];
//        Led led = Led.BuilderDefault();
//        led.color.set(Color.GREEN);
//        leds[0] = led;
//        led = Led.BuilderDefault();
//        led.color.set(Color.YELLOW);
//        leds[1] = led;
//        led = Led.BuilderDefault();
//        led.color.set(Color.RED);
//        leds[2] = led;

        return null;
    }

    private void addExtSensors() {
        mBluSensors.add(0, BluSensor.Builder(getString(R.string.temperature), getResources().getDrawable(R.drawable.temperature), mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.TEMP)));
        mBluSensors.add(1, BluSensor.Builder(getString(R.string.ambient_light), getResources().getDrawable(R.drawable.ic_light), mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.AMB_LIG)));
        mBluSensors.add(2, BluSensor.Builder(getString(R.string.humidity), getResources().getDrawable(R.drawable.ic_humidity), mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.HUM)));
        mBluSensors.add(3, BluSensor.Builder(getString(R.string.barometer), getResources().getDrawable(R.drawable.barometer), mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.BAR)));
    }

    private void addIntSensors() {
        mBluSensors.add(4, IntBluSensor.Builder(getString(R.string.title_section2), getResources().getDrawable(R.drawable.accelerometer)));
        mBluSensors.add(5, IntBluSensor.Builder(getString(R.string.title_section3), getResources().getDrawable(R.drawable.gyro)));
        mBluSensors.add(6, IntBluSensor.Builder(getString(R.string.title_section4), getResources().getDrawable(R.drawable.magnetometer)));
    }


    @Override
    public void onResume() {
        super.onResume();

//        subscribeNotification();
    }

    @Override
    public void onConnect() {
        super.onConnect();
//        if (isResumed() || isDisconnected) {
//            subscribeNotification();
//            isDisconnected = false;
//        }
    }

    @Override
    public void onDisconnect() {
        super.onDisconnect();
        isDisconnected = true;
    }

//    private void subscribeNotification(){
//        mUdooBluManager.subscribeNotificationAccelerometer(mBluAddress, new INotificationListener<byte[]>() {
//            @Override
//            public void onNext(byte[] rawValue) {
//                Point3D v = UDOOBLESensor.ACCELEROMETER.convert(rawValue);
//                XYZSensor xyzSensor = mXyzIntSensors.get(0);
//                xyzSensor.x = String.valueOf(df.format(v.x)) + " m/s^2";
//                xyzSensor.y = String.valueOf(df.format(v.y)) + " m/s^2";
//                xyzSensor.z = String.valueOf(df.format(v.z)) + " m/s^2";
//                mXyzSensorAdapter.notifyItemChanged(0, xyzSensor);
//            }
//
//            @Override
//            public void onError(UdooBluException runtimeException) {
//                Log.e(TAG, "onError: "+runtimeException.getReason());
//            }
//        });
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//        mUdooBluManager.subscribeNotificationGyroscope(mBluAddress, new INotificationListener<byte[]>() {
//            @Override
//            public void onNext(byte[] rawValue) {
//                Point3D v = UDOOBLESensor.GYROSCOPE.convert(rawValue);
//                XYZSensor xyzSensor = mXyzIntSensors.get(1);
//                xyzSensor.x = String.valueOf(df.format(v.x)) + " rad/s";
//                xyzSensor.y = String.valueOf(df.format(v.y)) + " rad/s";
//                xyzSensor.z = String.valueOf(df.format(v.z)) + " rad/s";
//                mXyzSensorAdapter.notifyItemChanged(1, xyzSensor);
//            }
//
//            @Override
//            public void onError(UdooBluException runtimeException) {
//                Log.e(TAG, "onError: "+runtimeException.getReason());
//            }
//        });
//            }
//        }, 400);
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                mUdooBluManager.subscribeNotificationMagnetometer(mBluAddress, new INotificationListener<byte[]>() {
//                    @Override
//                    public void onNext(byte[] rawValue) {
//                        Point3D v = UDOOBLESensor.MAGNETOMETER.convert(rawValue);
//                        XYZSensor xyzSensor = mXyzIntSensors.get(2);
//                        xyzSensor.x = String.valueOf(df.format(v.x)) + " uT";
//                        xyzSensor.y = String.valueOf(df.format(v.y)) + " uT";
//                        xyzSensor.z = String.valueOf(df.format(v.z)) + " uT";
//                        mXyzSensorAdapter.notifyItemChanged(2, xyzSensor);
//                    }
//
//                    @Override
//                    public void onError(UdooBluException runtimeException) {
//                        Log.e(TAG, "onError: " + runtimeException.getReason());
//                    }
//                });
//
//            }
//        }, 600);
//
//        if(mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.HUM)){
//            mHumidity.setDetect(true);
//            mUdooBluManager.subscribeNotificationHumidity(mBluAddress, new INotificationListener<byte[]>() {
//                @Override
//                public void onNext(byte[] value) {
//                    mHumidity.setValue(""+UDOOBLESensor.HUMIDITY.convertHumidity(value));
//                }
//
//                @Override
//                public void onError(UdooBluException runtimeException) {
//                    Log.e(TAG, "onErrorHumidity: "+runtimeException.getReason());
//                }
//            });
//        }else{
//            mHumidity.setDetect(false);
//        }
//        if(mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.AMB_LIG)){
//            mLight.setDetect(true);
//            mUdooBluManager.subscribeNotificationAmbientLight(mBluAddress, new INotificationListener<byte[]>() {
//                @Override
//                public void onNext(byte[] value) {
//                    mLight.setValue(""+UDOOBLESensor.AMBIENT_LIGHT.convertAmbientLight(value));
//                }
//
//                @Override
//                public void onError(UdooBluException runtimeException) {
//                    Log.e(TAG, "onErrorLight: "+runtimeException.getReason());
//                }
//            });
//        }else{
//            mLight.setDetect(false);
//        }
//
//
//        if(mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.TEMP)){
//            mTemperature.setDetect(true);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//                    mUdooBluManager.subscribeNotificationTemperature(mBluAddress, new INotificationListener<byte[]>() {
//                        @Override
//                        public void onNext(byte[] value) {
//                            mTemperature.setValue("" + UDOOBLESensor.TEMPERATURE.convertTemp(value));
//                        }
//
//                        @Override
//                        public void onError(UdooBluException runtimeException) {
//                            Log.i(TAG, "onErrorTEmp: " + runtimeException.getReason());
//                        }
//                    });
//                }
//            }, 600);
//        }else{
//            mTemperature.setDetect(false);
//        }
//        if(mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.BAR)){
//            mBarometer.setDetect(true);
//            mUdooBluManager.subscribeNotificationBarometer(mBluAddress, new INotificationListener<byte[]>() {
//                @Override
//                public void onNext(byte[] value) {
//                    mBarometer.setValue(""+UDOOBLESensor.BAROMETER_P.convertBar(value));
//                }
//
//                @Override
//                public void onError(UdooBluException runtimeException) {
//                    Log.i(TAG, "onErrorBarometer: "+runtimeException.getReason());
//                }
//            });
//        }else{
//            mBarometer.setDetect(false);
//        }
//    }
//
//    private void unsubscribeNotification(){
//        mUdooBluManager.unSubscribeNotificationAccelerometer(mBluAddress, new OnBluOperationResult<Boolean>() {
//            @Override
//            public void onSuccess(Boolean aBoolean) {
//                Log.i(TAG, "onSuccess unsub: acc" + aBoolean);
//            }
//
//            @Override
//            public void onError(UdooBluException runtimeException) {
//                Log.e(TAG, "onError unsub: acc" + runtimeException.getReason());
//            }
//        });
//
//        mUdooBluManager.unSubscribeNotificationGyroscope(mBluAddress, new OnBluOperationResult<Boolean>() {
//            @Override
//            public void onSuccess(Boolean aBoolean) {
//                Log.i(TAG, "onSuccess unsub: gyro" + aBoolean);
//            }
//
//            @Override
//            public void onError(UdooBluException runtimeException) {
//                Log.e(TAG, "onError unsub: gyro" + runtimeException.getReason());
//            }
//        });
//
//        mUdooBluManager.unSubscribeNotificationMagnetometer(mBluAddress, new OnBluOperationResult<Boolean>() {
//            @Override
//            public void onSuccess(Boolean aBoolean) {
//                Log.i(TAG, "onSuccess unsub: magn" + aBoolean);
//            }
//
//            @Override
//            public void onError(UdooBluException runtimeException) {
//                Log.e(TAG, "onError unsub: magn" + runtimeException.getReason());
//            }
//        });
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        unsubscribeNotification();
//    }
}
