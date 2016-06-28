package org.udoo.bluhomeexample.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.adapter.LedAdapter;
import org.udoo.bluhomeexample.adapter.XYZSensorSummaryAdapter;
import org.udoo.bluhomeexample.databinding.FragmentSensorHomeBinding;
import org.udoo.udooblulib.model.Barometer;
import org.udoo.udooblulib.model.Led;
import org.udoo.udooblulib.model.Temperature;
import org.udoo.udooblulib.model.XYZSensor;

import java.util.List;

/**
 * Created by harlem88 on 28/06/16.
 */

public class ManagerHomeSensorFragment extends Fragment {
    private RecyclerView mSensorIntList, mLedList;
    private RecyclerView.LayoutManager mIntSensorLayoutManager, mLedLayoutManager;

    private LedAdapter mLedAdapter;
    private XYZSensorSummaryAdapter mXyzSensorAdapter;

    private Led[] mLeds;
    private List<XYZSensor> mXyzIntSensors;
    private Barometer mBarometer = new Barometer();
    private Temperature mTemperature = new Temperature();
    private FragmentSensorHomeBinding mViewHomeBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mViewHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sensor_home, container, false);
        return mViewHomeBinding.getRoot();
    }



}
