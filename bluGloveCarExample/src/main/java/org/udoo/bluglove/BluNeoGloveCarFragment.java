package org.udoo.bluglove;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import org.udoo.udooblulib.interfaces.OnCharacteristicsListener;
import org.udoo.udooblulib.manager.UdooBluManager;
import org.udoo.udooblulib.model.IOPin;
import org.udoo.udooblulib.sensor.Constant;
import org.udoo.udooblulib.sensor.UDOOBLE;
import org.udoo.udooblulib.sensor.UDOOBLESensor;
import org.udoo.udooblulib.utils.Point3D;

import java.util.UUID;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by harlem88 on 02/04/16.
 */
public class BluNeoGloveCarFragment extends Fragment {

    public static BluNeoGloveCarFragment Builder(String address1, String address2) {
        BluNeoGloveCarFragment fragment = new BluNeoGloveCarFragment();
        Bundle bundle = new Bundle();
        bundle.putString("car", address1);
        bundle.putString("glove", address2);
        fragment.setArguments(bundle);
        return fragment;
    }

    private String mCarAddress, mGloveAddress;
    private UdooBluManager udooBluManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
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

//        udooBluManager.enableSensor(mGloveAddress, UDOOBLESensor.ACCELEROMETER, true);
//        udooBluManager.setNotificationPeriod(mGloveAddress, UDOOBLESensor.ACCELEROMETER);
//
//        udooBluManager.enableSensor(mGloveAddress, UDOOBLESensor.MAGNETOMETER, true);
//        udooBluManager.setNotificationPeriod(mGloveAddress, UDOOBLESensor.MAGNETOMETER);

        udooBluManager.enableSensor(mCarAddress, UDOOBLESensor.IOPIN, true);
        udooBluManager.setIoPinMode(mCarAddress, Constant.IOPIN.D6, Constant.IOPIN_TYPE.DIGITAL, Constant.IOPIN_MODE.INPUT);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    udooBluManager.digitalRead(mCarAddress, new OnCharacteristicsListener() {
                        @Override
                        public void onCharacteristicsRead(String uuidStr, byte[] value, int status) {
                            Log.i("onCharacteristicsRead: ", " " + value[0]);
                        }

                        @Override
                        public void onCharacteristicChanged(String uuidStr, byte[] rawValue) {

                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }}).start();


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
//                            ioPin[2] = Constant.IOPIN.A1;
//                            ioPin[3] = Constant.IOPIN.A2;
//                            value = Constant.IOPIN_VALUE.HIGH;
//                            udooBluManager.digitalWrite(mCarAddress, value, ioPin);
//                        }else{
//                            ioPin = new Constant.IOPIN[0];
//                            value = Constant.IOPIN_VALUE.LOW;
//                            udooBluManager.digitalWrite(mCarAddress, value, ioPin);
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
}
