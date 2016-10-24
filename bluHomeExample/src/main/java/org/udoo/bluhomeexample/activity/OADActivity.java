package org.udoo.bluhomeexample.activity;

import android.app.DownloadManager;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;

import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.adapter.OADAdapter;
import org.udoo.bluhomeexample.databinding.OadLayoutBinding;
import org.udoo.bluhomeexample.interfaces.IOADActivityView;
import org.udoo.bluhomeexample.model.OADModel;
import org.udoo.bluhomeexample.presenter.OADPresenter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by harlem88 on 20/10/16.
 */

public class OADActivity extends AppCompatActivity implements IOADActivityView{
    private OadLayoutBinding mViewBinding;
    private Uri image_uri = Uri.parse("http://www.androidtutorialpoint.com/wp-content/uploads/2016/09/Beauty.jpg");
    private DownloadManager downloadManager;
    private OADAdapter mOADAdapter;
    private OADPresenter mOADPresenter;
    private Handler mHandler;
    private static final String TAG = "OADActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = DataBindingUtil.setContentView(this, R.layout.oad_layout);
        mOADPresenter = new OADPresenter();
        mHandler = new Handler();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewBinding.listOadFirmware.setLayoutManager(new LinearLayoutManager(this));

        mOADAdapter = new OADAdapter(new ArrayList<OADModel>());
        mViewBinding.listOadFirmware.setAdapter(mOADAdapter);
        mOADPresenter.onStart(this);
    }

    private long DownloadData (Uri uri, View v) {

        long downloadReference;

        // Create request for android download manager
        downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle("Data Download");

        //Setting description of request
        request.setDescription("Android Data download using DownloadManager.");

        //Set the local destination for the downloaded file to a path within the application's external files directory
//        if(v.getId() == R.id.DownloadMusic)
//            request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS,"AndroidTutorialPoint.mp3");
//        else if(v.getId() == R.id.DownloadImage)
//            request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS,"AndroidTutorialPoint.jpg");
//
//        //Enqueue download and save into referenceId
//        downloadReference = downloadManager.enqueue(request);
//
//        Button DownloadStatus = (Button) findViewById(R.id.DownloadStatus);
//        DownloadStatus.setEnabled(true);
//        Button CancelDownload = (Button) findViewById(R.id.CancelDownload);
//        CancelDownload.setEnabled(true);

        return 0;
    }

    @Override
    public void addFirmwares(final List<OADModel> firmwares) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mOADAdapter.addOADFirmwares(firmwares);
            }
        });
    }

}
