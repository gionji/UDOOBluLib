package org.udoo.bluhomeexample.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.udoo.bluhomeexample.BluHomeApplication;
import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.adapter.LedAdapter;
import org.udoo.bluhomeexample.adapter.XYZSensorSummaryAdapter;
import org.udoo.bluhomeexample.databinding.ExternalSensorLayoutBinding;
import org.udoo.bluhomeexample.databinding.FragmentSensorHomeBinding;
import org.udoo.bluhomeexample.model.Barometer;
import org.udoo.bluhomeexample.model.Humidity;
import org.udoo.bluhomeexample.model.Led;
import org.udoo.bluhomeexample.model.Light;
import org.udoo.udooblulib.exceptions.UdooBluException;
import org.udoo.udooblulib.interfaces.INotificationListener;
import org.udoo.udooblulib.interfaces.OnBluOperationResult;
import org.udoo.bluhomeexample.model.Temperature;
import org.udoo.udooblulib.manager.UdooBluManager;
import org.udoo.udooblulib.model.XYZSensor;
import org.udoo.udooblulib.sensor.UDOOBLE;
import org.udoo.udooblulib.sensor.UDOOBLESensor;
import org.udoo.udooblulib.utils.Point3D;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by harlem88 on 28/06/16.
 */

public class ManagerHomeSensorFragment extends UdooFragment {
    private RecyclerView mLedList;
    private RecyclerView.LayoutManager mIntSensorLayoutManager, mLedLayoutManager;

    private LedAdapter mLedAdapter;
    private XYZSensorSummaryAdapter mXyzSensorAdapter;

    private Led[] mLeds;
    private List<XYZSensor> mXyzIntSensors;
    private Barometer mBarometer = new Barometer();
    private Temperature mTemperature = new Temperature();
    private Humidity mHumidity = new Humidity();
    private Light mLight = new Light();
    private FragmentSensorHomeBinding mViewHomeBinding;
    private static final String ADDRESS = "addr";
    DecimalFormat df = new DecimalFormat("#.##");
    private final static String TAG = "Home";
    private boolean isDisconnected;



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

        mLedList = (RecyclerView) view.findViewById(R.id.list);
        mLedList.setHasFixedSize(true);
        mViewHomeBinding.xyzSensorInternalList.setHasFixedSize(true);

        mIntSensorLayoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.grid));
        mLedLayoutManager = new LinearLayoutManager(getActivity());

        mViewHomeBinding.xyzSensorInternalList.setLayoutManager(mIntSensorLayoutManager);
        mLedList.setLayoutManager(mLedLayoutManager);

        mLeds = getLedsDefault();
        mXyzIntSensors = getIntSensors();
        mLedAdapter = new LedAdapter(mLeds);
        mXyzSensorAdapter = new XYZSensorSummaryAdapter(mXyzIntSensors);

        mViewHomeBinding.setBarometer(mBarometer);
        mViewHomeBinding.setTemperature(mTemperature);
        mViewHomeBinding.setHumidity(mHumidity);
        mViewHomeBinding.setLight(mLight);

                //mLedList.setAdapter(mLedAdapter);
        mViewHomeBinding.xyzSensorInternalList.setAdapter(mXyzSensorAdapter);

