package org.udoo.bluhomeexample.presenter;

import android.app.DownloadManager;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import org.json.JSONException;
import org.udoo.bluhomeexample.BluHomeApplication;
import org.udoo.bluhomeexample.interfaces.IOADActivityView;
import org.udoo.bluhomeexample.interfaces.OnResult;
import org.udoo.bluhomeexample.manager.OADManager;
import org.udoo.bluhomeexample.manager.RequestManager;
import org.udoo.bluhomeexample.manager.TIOADManager;
import org.udoo.bluhomeexample.model.OADModel;
import org.udoo.udooblulib.manager.UdooBluManager;
import org.udoo.udooblulib.manager.UdooBluManagerImpl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by harlem88 on 21/10/16.
 */

public class OADPresenter implements TIOADManager.IOADEvents{
    private List<OADModel> firmwares;
    private IOADActivityView mIoadActivityView;
    private TIOADManager mOadManager;
    private String mAddress;
    private OADModel mOADFirmware;
    private long mDowloadReference;
    private DownloadManager mDownloadManager;
    private ParcelFileDescriptor mOADFileDescriptor;

    public OADPresenter(String address){
        firmwares = new ArrayList<>();
        mAddress = address;
    }

    public void onStart(IOADActivityView ioadActivityView){
        mIoadActivityView = ioadActivityView;
        if(firmwares == null || firmwares.size() == 0){
            if(mIoadActivityView != null){
                mIoadActivityView.showProgress("Download firmwares", false);
                mIoadActivityView.enableOADUpload(false);
            }

            RequestManager.GetAODFirmwares(new OnResult<List<OADModel>>() {
                @Override
                public void onSuccess(List<OADModel> o) {
                    firmwares.clear();
                    firmwares.addAll(o);
                    if(mIoadActivityView != null){
                        mIoadActivityView.dismissProgress();
                        mIoadActivityView.addFirmwares(firmwares);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    //TODO
                    if(mIoadActivityView != null){
                        mIoadActivityView.dismissProgress();
                        Log.e("onError: OAD", throwable.getMessage());
                    }
                }
            });
        }
    }

    public void setOADInfo(ParcelFileDescriptor file){
        mOADFileDescriptor = file;
        if(mIoadActivityView != null){
            mIoadActivityView.enableOADUpload(true);
            mIoadActivityView.setOADInfo(mOADFirmware.name, mOADFirmware.version, mOADFirmware.data);
            mIoadActivityView.dismissProgress();
        }
    }

    public void uploadFirmware(BluHomeApplication bluHomeApplication){
        if(mIoadActivityView != null){
            mIoadActivityView.showProgress("Upload Firmware", false);
        }
        mOadManager = new TIOADManager(mAddress, mOADFileDescriptor, bluHomeApplication.getBluManager(), bluHomeApplication.getBaseContext(), this);
        mOadManager.start();
    }

    @Override
    public void onLoadProgress(int value) {
        if(mIoadActivityView != null)
            mIoadActivityView.updateProgress(value);
    }

    @Override
    public void onLoadCompleted() {
        if(mIoadActivityView != null){
            mIoadActivityView.showProgress("Upload Firmware",true);
        }
    }

    @Override
    public void onOADProcessCompleted() {
        if(mIoadActivityView != null){
            mIoadActivityView.showProgress("Upload Firmware Success",false);
            mIoadActivityView.updateProgress(100);
        }
    }

    @Override
    public void onLoadError(int state) {
        if(mIoadActivityView != null)
            mIoadActivityView.dismissProgress();
    }

    public void downloadOADFirmware(OADModel oADFirmware, Context context) {
        this.mOADFirmware = oADFirmware;
        if(mIoadActivityView != null){
            mIoadActivityView.showProgress("", true);
        }
        if (mDownloadManager == null)
            mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if(mOADFirmware.url != null && mOADFirmware.url.length() > 0)
            mDowloadReference = RequestManager.DownloadOadFirmaware(oADFirmware.name, oADFirmware.url,mDownloadManager, context);
        else{
            //TODO error
        }

    }

    public BroadcastReceiver mDownloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if(mDowloadReference == referenceId){


                DownloadManager.Query myDownloadQuery = new DownloadManager.Query();
                //set the query filter to our previously Enqueued download
                myDownloadQuery.setFilterById(mDowloadReference);

                //Query the download manager about downloads that have been requested.
                Cursor cursor = mDownloadManager.query(myDownloadQuery);
                if(cursor.moveToFirst()){
                    checkStatus(cursor);
                }

//                Button cancelDownload = (Button) findViewById(R.id.cancelDownload);
//                cancelDownload.setEnabled(false);

            }
        }
    };

    private void checkStatus(Cursor cursor){
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);
        int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
        String filename = cursor.getString(filenameIndex);

        switch(status){
            case DownloadManager.STATUS_FAILED:
//                statusText = "STATUS_FAILED";
            case DownloadManager.STATUS_PAUSED:
//                statusText = "STATUS_PAUSED";
                break;
            case DownloadManager.STATUS_PENDING:
//                statusText = "STATUS_PENDING";
                break;
            case DownloadManager.STATUS_RUNNING:
//                statusText = "STATUS_RUNNING";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
//                statusText = "STATUS_SUCCESSFUL";
//                reasonText = "Filename:\n" + filename;
                try {
                    ParcelFileDescriptor file = mDownloadManager.openDownloadedFile(mDowloadReference);
                    setOADInfo(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
