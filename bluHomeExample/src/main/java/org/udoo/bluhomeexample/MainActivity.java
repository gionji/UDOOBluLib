package org.udoo.bluhomeexample;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.udoo.bluhomeexample.activity.BluActivity;
import org.udoo.bluhomeexample.activity.ScreenSlidePagerActivity;
import org.udoo.bluhomeexample.interfaces.IFragmentToActivity;
import org.udoo.udooblulib.exceptions.UdooBluException;
import org.udoo.bluhomeexample.model.BluItem;

public class MainActivity extends AppCompatActivity implements IFragmentToActivity {
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


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Home");

        SharedPreferences appInfo = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = appInfo.edit();
        boolean isFirstLaunch = appInfo.getBoolean("firstLaunch", true);
        if (isFirstLaunch) {
            startTutorial();
            editor.putBoolean("firstLaunch", false);
            editor.apply();
        } else {
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
                } else {
                    showScanBlu(false);
                }
            } else if (savedInstanceState == null && mBleSupported) {
                showScanBlu(false);
            } else {
                //Error TODO
            }
        }
    }

    private void startTutorial() {
        startActivity(new Intent(getBaseContext(), ScreenSlidePagerActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.tutorial_blu_item:
                startTutorial();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showScanBlu(boolean isStateLost) {
        if (!isStateLost) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new ScanBluFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new ScanBluFragment()).commitAllowingStateLoss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showScanBlu(true);
                        }
                    });
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
            Log.i(TAG, "onBluError: " + e.getReason());
            String err;
            switch (e.getReason()) {
                case UdooBluException.BLU_SERVICE_NOT_READY:
                    err = "Service not ready";
                    break;
                case UdooBluException.BLUETOOTH_CANNOT_START:
                    err = "Bluetooth not start";
                    break;
                case UdooBluException.BLUETOOTH_LE_NOT_SUPPORTED:
                    err = "ble non supported";
                    break;
                case UdooBluException.BLUETOOTH_DISABLED:
                    err = "Bluetooth disabled";
                    break;
                case UdooBluException.BLUETOOTH_NOT_AVAILABLE:
                    err = "Bluetooth not available";
                    break;
                case UdooBluException.LOCATION_PERMISSION_MISSING:
                    err = "Location permission missing";
                    break;
                case UdooBluException.LOCATION_SERVICES_DISABLED:
                    err = "Location disabled";
                    break;
                case UdooBluException.BLU_SEQ_OBSERVER_ERROR:
                    err = "BluManager error";
                    break;
                case UdooBluException.BLU_READ_CHARAC_ERROR:
                    err = "BluManager read char error";
                    break;
                case UdooBluException.BLU_WRITE_CHARAC_ERROR:
                    err = "BluManager write char error";
                    break;
                case UdooBluException.BLU_GATT_SERVICE_NOT_FOUND:
                    err = "BluManager service gatt not found";
                    break;
                case UdooBluException.BLU_SENSOR_NOT_FOUND:
                    err = "BluManager blu sensor not found";
                    break;
                case UdooBluException.BLU_WRITE_DESCR_ERROR:
                    err = "BluManager write descr error";
                    break;
                case UdooBluException.BLU_NOTIFICATION_ERROR:
                    err = "BluManager notification error";
                    break;
                case UdooBluException.BLU_WRITE_PERIOD_NOTIFICATION_ERROR:
                    err = "BluManager write period notification error";
                    break;
                case UdooBluException.BLU_GENERIC_ERROR:
                default:
                    err = "Generic error";
            }
            Toast.makeText(this, err, Toast.LENGTH_LONG).show();
        } else {
            Log.e(TAG, "onBluError: " + UdooBluException.BLU_GENERIC_ERROR);
        }
    }

    @Override
    public void onConnect(BluItem bluItem) {
        Intent deviceIntent = new Intent(this, BluActivity.class);
        deviceIntent.putExtra(BluActivity.EXTRA_BLU_DEVICE, bluItem);
        startActivity(deviceIntent);
    }
}
