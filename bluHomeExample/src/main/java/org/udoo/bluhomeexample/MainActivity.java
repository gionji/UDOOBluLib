package org.udoo.bluhomeexample;

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

import org.udoo.bluhomeexample.activity.BluActivity;
import org.udoo.bluhomeexample.interfaces.IFragmentToActivity;
import org.udoo.udooblulib.exceptions.UdooBluException;
import org.udoo.udooblulib.model.BluItem;

public class MainActivity extends AppCompatActivity implements IFragmentToActivity{
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private boolean mBleSupported = true;
    private static BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBtAdapter = null;
    private final String TAG = "MainActivity";

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
                showScanBlu();
            }
        } else if (savedInstanceState == null && mBleSupported) {
            showScanBlu();
        } else {
            //Error TODO
        }
    }

    private void showScanBlu(){
        getFragmentManager().beginTransaction()
                .add(R.id.container, new ScanBluFragment()).commit();
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
    public void onBluError(UdooBluException e) {
        if (e != null) {
            Log.i(TAG, "onBluError: "+e.getReason());
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
        }else {
            Log.e(TAG, "onBluError: "+UdooBluException.BLU_GENERIC_ERROR);
        }
    }

    @Override
    public void onConnect(BluItem bluItem) {
        Intent deviceIntent = new Intent(this, BluActivity.class);
        deviceIntent.putExtra(BluActivity.EXTRA_BLU_DEVICE, bluItem);
        startActivity(deviceIntent);
    }
}