//        mIFragmentToActivity = (IFragmentToActivity) getActivity();
        mLedAdapter.setItemCLickListner(new LedAdapter.ItemCLickListner() {
            @Override
            public void onItemClickListener(int pos) {
                Led led = mLeds[pos];
//                byte func = led.onoff.get() ? Constant.LED_OFF : Constant.LED_ON;
//                if (mIBleBrickOp != null && mIBleBrickOp.turnLed(pos + 1, func, 100))
//                    led.onoff.set(func == Constant.LED_ON);
            }

            @Override
            public void onBlinkListener(int pos, boolean blink) {
                Led led = mLeds[pos];
//                byte func = led.blink.get() ? Constant.LED_OFF : Constant.BLINK_ON;
//                if (mIBleBrickOp != null && mIBleBrickOp.turnLed(pos + 1, func, 100)) {
//                    if (func == Constant.LED_OFF) {
//                        led.blink.set(false);
//                        led.onoff.set(false);
//                    } else led.blink.set(true);
//                }
            }
        });

        mXyzSensorAdapter.setSensorViewClickListener(new XYZSensorSummaryAdapter.SensorViewClickListener() {
            @Override
            public void onSensorViewClicked(String name) {
//                if(mIFragmentToActivity != null)
//                    mIFragmentToActivity.onXYZSummaryClick(name);
            }
        });

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

    private List<XYZSensor> getIntSensors() {
        List<XYZSensor> xyzSensors = new ArrayList<>();
        xyzSensors.add(XYZSensor.Builder(getString(R.string.title_section2), getResources().getDrawable(R.drawable.accelerometer)));
        xyzSensors.add(XYZSensor.Builder(getString(R.string.title_section3), getResources().getDrawable(R.drawable.gyro)));
        xyzSensors.add(XYZSensor.Builder(getString(R.string.title_section4), getResources().getDrawable(R.drawable.magnetometer)));
        return xyzSensors;
    }

    @Override
    public void onResume() {
        super.onResume();
        mUdooBluManager = ((BluHomeApplication)getActivity().getApplication()).getBluManager();
        subscribeNotification();
    }

    @Override
    public void onConnect() {
        super.onConnect();
        if (isResumed() || isDisconnected) {
            subscribeNotification();
            isDisconnected = false;
        }
    }

    @Override
    public void onDisconnect() {
        super.onDisconnect();
        isDisconnected = true;
    }

    private void subscribeNotification(){
        mUdooBluManager.subscribeNotificationAccelerometer(mBluAddress, new INotificationListener<byte[]>() {
            @Override
            public void onNext(byte[] rawValue) {
                Point3D v = UDOOBLESensor.ACCELEROMETER.convert(rawValue);
                XYZSensor xyzSensor = mXyzIntSensors.get(0);
                xyzSensor.x = String.valueOf(df.format(v.x)) + " m/s^2";
                xyzSensor.y = String.valueOf(df.format(v.y)) + " m/s^2";
                xyzSensor.z = String.valueOf(df.format(v.z)) + " m/s^2";
                mXyzSensorAdapter.notifyItemChanged(0, xyzSensor);
            }

            @Override
            public void onError(UdooBluException runtimeException) {
                Log.e(TAG, "onError: "+runtimeException.getReason());
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

        mUdooBluManager.subscribeNotificationGyroscope(mBluAddress, new INotificationListener<byte[]>() {
            @Override
            public void onNext(byte[] rawValue) {
                Point3D v = UDOOBLESensor.GYROSCOPE.convert(rawValue);
                XYZSensor xyzSensor = mXyzIntSensors.get(1);
                xyzSensor.x = String.valueOf(df.format(v.x)) + " rad/s";
                xyzSensor.y = String.valueOf(df.format(v.y)) + " rad/s";
                xyzSensor.z = String.valueOf(df.format(v.z)) + " rad/s";
                mXyzSensorAdapter.notifyItemChanged(1, xyzSensor);
            }

            @Override
            public void onError(UdooBluException runtimeException) {
                Log.e(TAG, "onError: "+runtimeException.getReason());
            }
        });
            }
        }, 400);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                mUdooBluManager.subscribeNotificationMagnetometer(mBluAddress, new INotificationListener<byte[]>() {
                    @Override
                    public void onNext(byte[] rawValue) {
                        Point3D v = UDOOBLESensor.MAGNETOMETER.convert(rawValue);
                        XYZSensor xyzSensor = mXyzIntSensors.get(2);
                        xyzSensor.x = String.valueOf(df.format(v.x)) + " uT";
                        xyzSensor.y = String.valueOf(df.format(v.y)) + " uT";
                        xyzSensor.z = String.valueOf(df.format(v.z)) + " uT";
                        mXyzSensorAdapter.notifyItemChanged(2, xyzSensor);
                    }

                    @Override
                    public void onError(UdooBluException runtimeException) {
                        Log.e(TAG, "onError: " + runtimeException.getReason());
                    }
                });

            }
        }, 600);

        if(mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.HUM)){
            mHumidity.setDetect(true);
            mUdooBluManager.subscribeNotificationHumidity(mBluAddress, new INotificationListener<byte[]>() {
                @Override
                public void onNext(byte[] value) {
                    mHumidity.setValue(""+UDOOBLESensor.HUMIDITY.convertHumidity(value));
                }

                @Override
                public void onError(UdooBluException runtimeException) {
                    Log.e(TAG, "onErrorHumidity: "+runtimeException.getReason());
                }
            });
        }else{
            mHumidity.setDetect(false);
        }
        if(mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.AMB_LIG)){
            mLight.setDetect(true);
            mUdooBluManager.subscribeNotificationAmbientLight(mBluAddress, new INotificationListener<byte[]>() {
                @Override
                public void onNext(byte[] value) {
                    mLight.setValue(""+UDOOBLESensor.AMBIENT_LIGHT.convertAmbientLight(value));
                }

                @Override
                public void onError(UdooBluException runtimeException) {
                    Log.e(TAG, "onErrorLight: "+runtimeException.getReason());
                }
            });
        }else{
            mLight.setDetect(false);
        }


        if(mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.TEMP)){
            mTemperature.setDetect(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

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
                }
            }, 600);
        }else{
            mTemperature.setDetect(false);
        }
        if(mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.BAR)){
            mBarometer.setDetect(true);
            mUdooBluManager.subscribeNotificationBarometer(mBluAddress, new INotificationListener<byte[]>() {
                @Override
                public void onNext(byte[] value) {
                    mBarometer.setValue(""+UDOOBLESensor.BAROMETER_P.convertBar(value));
                }

                @Override
                public void onError(UdooBluException runtimeException) {
                    Log.i(TAG, "onErrorBarometer: "+runtimeException.getReason());
                }
            });
        }else{
            mBarometer.setDetect(false);
        }
    }

    private void unsubscribeNotification(){
        mUdooBluManager.unSubscribeNotificationAccelerometer(mBluAddress, new OnBluOperationResult<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                Log.i(TAG, "onSuccess unsub: acc" + aBoolean);
            }

            @Override
            public void onError(UdooBluException runtimeException) {
                Log.e(TAG, "onError unsub: acc" + runtimeException.getReason());
            }
        });

        mUdooBluManager.unSubscribeNotificationGyroscope(mBluAddress, new OnBluOperationResult<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                Log.i(TAG, "onSuccess unsub: gyro" + aBoolean);
            }

            @Override
            public void onError(UdooBluException runtimeException) {
                Log.e(TAG, "onError unsub: gyro" + runtimeException.getReason());
            }
        });

        mUdooBluManager.unSubscribeNotificationMagnetometer(mBluAddress, new OnBluOperationResult<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                Log.i(TAG, "onSuccess unsub: magn" + aBoolean);
            }

            @Override
            public void onError(UdooBluException runtimeException) {
                Log.e(TAG, "onError unsub: magn" + runtimeException.getReason());
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        unsubscribeNotification();
    }
}
