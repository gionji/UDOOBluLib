package org.udoo.bluhomeexample.fragment;

/**
 * Created by harlem88 on 29/06/16.
 */


import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.databinding.XyzLayoutBinding;
import org.udoo.udooblulib.exceptions.UdooBluException;
import org.udoo.udooblulib.interfaces.INotificationListener;
import org.udoo.udooblulib.interfaces.OnBluOperationResult;
import org.udoo.udooblulib.sensor.UDOOBLESensor;
import org.udoo.udooblulib.utils.Point3D;

/**
 * Created by harlem88 on 11/02/16.
 */
public class GyroscopeFragment extends UdooFragment {
    private XyzLayoutBinding mViewBinding;
    private int count;
    private static final String TAG = "GyroscopeFragment";

    public static UdooFragment Builder(String address){
        return Builder(new GyroscopeFragment(), address);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mViewBinding = DataBindingUtil.inflate(inflater, R.layout.xyz_layout, container, false);
        return mViewBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public void onConnect() {
        super.onConnect();
    }


    private void init() {
        mViewBinding.chart.setBackgroundColor(Color.LTGRAY);
        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        // add empty data
        mViewBinding.chart.setData(data);

        XAxis xl = mViewBinding.chart.getXAxis();

        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setSpaceBetweenLabels(5);
        xl.setEnabled(true);

        YAxis leftAxis = mViewBinding.chart.getAxisLeft();

        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaxValue(40f);
        leftAxis.setAxisMinValue(-40f);
        leftAxis.setStartAtZero(false);
        leftAxis.setDrawGridLines(true);
        YAxis rightAxis = mViewBinding.chart.getAxisRight();
        rightAxis.setEnabled(false);

    }

    private LineDataSet createSetX() {

        LineDataSet set1 = new LineDataSet(null, "x");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(Color.GREEN);
        set1.setCircleColor(Color.WHITE);
        set1.setLineWidth(2f);
        set1.setCircleRadius(3f);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);
        return set1;
    }


    private LineDataSet createSetY() {

        LineDataSet set2 = new LineDataSet(null, "y");
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);
        set2.setColor(Color.RED);
        set2.setCircleColor(Color.WHITE);
        set2.setLineWidth(2f);
        set2.setCircleRadius(3f);
        set2.setFillAlpha(65);
        set2.setFillColor(Color.RED);
        set2.setDrawCircleHole(false);
        set2.setHighLightColor(Color.rgb(244, 117, 117));
        return set2;
    }

    private LineDataSet createSetZ() {

        LineDataSet set = new LineDataSet(null, "z");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    private void setData(float x, float y, float z) {

        LineData data = mViewBinding.chart.getData();
        if (data != null) {

            data.addXValue(count + "");
            ILineDataSet set1 = data.getDataSetByIndex(0);

            if (set1 == null) {
                set1 = createSetX();
                data.addDataSet(set1);
            }

            ILineDataSet set2 = data.getDataSetByIndex(1);

            if (set2 == null) {
                set2 = createSetY();
                data.addDataSet(set2);
            }

            ILineDataSet set3 = data.getDataSetByIndex(2);

            if (set3 == null) {
                set3 = createSetZ();
                data.addDataSet(set3);
            }

            set1.addEntry(new Entry(x, count));
            set2.addEntry(new Entry(y, count));
            set3.addEntry(new Entry(z, count));
            count++;

            data.setValueTextColor(Color.BLACK);
            data.setValueTextSize(9f);

            mViewBinding.chart.notifyDataSetChanged();

            mViewBinding.chart.setVisibleXRangeMaximum(20);

            mViewBinding.chart.moveViewToX(data.getXValCount() - 21);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mUdooBluManager.subscribeNotificationGyroscope(mBluAddress, new INotificationListener<byte[]>() {
            @Override
            public void onNext(byte[] rawValue) {
                Point3D v = UDOOBLESensor.GYROSCOPE.convert(rawValue);
                setData((float) v.x, (float) v.y, (float) v.z);
            }

            @Override
            public void onError(UdooBluException runtimeException) {
                Log.e(TAG, "onError: " + runtimeException.getReason());
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mUdooBluManager.unSubscribeNotificationGyroscope(mBluAddress, new OnBluOperationResult<Boolean>() {
            public void onSuccess(Boolean aBoolean) {
                Log.i(TAG, "onSuccess unsub" + aBoolean);
            }

            @Override
            public void onError(UdooBluException runtimeException) {
                Log.e(TAG, "onError unsub" + runtimeException.getReason());
            }
        });
    }
}