package org.udoo.udoobluwearexample;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.DotsPageIndicator;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.udoo.udooblulib.exceptions.UdooBluException;
import org.udoo.udooblulib.interfaces.IBleDeviceListener;
import org.udoo.udooblulib.interfaces.IBluManagerCallback;
import org.udoo.udooblulib.manager.UdooBluManager;
import org.udoo.udooblulib.manager.UdooBluManagerImpl;
import org.udoo.udoobluwearexample.adapter.VerticalGridPagerAdapter;
import org.udoo.udoobluwearexample.databinding.ActivityMainBinding;
import org.udoo.udoobluwearexample.fragment.BluScanFragment;
import org.udoo.udoobluwearexample.fragment.IFragmentToActivity;
import org.udoo.udoobluwearexample.model.BluItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends WearableActivity implements IFragmentToActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private boolean mBleSupported = true;
    private static BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBtAdapter = null;
    private final String TAG = "MainActivity";
    private ActivityMainBinding mViewBinding;
    private Handler mHandler;
    private Fragment mCurrentFragment;
    private UdooBluManager mUdooBluManager;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setAmbientEnabled();

        mHandler = new Handler(getMainLooper());
        final Resources res = getResources();

        mViewBinding.pager.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                // Adjust page margins:
                //   A little extra horizontal spacing between pages looks a bit
                //   less crowded on a round display.
                final boolean round = insets.isRound();
                int rowMargin = res.getDimensionPixelOffset(R.dimen.page_row_margin);
                int colMargin = res.getDimensionPixelOffset(round ?
                        R.dimen.page_column_margin_round : R.dimen.page_column_margin);
                mViewBinding.pager.setPageMargins(rowMargin, colMargin);

                // GridViewPager relies on insets to properly handle
                // layout for round displays. They must be explicitly
                // applied since this listener has taken them over.
                mViewBinding.pager.onApplyWindowInsets(insets);
                return insets;
            }
        });

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_LONG).show();
            mBleSupported = false;
        }

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = mBluetoothManager.getAdapter();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                        }
                    }
                });
                builder.show();
            } else {
                showScanBlu();
            }
        } else {
            showScanBlu();
        }

        mUdooBluManager = ((BluWearApplication) getApplication()).getBluManager();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
//            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
//            mTextView.setTextColor(getResources().getColor(android.R.color.white));
//            mClockView.setVisibility(View.VISIBLE);
//
//            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
//            mContainerView.setBackground(null);
//            mTextView.setTextColor(getResources().getColor(android.R.color.black));
//            mClockView.setVisibility(View.GONE);
        }
    }

    private void showScanBlu() {
        addFragment(new BluScanFragment());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, R.string.bt_on, Toast.LENGTH_SHORT).show();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(this, R.string.bt_not_on, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                Log.e(TAG, "Unknown request code");
                break;
        }
    }

    @Override
    public void onBluError(UdooBluException e) {
        if (e != null) {
            Log.i(TAG, "onBluError: " + e.getReason());
            switch (e.getReason()) {
                case UdooBluException.BLU_SERVICE_NOT_READY:
                    break;
                case UdooBluException.BLUETOOTH_CANNOT_START:
                    break;
                case UdooBluException.BLUETOOTH_LE_NOT_SUPPORTED:
                    break;
                case UdooBluException.BLUETOOTH_DISABLED:
                    break;
                case UdooBluException.BLUETOOTH_NOT_AVAILABLE:
                    break;
                case UdooBluException.LOCATION_PERMISSION_MISSING:
                    break;
                case UdooBluException.LOCATION_SERVICES_DISABLED:
                    break;
                case UdooBluException.BLU_SEQ_OBSERVER_ERROR:
                    break;
                case UdooBluException.BLU_READ_CHARAC_ERROR:
                    break;
                case UdooBluException.BLU_WRITE_CHARAC_ERROR:
                    break;
                case UdooBluException.BLU_GATT_SERVICE_NOT_FOUND:
                    break;
                case UdooBluException.BLU_SENSOR_NOT_FOUND:
                    break;
                case UdooBluException.BLU_WRITE_DESCR_ERROR:
                    break;
                case UdooBluException.BLU_NOTIFICATION_ERROR:
                    break;
                case UdooBluException.BLU_WRITE_PERIOD_NOTIFICATION_ERROR:
                    break;
                case UdooBluException.BLU_GENERIC_ERROR:
                default: {

                }
            }
        } else {
            Log.e(TAG, "onBluError: " + UdooBluException.BLU_GENERIC_ERROR);
        }
    }

    @Override
    public void onConnect(final BluItem bluItem) {
        mUdooBluManager.setIBluManagerCallback(new IBluManagerCallback() {
            @Override
            public void onBluManagerReady() {
                mUdooBluManager.connect(bluItem.address, new IBleDeviceListener() {
                    @Override
                    public void onDeviceConnected() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                removeFragment(mCurrentFragment);
                                mViewBinding.mainProgress.setVisibility(View.GONE);
                                VerticalGridPagerAdapter verticalGridPagerAdapter = new VerticalGridPagerAdapter(getBaseContext(), getFragmentManager(), bluItem.address);
                                mViewBinding.pager.setAdapter(verticalGridPagerAdapter);
                                DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
                                dotsPageIndicator.setPager(mViewBinding.pager);
                            }
                        });
                    }

                    @Override
                    public void onDeviceDisconnect() {
                        Log.i(TAG, "onDeviceDisconnect: ");
                    }

                    @Override
                    public void onError(UdooBluException runtimeException) {
                        onError(runtimeException);
                    }
                });
            }
        });
    }


    public void addFragment(Fragment fragment) {
        if (fragment != null) {
            mCurrentFragment = fragment;
            getFragmentManager().beginTransaction().replace(R.id.container, mCurrentFragment).commit();
        }
    }

    public void removeFragment(Fragment fragment) {
        if (fragment != null) {
            getFragmentManager().beginTransaction().remove(fragment).commit();
            fragment = null;
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
