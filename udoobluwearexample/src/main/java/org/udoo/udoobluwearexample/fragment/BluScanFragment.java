package org.udoo.udoobluwearexample.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.udoo.udooblulib.exceptions.UdooBluException;
import org.udoo.udooblulib.interfaces.IBluManagerCallback;
import org.udoo.udooblulib.manager.UdooBluManagerImpl;
import org.udoo.udooblulib.scan.BluScanCallBack;
import org.udoo.udoobluwearexample.BluWearApplication;
import org.udoo.udoobluwearexample.R;
import org.udoo.udoobluwearexample.adapter.BluItemAdapter;
import org.udoo.udoobluwearexample.databinding.FragmentScanLayoutBinding;
import org.udoo.udoobluwearexample.model.BluItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by harlem88 on 05/03/16.
 */

public class BluScanFragment extends Fragment {
    private FragmentScanLayoutBinding mViewBinding;
    private BluViewModel mBluViewModel;
    private Map<String, BluItem> bleItemMap;

    private UdooBluManagerImpl udooBluManager;
    private IFragmentToActivity mIFragmentToActivity;
    private boolean mScan;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        udooBluManager = ((BluWearApplication) getActivity().getApplication()).getBluManager();
        bleItemMap = new HashMap<>();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mIFragmentToActivity =(IFragmentToActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scan_layout, container, false);
        return mViewBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BluItemAdapter bleItemAdapter = new BluItemAdapter(getActivity(), new ArrayList<BluItem>());
        mBluViewModel = new BluViewModel(mViewBinding, (IFragmentToActivity) getActivity(), bleItemAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        startScan();
    }


    private void startScan(){
        udooBluManager.setIBluManagerCallback(new IBluManagerCallback() {
            @Override
            public void onBluManagerReady() {
                udooBluManager.scanLeDevice(true, scanCallback);
                mViewBinding.btnSearch.setVisibility(View.GONE);
                mViewBinding.progressScan.setVisibility(View.VISIBLE);
            }
        });
    }

    public static class BluViewModel {
        private FragmentScanLayoutBinding mViewBinding;
        private IFragmentToActivity mIFragmentToActivity;
        private BluItemAdapter mBluItemAdapter;


        public BluViewModel(FragmentScanLayoutBinding viewBinding, IFragmentToActivity iFragmentToActivity, BluItemAdapter bluItemAdapter) {
            mViewBinding = viewBinding;
            mViewBinding.setViewModel(this);
            mIFragmentToActivity = iFragmentToActivity;
            mBluItemAdapter = bluItemAdapter;
            mViewBinding.wearableList.setAdapter(mBluItemAdapter);
            mViewBinding.wearableList.setClickListener(onClickList());
        }


        public void addItem(BluItem bluItem){
            mBluItemAdapter.addItem(bluItem);
            mBluItemAdapter.notifyDataSetChanged();
        }


        public WearableListView.ClickListener onClickList() {
            return new WearableListView.ClickListener() {
                @Override
                public void onClick(WearableListView.ViewHolder viewHolder) {
                    Integer tag = (Integer) viewHolder.itemView.getTag();
                    BluItem bluItem = mBluItemAdapter.getItem(tag);
                    if(bluItem != null && mIFragmentToActivity != null){
                        mIFragmentToActivity.onConnect(bluItem);
                    }

                    mViewBinding.progressScan.setVisibility(View.GONE);
                }

                @Override
                public void onTopEmptyRegionClick() {

                }
            };
        }

        public Button.OnClickListener onSearchClick() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewBinding.btnSearch.setVisibility(View.GONE);
                    mViewBinding.progressScan.setVisibility(View.VISIBLE);
                }
            };
        }
    }

    private BluScanCallBack scanCallback = new BluScanCallBack() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            if (device != null) {
                Log.i("Blu", "onScanResult: " + device.getAddress());
//                mViewBinding.tvNoItems.setVisibility(View.GONE);
                if (!bleItemMap.containsKey(device.getAddress())) {
                    BluItem bluItem = BluItem.Builder(device, String.valueOf(result.getRssi()));
                    bleItemMap.put(device.getAddress(), bluItem);
                    mBluViewModel.addItem(bluItem);
                }
            }
        }

        @Override
        public void onScanFinished() {
            mScan = false;
            mViewBinding.btnSearch.setVisibility(View.VISIBLE);
            mViewBinding.progressScan.setVisibility(View.GONE);
            Log.i("bul", "onScanFinished: ");
        }

        @Override
        public void onError(UdooBluException runtimeException) {
            if(mIFragmentToActivity != null)
                mIFragmentToActivity.onBluError(runtimeException);

            mScan = false;
            mViewBinding.btnSearch.setVisibility(View.VISIBLE);
            mViewBinding.progressScan.setVisibility(View.GONE);


        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            mScan = false;
            mViewBinding.btnSearch.setVisibility(View.VISIBLE);
            mViewBinding.progressScan.setVisibility(View.GONE);
        }
    };
}
