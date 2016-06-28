package org.udoo.bluhomeexample;

import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.udoo.bluhomeexample.databinding.FragmentBleScanBinding;
import org.udoo.bluhomeexample.interfaces.IFragmentToActivity;
import org.udoo.udooblulib.exceptions.UdooBluException;
import org.udoo.udooblulib.manager.UdooBluManagerImpl;
import org.udoo.udooblulib.model.BluItem;
import org.udoo.udooblulib.scan.BluScanCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by harlem88 on 02/04/16.
 */
public class ScanBluFragment extends Fragment {

    private BleItemAdapter mBleItemAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Map<String, BluItem> bleItemMap;

    private UdooBluManagerImpl udooBluManager;
    private List<String> mItemClicked;
    private IFragmentToActivity mIFragmentToActivity;
    private boolean mScan;
    private FragmentBleScanBinding mViewBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        udooBluManager = ((BluHomeApplication) getActivity().getApplication()).getBluManager();
        mIFragmentToActivity = (IFragmentToActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_ble_scan, container, false);
        return mViewBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewBinding.list.setHasFixedSize(true);
        bleItemMap = new HashMap<>();
        mLayoutManager = new GridLayoutManager(getActivity(), 3);
        mViewBinding.list.setLayoutManager(mLayoutManager);
        mBleItemAdapter = new BleItemAdapter(new ArrayList<BluItem>());
        mViewBinding.list.setAdapter(mBleItemAdapter);

        mBleItemAdapter.setItemCLickListner(new BleItemAdapter.ItemCLickListner() {
            @Override
            public void onItemClickListener(BluItem item) {
                if(mIFragmentToActivity != null)
                    mIFragmentToActivity.onConnect(item);
            }
        });
        mViewBinding.buttonRunStopBleScan.setOnClickListener(runStopClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        udooBluManager.setIBluManagerCallback(new UdooBluManagerImpl.IBluManagerCallback() {
            @Override
            public void onBluManagerReady() {
                udooBluManager.scanLeDevice(true, scanCallback);
                mViewBinding.progressBleScan.setVisibility(View.VISIBLE);
                mViewBinding.buttonRunStopBleScan.setText("STOP");
            }
        });
        mItemClicked = new ArrayList<>();
    }

    private BluScanCallBack scanCallback = new BluScanCallBack() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            if (device != null) {
                ParcelUuid parcelUuids[] = device.getUuids();
                mViewBinding.tvNoItems.setVisibility(View.GONE);
                if (!bleItemMap.containsKey(device.getAddress())) {
                    bleItemMap.put(device.getAddress(), BluItem.Builder(device, String.valueOf(result.getRssi())));
                    mBleItemAdapter.addDevice(bleItemMap.get(device.getAddress()));
                    mBleItemAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onScanFinished() {
            mViewBinding.buttonRunStopBleScan.setText("START");
            mScan = false;
            mViewBinding.progressBleScan.setVisibility(View.GONE);
        }

        @Override
        public void onError(UdooBluException runtimeException) {
            if(mIFragmentToActivity != null)
                mIFragmentToActivity.onBluError(runtimeException);

            mViewBinding.buttonRunStopBleScan.setText("START");
            mScan = false;
            mViewBinding.progressBleScan.setVisibility(View.GONE);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            mScan = false;
            mViewBinding.buttonRunStopBleScan.setText("START");
            mViewBinding.progressBleScan.setVisibility(View.GONE);
        }
    };

    private Button.OnClickListener runStopClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mScan) {
                udooBluManager.scanLeDevice(false, scanCallback);
                mViewBinding.progressBleScan.setVisibility(View.GONE);
                mViewBinding.buttonRunStopBleScan.setText("START");
                mScan = false;
            } else {
                udooBluManager.scanLeDevice(true, scanCallback);
                mViewBinding.progressBleScan.setVisibility(View.VISIBLE);
                mViewBinding.buttonRunStopBleScan.setText("STOP");
                mScan = true;
            }
        }
    };

}
