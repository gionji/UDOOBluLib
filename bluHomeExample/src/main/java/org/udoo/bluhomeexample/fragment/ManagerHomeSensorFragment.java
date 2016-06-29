package org.udoo.bluhomeexample.fragment;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import org.udoo.udooblulib.exceptions.UdooBluException;
import org.udoo.udooblulib.interfaces.INotificationListener;
import org.udoo.udooblulib.manager.UdooBluManager;
import org.udoo.udooblulib.model.Barometer;
import org.udoo.udooblulib.model.Led;
import org.udoo.udooblulib.model.Temperature;
import org.udoo.udooblulib.model.XYZSensor;
import org.udoo.udooblulib.sensor.Constant;
import org.udoo.udooblulib.sensor.UDOOBLESensor;
import org.udoo.udooblulib.utils.Point3D;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;

/**
 * Created by harlem88 on 28/06/16.
 */

public class ManagerHomeSensorFragment extends Fragment {
    private RecyclerView mLedList;
    private RecyclerView.LayoutManager mIntSensorLayoutManager, mLedLayoutManager;

    private LedAdapter mLedAdapter;
    private XYZSensorSummaryAdapter mXyzSensorAdapter;

    private Led[] mLeds;
    private List<XYZSensor> mXyzIntSensors;
    private Barometer mBarometer = new Barometer();
    private Temperature mTemperature = new Temperature();
    private FragmentSensorHomeBinding mViewHomeBinding;
    private UdooBluManager mUdooBluManager;
    private static final String ADDRESS = "addr";
    private String mBluAddress;
    DecimalFormat df = new DecimalFormat("#.##");


    public static ManagerHomeSensorFragment Builder(String address){
        ManagerHomeSensorFragment managerHomeSensorFragment = new ManagerHomeSensorFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ADDRESS, address);
        managerHomeSensorFragment.setArguments(bundle);
        return managerHomeSensorFragment;
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

        ExternalSensorLayoutBinding externalSensorLayoutBinding = ExternalSensorLayoutBinding.bind(view.findViewById(R.id.external_view_root));
        externalSensorLayoutBinding.setBarometer(mBarometer);
        externalSensorLayoutBinding.setTemperature(mTemperature);

        mLedList.setAdapter(mLedAdapter);
        mViewHomeBinding.xyzSensorInternalList.setAdapter(mXyzSensorAdapter);

//        mIFragmentToActivity = (IFragmentToActivity) getActivity();
        mLedAdapter.setItemCLickListner(new LedAdapter.ItemCLickListner() {
            @Override
            public void onItemClickListener(int pos) {
                Led led = mLeds[pos];
                byte func = led.onoff.get() ? Constant.LED_OFF : Constant.LED_ON;
//                if (mIBleBrickOp != null && mIBleBrickOp.turnLed(pos + 1, func, 100))
//                    led.onoff.set(func == Constant.LED_ON);
            }

            @Override
            public void onBlinkListener(int pos, boolean blink) {
                Led led = mLeds[pos];
                byte func = led.blink.get() ? Constant.LED_OFF : Constant.BLINK_ON;
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

        if(savedInstanceState == null)
            mBluAddress = getArguments().getString(ADDRESS);
    }

    private Led[] getLedsDefault() {
        Led[] leds = new Led[3];
        Led led = Led.BuilderDefault();
        led.color.set(Color.GREEN);
        leds[0] = led;
        led = Led.BuilderDefault();
        led.color.set(Color.YELLOW);
        leds[1] = led;
        led = Led.BuilderDefault();
        led.color.set(Color.RED);
        leds[2] = led;

        return leds;
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
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
