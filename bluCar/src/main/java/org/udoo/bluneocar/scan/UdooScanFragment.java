package org.udoo.bluneocar.scan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.udoo.bluneocar.BluNeoCarApplication;
import org.udoo.bluneocar.R;
import org.udoo.bluneocar.interfaces.IFragmentToActivity;
import org.udoo.udooblulib.manager.UdooBluManager;
import org.udoo.udooblulib.model.BleItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by harlem88 on 16/02/16.
 */

public class UdooScanFragment extends Fragment {
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private RecyclerView mRecyclerView;
    private BleItemAdapter mBleItemAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Map<String, BleItem> bleItemMap;
    private IFragmentToActivity mIFragmentToActivity;
    private ProgressBar mProgressBar;
    private Button mBleScanRunStopBtn;
    private TextView mTvNoItem;
    private UdooBluManager mUooUdooBluManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUooUdooBluManager = ((BluNeoCarApplication) getActivity().getApplication()).getBluManager();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mIFragmentToActivity = (IFragmentToActivity) getActivity();
        return inflater.inflate(R.layout.fragment_ble_scan, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_ble_scan);
        mBleScanRunStopBtn = (Button) view.findViewById(R.id.button_run_stop_ble_scan);
        mTvNoItem = (TextView) view.findViewById(R.id.tv_no_items);

        mRecyclerView.setHasFixedSize(true);
        bleItemMap = new HashMap<>();
        mLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mBleItemAdapter = new BleItemAdapter(new ArrayList<BleItem>());
        mRecyclerView.setAdapter(mBleItemAdapter);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mHandler = new Handler(Looper.getMainLooper());
        mBleItemAdapter.setItemCLickListner(new BleItemAdapter.ItemCLickListner() {
            @Override
            public void onItemClickListener(BleItem item) {
                if (mIFragmentToActivity != null)
                    mIFragmentToActivity.onBleItemClick(item);
            }
        });

        mBleScanRunStopBtn.setOnClickListener(runStopClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        scanLeDevice(true);
    }

    private Button.OnClickListener runStopClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            scanLeDevice(!mScanning);
        }
    };


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mProgressBar.setVisibility(View.VISIBLE);
            mBleScanRunStopBtn.setText("STOP");
            mTvNoItem.setVisibility(View.VISIBLE);
            mBleItemAdapter.clear();
            mScanning = true;
        } else {
            mScanning = false;
            mBleScanRunStopBtn.setText("START");
            mProgressBar.setVisibility(View.GONE);
        }

        mUooUdooBluManager.scanLeDevice(enable).onBackpressureBuffer()
                .subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ScanResult>() {
            @Override
            public void onCompleted() {
                mBleScanRunStopBtn.setText("START");
                mProgressBar.setVisibility(View.GONE);
                mScanning = false;
            }

            @Override
            public void onError(Throwable e) {
                mScanning = false;
                mBleScanRunStopBtn.setText("START");
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onNext(ScanResult scanResult) {
                BluetoothDevice device = scanResult.getDevice();
                if (device != null && device.getAddress().startsWith("B0:B4:48")) {
                    mTvNoItem.setVisibility(View.GONE);
                    if (!bleItemMap.containsKey(device.getAddress())) {
                        bleItemMap.put(device.getAddress(), BleItem.Builder(device, String.valueOf(scanResult.getRssi())));
                        mBleItemAdapter.addDevice(bleItemMap.get(device.getAddress()));
                        mBleItemAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }

}
