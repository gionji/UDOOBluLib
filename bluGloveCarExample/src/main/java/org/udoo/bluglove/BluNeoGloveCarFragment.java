package org.udoo.bluglove;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.udoo.udooblulib.exceptions.UdooBluException;
import org.udoo.udooblulib.interfaces.INotificationListener;
import org.udoo.udooblulib.interfaces.OnBluOperationResult;
import org.udoo.udooblulib.manager.UdooBluManagerImpl;
import org.udoo.udooblulib.model.IOPin;
import org.udoo.udooblulib.sensor.UDOOBLESensor;
import org.udoo.udooblulib.utils.Point3D;

/**
 * Created by harlem88 on 02/04/16.
 */
public class BluNeoGloveCarFragment extends Fragment {

    private static final String TAG = "BluFRa";

    public static BluNeoGloveCarFragment Builder(String address1, String address2) {
        BluNeoGloveCarFragment fragment = new BluNeoGloveCarFragment();
        Bundle bundle = new Bundle();
        bundle.putString("car", address1);
        bundle.putString("glove", address2);
        fragment.setArguments(bundle);
        return fragment;
    }

    private String mCarAddress, mGloveAddress;
    private UdooBluManagerImpl udooBluManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCarAddress = getArguments().getString("car");
            mGloveAddress = getArguments().getString("glove");
        }

        udooBluManager = ((BluNeoGloveCarApplication) getActivity().getApplication()).getBluManager();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();


//        Timer job = new Timer();
//        job.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {

//
//        udooBluManager.readAnalog(mCarAddress, IOPin.IOPIN_PIN.A1, new IReaderListener<byte[]>() {
//            @Override
//            public void oRead(byte[] value) {
//                Log.i(TAG, "oRead: " + value);
//
//            }
//
//            @Override
//            public void onError(UdooBluException runtimeException) {
//                Log.e(TAG, "onError: " + runtimeException.getReason());
//
//            }
//        });

//        udooBluManager.setPwm(mCarAddress, IOPin.IOPIN_PIN.D7, 50, 30, new OnBluOperationResult<Boolean>() {
//            @Override
//            public void onSuccess(Boolean aBoolean) {
//                Log.i(TAG, "oRead: " + aBoolean);
//            }
//
//            @Override
//            public void onError(UdooBluException runtimeException) {
//                Log.e(TAG, "onError: " + runtimeException.getReason());
//            }
//        });


//            }
//        }, 4000, 4000);

        udooBluManager.subscribeNotificationAnalog(mCarAddress, IOPin.IOPIN_PIN.A2, new INotificationListener<byte[]>() {
            @Override
            public void onNext(byte[] value) {
                float val = UDOOBLESensor.IOPIN_ANALOG.convertADC(value);
                Log.i(TAG, "onNext: " + val);
            }

            @Override
            public void onError(UdooBluException runtimeException) {
                Log.e(TAG, "onError: " + runtimeException.getReason());
            }
        });


    }






//        udooBluManager.writeDigital(mCarAddress, Constant.IOPIN_VALUE.HIGH, Constant.IOPIN.D6);
//        udooBluManager.enableSensor(mGloveAddress, UDOOBLESensor.ACCELEROMETER, true);
//        udooBluManager.setNotificationPeriod(mGloveAddress, UDOOBLESensor.ACCELEROMETER);
//
//        udooBluManager.enableSensor(mGloveAddress, UDOOBLESensor.MAGNETOMETER, true);
//        udooBluManager.setNotificationPeriod(mGloveAddress, UDOOBLESensor.MAGNETOMETER);

//        udooBluManager.enableSensor(mCarAddress, UDOOBLESensor.IOPIN, true);
//
//        udooBluManager.setIoPinMode(mCarAddress, Constant.IOPIN.D6, null);
//
//        udooBluManager.setPinAnalogOrPwmIndex(mCarAddress, Constant.IOPIN.D6);

//        udooBluManager.setFreq(mCarAddress, 80);
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    udooBluManager.digitalRead(mCarAddress, new OnCharacteristicsListener() {
//                        @Override
//                        public void onCharacteristicsRead(String uuidStr, byte[] value, int status) {
//                            Log.i("onCharacteristicsRead: ", " " + value[0]);
//                        }
//
//                        @Override
//                        public void onCharacteristicChanged(String uuidStr, byte[] rawValue) {
//
//                        }
//                    });
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }}).start();
//

//                udooBluManager.setNotificationPeriod(mCarAddress, UDOOBLESensor.IOPIN);
//
//         Observable.create(new Observable.OnSubscribe<float[]>() {
//            @Override
//            public void call(final Subscriber<? super float[]> subscriber) {
//                udooBluManager.enableNotification(mCarAddress, true, UDOOBLESensor.IOPIN, new OnCharacteristicsListener() {
//                    @Override
//                    public void onCharacteristicsRead(String uuidStr, byte[] value, int status) {
//                    }
//
//                    @Override
//                    public void onCharacteristicChanged(String uuidStr, byte[] rawValue) {
//                        float value = UDOOBLESensor.IOPIN.convertADC(rawValue);
//                        Log.i("onCharacteristicChanged: ", " " + value);
//                    }
//                });
//            }
//        });

//        final Observable<float[]> magnetomerterObservable = Observable.create(new Observable.OnSubscribe<float[]>() {
//            @Override
//            public void call(final Subscriber<? super float[]> subscriber) {
//                udooBluManager.enableNotification(mGloveAddress, true, UDOOBLESensor.MAGNETOMETER, new OnCharacteristicsListener() {
//                    @Override
//                    public void onCharacteristicsRead(String uuidStr, byte[] value, int status) {
//                    }
//
//                    @Override
//                    public void onCharacteristicChanged(String uuidStr, byte[] rawValue) {
//                        Point3D point3D = UDOOBLESensor.MAGNETOMETER.convert(rawValue);
//                        if (point3D != null)
//                            subscriber.onNext(point3D.toFloatArray());
//                    }
//                });
//            }
//        });
//
//        Observable.zip(accelerometerObservable, magnetomerterObservable, new Func2<float[], float[], float[]>() {
//                    @Override
//                    public float[] call(float[] values1, float[] values2) {
//                        float vv[] = Util.GetOrientationValues(values1, values2);
//
//                        //azimuth 0
//                        //pitch   1
//                        //roll    2
//                        Log.i("call: ", vv[0] + " " + vv[1] + " " + vv[2]);
//                        vv[2] -= 170;
//                        Constant.IOPIN ioPin[] = null;
//                        Constant.IOPIN_VALUE value = Constant.IOPIN_VALUE.LOW;
//                        if(vv[1] <  - 60) {
//                            ioPin = new Constant.IOPIN[4];
//                            ioPin[0] = Constant.IOPIN.D6;
//                            ioPin[1] = Constant.IOPIN.A0;
//                            ioPin[2] = Constant.IOPIN.A1;f
//                            ioPin[3] = Constant.IOPIN.A2;
//                            value = Constant.IOPIN_VALUE.HIGH;
//                            udooBluManager.writeDigital(mCarAddress, value, ioPin);
//                        }else{
//                            ioPin = new Constant.IOPIN[0];
//                            value = Constant.IOPIN_VALUE.LOW;
//                            udooBluManager.writeDigital(mCarAddress, value, ioPin);
//                        }
//                        return vv;
//                    }
//                }
//
//        ).onBackpressureBuffer()
//                .subscribeOn(Schedulers.io())
//                .subscribe(new Observer<float[]>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(float[] floats) {
//
//                    }
//                });
    }
