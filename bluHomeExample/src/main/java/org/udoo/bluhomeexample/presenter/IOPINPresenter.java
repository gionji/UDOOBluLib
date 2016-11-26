package org.udoo.bluhomeexample.presenter;

import android.util.Log;

import org.udoo.bluhomeexample.BluHomeApplication;
import org.udoo.bluhomeexample.adapter.IOPinAdapter;
import org.udoo.bluhomeexample.interfaces.IIOPinView;
import org.udoo.bluhomeexample.interfaces.OnResult;
import org.udoo.udooblulib.exceptions.UdooBluException;
import org.udoo.udooblulib.interfaces.INotificationListener;
import org.udoo.udooblulib.interfaces.OnBluOperationResult;
import org.udoo.udooblulib.manager.UdooBluManager;
import org.udoo.udooblulib.model.IOPin;
import org.udoo.udooblulib.sensor.UDOOBLESensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by harlem88 on 23/11/16.
 */

public class IOPINPresenter implements OnResult<IOPin>, IOPinAdapter.IIOPinValueCallback{
    private UdooBluManager mUdooBluManager;
    private String mAddress;
    private IIOPinView mIioPinView;
    private static final String TAG = "IOPINPresenter";
    private Map<String, IOPin> mIOPinMap;
    private ArrayList<IOPin> mPinsAnalog;
    private ArrayList<IOPin> mPinsDigital;
    private short mAnalogPos;


    public IOPINPresenter(String address, BluHomeApplication bluHomeApplication, IIOPinView iIOPinView){
        mAddress = address;
        mUdooBluManager = bluHomeApplication.getBluManager();
        mIioPinView = iIOPinView;
        mIOPinMap = new HashMap<>();
        mPinsAnalog = new ArrayList<>();
        mPinsDigital = new ArrayList<>();
    }

    @Override
    public void onSuccess(final IOPin pin) {
        if(mIioPinView != null)
            mIioPinView.showProgress(true);

        if(mUdooBluManager!= null)
            mUdooBluManager.setIoPinMode(mAddress, new OnBluOperationResult<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    if(mIioPinView != null){
                        mIioPinView.addIOPin(pin);
                        addPin(pin);
                        mIioPinView.showProgress(false);
                    }
                }

                @Override
                public void onError(UdooBluException runtimeException) {
                    if(mIioPinView != null){
                        mIioPinView.showProgress(false);
                    }

                    //TODO error
                    Log.e(TAG, "onError: "+ runtimeException.getReason());
                }
            }, pin);
    }

    @Override
    public void onError(Throwable throwable) {
        if(mIioPinView != null){
            mIioPinView.showProgress(false);
        }

        //TODO error
        Log.e(TAG, "onError: "+ throwable.getMessage());
    }

    private void addPin(final IOPin pin) {
        mIOPinMap.put(pin.pin.name(), pin);

        if (pin.mode.compareTo(IOPin.IOPIN_MODE.DIGITAL_INPUT) == 0) {
            if (mPinsDigital.size() == 0) {
                mUdooBluManager.subscribeNotificationDigital(mAddress, digitalListener);
            }
            mPinsDigital.add(pin);
        } else if (pin.mode.compareTo(IOPin.IOPIN_MODE.ANALOG) == 0) {
            mPinsAnalog.add(pin);
            if (mPinsAnalog.size() == 1) {
                mUdooBluManager.subscribeNotificationAnalog(mAddress, pin.pin, analogListener, 1000);
            }
        }
    }


    private INotificationListener<byte[]> analogListener =  new INotificationListener<byte[]>() {
        @Override
        public void onNext(byte[] value) {

            IOPin ioPin = mPinsAnalog.get(mAnalogPos);
            ioPin.analogValue = UDOOBLESensor.IOPIN_ANALOG.convertADC(value);

            Log.i(TAG, "onNext: analogListener" + ioPin.analogValue);
            if(mIioPinView != null)
                mIioPinView.updateIOPinAnalog(ioPin);

            if (mPinsAnalog.size() > 1) {
                resetAnalogIndex();
            }
        }

        @Override
        public void onError(UdooBluException runtimeException) {

        }
    };


    private INotificationListener<byte[]> digitalListener =  new INotificationListener<byte[]>() {
        @Override
        public void onNext(byte[] value) {
            IOPin[] pins = new IOPin[mPinsDigital.size()];
            pins = mPinsDigital.toArray(pins);
            boolean iopins [] = UDOOBLESensor.IOPIN_DIGITAL.convertIOPinDigital(value, pins);
            Log.i(TAG, "onNext: digitalListener" + iopins);
            for (int i = 0; i < pins.length; i++) {
                if (pins[i].digitalValue != (iopins[i] ? IOPin.IOPIN_DIGITAL_VALUE.HIGH : IOPin.IOPIN_DIGITAL_VALUE.LOW)) {
                    pins[i].digitalValue = iopins[i] ? IOPin.IOPIN_DIGITAL_VALUE.HIGH : IOPin.IOPIN_DIGITAL_VALUE.LOW;
                    if (mIioPinView != null)
                        mIioPinView.updateIOPinDigital(pins[i]);
                }
            }
        }

        @Override
        public void onError(UdooBluException runtimeException) {

        }
    };

    @Override
    public void onDigitalOutputPinValueListener(IOPin.IOPIN_PIN pin, IOPin.IOPIN_DIGITAL_VALUE value) {
        Log.i(TAG, "onDigitalOutputPinValueListener: " + pin +" " + value);

        mUdooBluManager.writeDigital(mAddress, new OnBluOperationResult<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {}

            @Override
            public void onError(UdooBluException runtimeException) {}
        }, IOPin.Builder(pin, value));
    }

    @Override
    public void onPwmPinListener(IOPin.IOPIN_PIN pin, int freq, int duty) {
        Log.i(TAG, "onPwmPinListener: " + pin +" " + freq + " " + duty);

        mUdooBluManager.writePwm(mAddress, pin, freq, duty, new OnBluOperationResult<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                resetAnalogIndex();
            }

            @Override
            public void onError(UdooBluException runtimeException) {}
        });
    }

    private void resetAnalogIndex(){
        mAnalogPos = mAnalogPos == mPinsAnalog.size() ? 0 : mAnalogPos++;
        mUdooBluManager.setPinAnalogPwmIndex(mAddress, mPinsAnalog.get(mAnalogPos), new OnBluOperationResult<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
            }

            @Override
            public void onError(UdooBluException runtimeException) {

            }
        });
    }

}
