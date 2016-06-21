package org.udoo.bluglove;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.udoo.udooblulib.exceptions.UdooBluException;
import org.udoo.udooblulib.interfaces.IReaderListener;
import org.udoo.udooblulib.interfaces.OnBluOperationResult;
import org.udoo.udooblulib.manager.UdooBluManagerImpl;
import org.udoo.udooblulib.model.IOPin;
import org.udoo.udooblulib.sensor.Constant;
import org.udoo.udooblulib.sensor.UDOOBLE;
import org.udoo.udooblulib.sensor.UDOOBLESensor;
import org.udoo.udooblulib.utils.Point3D;

import java.util.Timer;
import java.util.TimerTask;

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


        Timer job = new Timer();
        job.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                final IOPin[] iopins = new IOPin[8];
                iopins[0] = IOPin.Builder(IOPin.IOPIN_PIN.A0, IOPin.IOPIN_MODE.DIGITAL_INPUT);
                iopins[1] = IOPin.Builder(IOPin.IOPIN_PIN.A1, IOPin.IOPIN_MODE.DIGITAL_INPUT);
                iopins[2] = IOPin.Builder(IOPin.IOPIN_PIN.A2, IOPin.IOPIN_MODE.DIGITAL_INPUT);
                iopins[3] = IOPin.Builder(IOPin.IOPIN_PIN.A3, IOPin.IOPIN_MODE.DIGITAL_INPUT);
                iopins[4] = IOPin.Builder(IOPin.IOPIN_PIN.A4, IOPin.IOPIN_MODE.DIGITAL_INPUT);
                iopins[5] = IOPin.Builder(IOPin.IOPIN_PIN.A5, IOPin.IOPIN_MODE.DIGITAL_INPUT);
                iopins[6] = IOPin.Builder(IOPin.IOPIN_PIN.D6, IOPin.IOPIN_MODE.DIGITAL_INPUT);
                iopins[7] = IOPin.Builder(IOPin.IOPIN_PIN.D7, IOPin.IOPIN_MODE.DIGITAL_INPUT);
                udooBluManager.readDigital(mCarAddress, new IReaderListener<byte[]>() {
                    @Override
                    public void oRead(byte[] value) {
                        boolean [] values = UDOOBLESensor.IOPINDIGITAL.convertIOPinDigital(value, iopins);
                        for(int i = 0; i < values.length; i++){
                            Log.i(TAG, "oRead: " + i + ": "+values[i]);
                        }
                    }

                    @Override
                    public void onError(UdooBluException runtimeException) {
                        Log.e(TAG, "onError: " + runtimeException.getReason());
                    }
                }, iopins);


            }}, 2000, 2000);


//        udooBluManager.digitalWrite(mCarAddress, Constant.IOPIN_VALUE.HIGH, Constant.IOPIN.D6);
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
