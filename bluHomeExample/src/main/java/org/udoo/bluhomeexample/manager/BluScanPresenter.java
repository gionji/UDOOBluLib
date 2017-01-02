package org.udoo.bluhomeexample.manager;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import org.udoo.bluhomeexample.BluItemAdapter;
import org.udoo.bluhomeexample.dialog.BluSaveDialog;
import org.udoo.bluhomeexample.interfaces.IBluScanView;
import org.udoo.bluhomeexample.interfaces.OnResult;
import org.udoo.bluhomeexample.model.BluItem;
import org.udoo.bluhomeexample.model.Led;
import org.udoo.bluhomeexample.util.Util;
import org.udoo.udooblulib.exceptions.UdooBluException;
import org.udoo.udooblulib.interfaces.IBleDeviceListener;
import org.udoo.udooblulib.interfaces.IBluManagerCallback;
import org.udoo.udooblulib.interfaces.IReaderListener;
import org.udoo.udooblulib.manager.UdooBluManager;
import org.udoo.udooblulib.scan.BluScanCallBack;
import org.udoo.udooblulib.sensor.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by harlem88 on 09/10/16.
 */

public class BluScanPresenter implements SwipeRefreshLayout.OnRefreshListener, OnResult<BluItem> , BluItemAdapter.ItemCLickListener{
    private UdooBluManager mUdooBluManager;
    private Map<String, BluItem> bluItemMap;
    private boolean mScan;
    private Context mContext;
    private IBluScanView mIBluScanView;
    private BluSaveDialog mBluSaveDialog;
    private Map<String, AtomicBoolean> inTryConnections;

    public BluScanPresenter(UdooBluManager udooBluManager, Context context) {
        mUdooBluManager = udooBluManager;
        mContext = context;
        bluItemMap = new HashMap<>();
        inTryConnections = new HashMap<>();
    }

    public void onViewCreated(final IBluScanView iBluScanView) {
        mIBluScanView = iBluScanView;
        mUdooBluManager.setIBluManagerCallback(new IBluManagerCallback() {
            @Override
            public void onBluManagerReady() {
                onRefresh();
            }
        });
    }

    public void onResume(){}


    private BluScanCallBack scanCallback = new BluScanCallBack() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            String rssi = String.valueOf(result.getRssi());
            if (device != null) {
                if (!bluItemMap.containsKey(device.getAddress())) {
                    BluItem bluItem = BluItem.Builder(device, rssi);
                    bluItem.setFound(true);
                    bluItemMap.put(device.getAddress(), bluItem);

                    if (mIBluScanView != null)
                        mIBluScanView.addDevice(bluItem);

                } else {
                    BluItem bluItem = bluItemMap.get(device.getAddress());
                    if (!bluItem.isFound() || !bluItem.rssi.equalsIgnoreCase(rssi)) {

                        if (!bluItem.isFound())
                            bluItem.setFound(true);

                        if (!bluItem.rssi.equalsIgnoreCase(rssi))
                            bluItem.rssi = rssi;

                        if (mIBluScanView != null)
                            mIBluScanView.updateDevice(bluItem);
                    }
                }
            }
        }

        @Override
        public void onScanFinished() {
            mScan = false;
            if (mIBluScanView != null)
                mIBluScanView.setRefresh(false);

            tryConnect();
        }

        @Override
        public void onError(UdooBluException runtimeException) {
            if (mIBluScanView != null)
                mIBluScanView.onError(runtimeException);

            mScan = false;
            if (mIBluScanView != null)
                mIBluScanView.setRefresh(false);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            mScan = false;

            if (mIBluScanView != null)
                mIBluScanView.setRefresh(false);
        }
    };

    @Override
    public void onRefresh() {
        if (mScan) {
            mUdooBluManager.scanLeDevice(false, scanCallback);

            if (mIBluScanView != null)
                mIBluScanView.setRefresh(false);
            mScan = false;
        } else {
            if (bluItemMap.size() > 0) {
                Set<String> keys = bluItemMap.keySet();
                for (String key : keys) {
                    bluItemMap.get(key).setFound(false);
                }
                if (mIBluScanView != null)
                    mIBluScanView.addDevices(new ArrayList<>(bluItemMap.values()));

                mUdooBluManager.scanLeDevice(true, scanCallback);
            } else {
                final Handler handler = new Handler();
                Util.LoadBlusFromPreferences(mContext, new OnResult<Map<String, BluItem>>() {
                    @Override
                    public void onSuccess(final Map<String, BluItem> bluMap) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                bluItemMap = bluMap;

                                if (bluItemMap.size() > 0 && mIBluScanView != null) {
                                    mIBluScanView.addDevices(new ArrayList<>(bluItemMap.values()));
                                }
                                mUdooBluManager.scanLeDevice(true, scanCallback);
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable throwable) {
                    }
                });
            }

            if (mIBluScanView != null)
                mIBluScanView.setRefresh(true);

            mScan = true;
        }
    }

    @Override
    public void onSuccess(BluItem saveItemDialog) {
        BluItem item = bluItemMap.get(saveItemDialog.address);
        item.color = saveItemDialog.color;
        item.name = saveItemDialog.name;
        item.paired = true;
        bluItemMap.put(item.address, item);
        if (mIBluScanView != null)
            mIBluScanView.updateDevice(item);

        Util.SaveBlusInPreferences(mContext, new ArrayList<>(bluItemMap.values()));
    }

    @Override
    public void onError(Throwable throwable) {
    }

    @Override
    public void onItemClickListener(BluItem item) {
        BluItem itemMap = bluItemMap.get(item.address);
        if (!itemMap.paired) {
            mBluSaveDialog = BluSaveDialog.Builder(item.address);
            mBluSaveDialog.setResultCallback(this);

            if (mIBluScanView != null)
                mIBluScanView.showDialog(mBluSaveDialog);
        }else{
            if (item.isConnected() && mIBluScanView != null)
                mIBluScanView.onConnectPage(item);
        }
    }

    @Override
    public void onItemLedClickListener(BluItem bluItem, Led ledControl) {
        if(ledControl != null){
            mUdooBluManager.blinkLed(bluItem.address, ledControl.led, ledControl.blink.get());
        }
    }

    public void tryConnect() {

        Handler handler = new Handler();
        List<String> itemsPaired = new LinkedList<>();
        Set<String> keys = bluItemMap.keySet();
        for (String key : keys) {
            if (bluItemMap.get(key).isFound() && bluItemMap.get(key).paired)
                itemsPaired.add(key);
        }

        for (int i = 0; i < itemsPaired.size(); i++) {
            final String address = itemsPaired.get(i);
            inTryConnections.put(address, new AtomicBoolean(true));
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mUdooBluManager.connect(address, new IBleDeviceListener() {
                        @Override
                        public void onDeviceConnected() {
                            BluItem bluItem = bluItemMap.get(address);
                            bluItem.setConnected(true);
                            bluItemMap.put(bluItem.address, bluItem);

                            if (mIBluScanView != null)
                                mIBluScanView.updateDevice(bluItem);
                        }

                        @Override
                        public void onDeviceDisconnect() {
                            BluItem bluItem = bluItemMap.get(address);
                            bluItem.setConnected(true);
                            bluItemMap.put(bluItem.address, bluItem);

                            if (mIBluScanView != null)
                                mIBluScanView.updateDevice(bluItem);
                        }

                        @Override
                        public void onError(UdooBluException runtimeException) {

                        }
                    });

                }
            }, i * 500);
        }
    }
}
