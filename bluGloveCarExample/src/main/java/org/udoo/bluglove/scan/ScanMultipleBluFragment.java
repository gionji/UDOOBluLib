package org.udoo.bluglove.scan;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.udoo.bluglove.BluNeoGloveCarApplication;
import org.udoo.bluglove.MainActivity;
import org.udoo.bluglove.R;
import org.udoo.udooblulib.manager.UdooBluManager;
import org.udoo.udooblulib.model.BleItem;
import org.udoo.udooblulib.scan.BluScanCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by harlem88 on 02/04/16.
 */
public class ScanMultipleBluFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private BleItemAdapter mBleItemAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Map<String, BleItem> bleItemMap;
    private ProgressBar mProgressBar;
    private Button mBleScanRunStopBtn;
    private TextView mTvNoItem;
    private UdooBluManager udooBluManager;
    private List<String> mItemClicked;
    private IFragmentToActivity mIFragmentToActivity;
    private boolean mScan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        udooBluManager = ((BluNeoGloveCarApplication) getActivity().getApplication()).getBluManager();
        mIFragmentToActivity = (IFragmentToActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
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

        mBleItemAdapter.setItemCLickListner(new BleItemAdapter.ItemCLickListner() {
            @Override
            public void onItemClickListener(BleItem item) {
                mItemClicked.add(item.address);

                if (mItemClicked.size() > 0 && mIFragmentToActivity != null) {
                    mIFragmentToActivity.onTwoBluSelected(mItemClicked.get(0), "");
                }

            }
        });

        mBleScanRunStopBtn.setOnClickListener(runStopClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();


      udooBluManager.setIBluManagerCallback(new UdooBluManager.IBluManagerCallback() {
          @Override
          public void onBluManagerReady() {
              udooBluManager.scanLeDevice(true, scanCallback);
          }
      });

        mItemClicked = new ArrayList<>();
    }


    private BluScanCallBack scanCallback = new BluScanCallBack() {


        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            if (device != null && device.getAddress().startsWith("B0:B4:48")) {
                ParcelUuid parcelUuids[] = device.getUuids();
                mTvNoItem.setVisibility(View.GONE);
                if (!bleItemMap.containsKey(device.getAddress())) {
                    bleItemMap.put(device.getAddress(), BleItem.Builder(device, String.valueOf(result.getRssi())));
                    mBleItemAdapter.addDevice(bleItemMap.get(device.getAddress()));
                    mBleItemAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onScanFinished() {
            super.onScanFinished();
            mBleScanRunStopBtn.setText("START");
            mScan = false;
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            mScan = false;
            mBleScanRunStopBtn.setText("START");
            mProgressBar.setVisibility(View.GONE);
        }
    };

    private Button.OnClickListener runStopClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mScan){
                udooBluManager.scanLeDevice(false, scanCallback);
                mProgressBar.setVisibility(View.GONE);
                mBleScanRunStopBtn.setText("START");
                mScan = false;
            }else{
                udooBluManager.scanLeDevice(true, scanCallback);
                mProgressBar.setVisibility(View.VISIBLE);
                mBleScanRunStopBtn.setText("STOP");
                mScan = true;
            }
        }
    };

}
