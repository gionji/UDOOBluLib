package org.udoo.bluhomeexample.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
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
import org.udoo.udooblulib.model.IOPin;
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
    private int stateLux;

    public static UdooFragment Builder(String address) {
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

        RecyclerView.ItemAnimator animator = mViewHomeBinding.sensorList.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        mSensorLayoutManager.setSpanSizeLookup(mHomeSensorAdapter.getSpanSizeLookup());

//        mSensorLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                return (position < 4 ? 2 : 1);
//            }
//        });

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
        mUdooBluManager = ((BluHomeApplication) getActivity().getApplication()).getBluManager();
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

        subscribeNotification();
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

    private void subscribeNotification() {
        mUdooBluManager.subscribeNotificationAccelerometer(mBluAddress, new INotificationListener<byte[]>() {
            @Override
            public void onNext(byte[] rawValue) {
                Point3D v = UDOOBLESensor.ACCELEROMETER.convert(rawValue);

                IntBluSensor acc = (IntBluSensor) mBluSensors.get(4);
                acc.x = String.valueOf(df.format(v.x)) + " m/s^2";
                acc.y = String.valueOf(df.format(v.y)) + " m/s^2";
                acc.z = String.valueOf(df.format(v.z)) + " m/s^2";

                mHomeSensorAdapter.updateSensor(4, mBluSensors.get(4));
            }

            @Override
            public void onError(UdooBluException runtimeException) {
                Log.e(TAG, "onError: " + runtimeException.getReason());
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mUdooBluManager.subscribeNotificationGyroscope(mBluAddress, new INotificationListener<byte[]>() {
                    @Override
                    public void onNext(byte[] rawValue) {
                        Point3D v = UDOOBLESensor.GYROSCOPE.convert(rawValue);
                        IntBluSensor gyr = (IntBluSensor) mBluSensors.get(5);

                        gyr.x = String.valueOf(df.format(v.x)) + " rad/s";
                        gyr.y = String.valueOf(df.format(v.y)) + " rad/s";
                        gyr.z = String.valueOf(df.format(v.z)) + " rad/s";

                        mHomeSensorAdapter.updateSensor(5, mBluSensors.get(5));
                    }

                    @Override
                    public void onError(UdooBluException runtimeException) {
                        Log.e(TAG, "onError: " + runtimeException.getReason());
                    }
                });
            }
        }, 500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                mUdooBluManager.subscribeNotificationMagnetometer(mBluAddress, new INotificationListener<byte[]>() {
                    @Override
                    public void onNext(byte[] rawValue) {
                        Point3D v = UDOOBLESensor.MAGNETOMETER.convert(rawValue);

                        IntBluSensor magn = (IntBluSensor) mBluSensors.get(6);

                        magn.x = String.valueOf(df.format(v.x)) + " uT";
                        magn.y = String.valueOf(df.format(v.y)) + " uT";
                        magn.z = String.valueOf(df.format(v.z)) + " uT";

                        mHomeSensorAdapter.updateSensor(6, mBluSensors.get(6));
                    }

                    @Override
                    public void onError(UdooBluException runtimeException) {
                        Log.e(TAG, "onError: " + runtimeException.getReason());
                    }
                });

            }
        }, 700);

        if (mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.TEMP)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mUdooBluManager.subscribeNotificationTemperature(mBluAddress, new INotificationListener<byte[]>() {
                        @Override
                        public void onNext(byte[] value) {
                            mBluSensors.get(0).value = UDOOBLESensor.TEMPERATURE.convertTemp(value) + " °";
                            mHomeSensorAdapter.updateSensor(0, mBluSensors.get(0));
                        }

                        @Override
                        public void onError(UdooBluException runtimeException) {
                            Log.i(TAG, "onErrorTEmp: " + runtimeException.getReason());
                        }
                    });
                }
            }, 1200);
        }

        if (mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.HUM)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    mUdooBluManager.subscribeNotificationHumidity(mBluAddress, new INotificationListener<byte[]>() {
                        @Override
                        public void onNext(byte[] value) {
                            mBluSensors.get(2).value = UDOOBLESensor.HUMIDITY.convertHumidity(value) + " ";
                            mHomeSensorAdapter.updateSensor(2, mBluSensors.get(2));
                        }

                        @Override
                        public void onError(UdooBluException runtimeException) {
                            Log.e(TAG, "onErrorHumidity: " + runtimeException.getReason());
                        }
                    }, 1000);
                }
            }, 1400);
        }

        if (mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.BAR)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    mUdooBluManager.subscribeNotificationBarometer(mBluAddress, new INotificationListener<byte[]>() {
                        @Override
                        public void onNext(byte[] value) {
                            mBluSensors.get(3).value = UDOOBLESensor.BAROMETER_P.convertBar(value) + " ";
                            mHomeSensorAdapter.updateSensor(3, mBluSensors.get(3));
                        }

                        @Override
                        public void onError(UdooBluException runtimeException) {
                            Log.i(TAG, "onErrorBarometer: " + runtimeException.getReason());
                        }
                    }, 1000);
                }
            }, 1600);
        }


        if (mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.AMB_LIG)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mUdooBluManager.subscribeNotificationAmbientLight(mBluAddress, new INotificationListener<byte[]>() {
                        @Override
                        public void onNext(byte[] value) {

                            int lux = UDOOBLESensor.AMBIENT_LIGHT.convertAmbientLight(value);
                            mBluSensors.get(1).value = lux + " lux";
                            mHomeSensorAdapter.updateSensor(1, mBluSensors.get(1));

                            final IOPin.IOPIN_DIGITAL_VALUE valueDig = lux < 50 ? IOPin.IOPIN_DIGITAL_VALUE.HIGH : IOPin.IOPIN_DIGITAL_VALUE.LOW;

                            if (stateLux != valueDig.ordinal()) {
                                mUdooBluManager.writeDigital(mBluAddress, new OnBluOperationResult<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean aBoolean) {
                                        if (aBoolean) {
                                            stateLux = valueDig.ordinal();
                                        }
                                    }

                                    @Override

                                    public void onError(UdooBluException runtimeException) {
                                    }
                                }, IOPin.Builder(IOPin.IOPIN_PIN.A1, valueDig));
                            } else {

                            }
                        }

                        @Override
                        public void onError(UdooBluException runtimeException) {
                            Log.e(TAG, "onErrorLight: " + runtimeException.getReason());
                        }
                    });

                    Handler handler = new Handler();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mUdooBluManager.setIoPinMode(mBluAddress, new OnBluOperationResult<Boolean>() {
                                @Override
                                public void onSuccess(Boolean aBoolean) {

                                }

                                @Override
                                public void onError(UdooBluException runtimeException) {

                                }
                            }, IOPin.Builder(IOPin.IOPIN_PIN.A1, IOPin.IOPIN_MODE.DIGITAL_OUTPUT));
                        }
                    }, 2000);
                }
            }, 1800);
        }
    }


    private void unsubscribeNotification() {
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
