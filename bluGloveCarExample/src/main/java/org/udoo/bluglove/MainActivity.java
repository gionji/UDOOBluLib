package org.udoo.bluglove;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.udoo.bluglove.scan.IFragmentToActivity;
import org.udoo.bluglove.scan.ScanMultipleBluFragment;
import org.udoo.udooblulib.interfaces.IBleDeviceListener;
import org.udoo.udooblulib.manager.UdooBluManager;

import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity implements IFragmentToActivity{
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private boolean mBleSupported = true;
    private static BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBtAdapter = null;
    private final String TAG = "MainActivity";
    private AtomicBoolean onBluConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_LONG).show();
            mBleSupported = false;
        }

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = mBluetoothManager.getAdapter();

        onBluConnected = new AtomicBoolean(false);

        if (mBtAdapter == null || !mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

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
            }else{
                getFragmentManager().beginTransaction()
                        .add(R.id.container, new ScanMultipleBluFragment()).commit();
            }
        } else if (savedInstanceState == null && mBleSupported) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ScanMultipleBluFragment()).commit();
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
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
    public void onTwoBluSelected(final String address1, final String address2) {
        final UdooBluManager udooBluManager = ((BluNeoGloveCarApplication) getApplication()).getBluManager();
        udooBluManager.connect(address1, new IBleDeviceListener() {
            @Override
            public void onDeviceConnected() {
                udooBluManager.discoveryServices(address1);
            }

            @Override
            public void onServicesDiscoveryCompleted(String address) {
                onBluConnected.set(true);
                lunchGloveFragment(address1, address2);
            }

            @Override
            public void onDeviceDisconnect() {

            }
        });

//        udooBluManager.connect(address2, new IBleDeviceListener() {
//            @Override
//            public void onDeviceConnected() {
//                udooBluManager.discoveryServices(address2);
//            }
//
//            @Override
//            public void onServicesDiscoveryCompleted() {
//                lunchGloveFragment(address1, address2);
//            }
//
//            @Override
//            public void onDeviceDisconnect() {
//
//            }
//        });
    }

    private void lunchGloveFragment(String address1, String address2){
        if(onBluConnected.compareAndSet(false, true)){
        }else{
            getFragmentManager().beginTransaction().replace(R.id.container, BluNeoGloveCarFragment.Builder(address1, address2)).commit();
        }
    }

    public void connects() {
        final UdooBluManager udooBluManager = ((BluNeoGloveCarApplication) getApplication()).getBluManager();
        udooBluManager.connects(new IBleDeviceListener() {
            @Override
            public void onDeviceConnected() {

            }

            @Override
            public void onServicesDiscoveryCompleted(String address1) {
                onBluConnected.set(true);
                lunchGloveFragment(address1 , address1);
            }

            @Override
            public void onDeviceDisconnect() {

            }
        });
    }
}
