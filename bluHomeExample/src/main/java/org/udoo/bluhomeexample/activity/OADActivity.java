package org.udoo.bluhomeexample.activity;

import android.app.DownloadManager;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.WindowManager;

import org.udoo.bluhomeexample.BluHomeApplication;
import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.adapter.OADAdapter;
import org.udoo.bluhomeexample.databinding.OadLayoutBinding;
import org.udoo.bluhomeexample.interfaces.IOADActivityView;
import org.udoo.bluhomeexample.model.BluItem;
import org.udoo.bluhomeexample.model.OADModel;
import org.udoo.bluhomeexample.presenter.OADPresenter;

import java.util.ArrayList;
import java.util.List;

import static org.udoo.bluhomeexample.activity.BluActivity.EXTRA_BLU_DEVICE;

/**
 * Created by harlem88 on 20/10/16.
 */

public class OADActivity extends AppCompatActivity implements IOADActivityView{
    private OadLayoutBinding mViewBinding;
    private Uri image_uri = Uri.parse("http://www.androidtutorialpoint.com/wp-content/uploads/2016/09/Beauty.jpg");
    private DownloadManager downloadManager;
    private OADAdapter mOADAdapter;
    private OADPresenter mOADPresenter;
    private Handler mUiHandler;
    private static final String TAG = "OADActivity";
    private BluItem mBluItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = DataBindingUtil.setContentView(this, R.layout.oad_layout);

        if (savedInstanceState == null) {
            mBluItem = getIntent().getParcelableExtra(EXTRA_BLU_DEVICE);
        }

        mOADPresenter = new OADPresenter(mBluItem.address);
        mUiHandler = new Handler();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewBinding.listOadFirmware.setLayoutManager(new LinearLayoutManager(this));

        mOADAdapter = new OADAdapter(new ArrayList<OADModel>());
        mViewBinding.listOadFirmware.setAdapter(mOADAdapter);
        mOADPresenter.onStart(this);

        setListener();
    }

    private void setListener() {
        mViewBinding.btnLoadFirmware.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOADPresenter.uploadFirmware(((BluHomeApplication) getApplication()));
            }
        });

        mOADAdapter.setOnOADFirmwareClickListener(new OADAdapter.OnOADFirmwareClickListener() {
            @Override
            public void onOADFirmwareClick(OADModel oadModel) {
                mOADPresenter.downloadOADFirmware(oadModel, getBaseContext());
            }
        });

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(mOADPresenter.mDownloadReceiver, filter);
    }

    @Override
    public void addFirmwares(final List<OADModel> firmwares) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                mOADAdapter.addOADFirmwares(firmwares);
            }
        });
    }

    @Override
    public void showProgress(final String text, final boolean indeterminate) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                mViewBinding.lOADContent.setClickable(false);
                mViewBinding.lOADContent.setEnabled(false);
                mViewBinding.textProgress.setText(text);
                mViewBinding.lOADContent.setAlpha(0.7f);
                mViewBinding.progressBarOad.setIndeterminate(indeterminate);
                mViewBinding.lOADProgress.setVisibility(View.VISIBLE);
                if(!indeterminate){
                    mViewBinding.progressBarOad.setMax(100);
                }
            }
        });

    }

    @Override
    public void updateProgress(final int value) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                mViewBinding.progressBarOad.setProgress(value);
            }
        });

    }

    @Override
    public void dismissProgress() {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                mViewBinding.lOADContent.setClickable(true);
                mViewBinding.lOADContent.setEnabled(true);
                mViewBinding.lOADContent.setAlpha(1f);
                mViewBinding.lOADProgress.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void enableOADUpload(final boolean enable) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                mViewBinding.btnLoadFirmware.setEnabled(enable);
            }
        });
    }

    @Override
    public void setOADInfo(final String name,final String version, final String data) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                mViewBinding.textOadSelected.setText(name + " " + version + " "+ data);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        unregisterReceiver(mOADPresenter.mDownloadReceiver);
    }
}
