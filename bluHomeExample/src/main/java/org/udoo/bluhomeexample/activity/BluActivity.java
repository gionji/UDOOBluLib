package org.udoo.bluhomeexample.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.udoo.bluhomeexample.BluHomeApplication;
import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.databinding.BluActivityLayoutBinding;
import org.udoo.bluhomeexample.databinding.UdoobluHeaderBinding;
import org.udoo.bluhomeexample.fragment.AccelerometerFragment;
import org.udoo.bluhomeexample.fragment.GyroscopeFragment;
import org.udoo.bluhomeexample.fragment.MagnetometerFragment;
import org.udoo.bluhomeexample.fragment.ManagerHomeSensorFragment;
import org.udoo.bluhomeexample.fragment.ManagerIOPinsFragment;
import org.udoo.bluhomeexample.fragment.brick.AmbientLuxBrickFragment;
import org.udoo.bluhomeexample.fragment.brick.BarometerBrickFragment;
import org.udoo.bluhomeexample.fragment.brick.HumidityBrickFragment;
import org.udoo.bluhomeexample.fragment.brick.TemperatureBrickFragment;
import org.udoo.bluhomeexample.interfaces.BluListener;
import org.udoo.bluhomeexample.interfaces.IFragmentToBluActivity;
import org.udoo.bluhomeexample.model.BluItem;
import org.udoo.bluhomeexample.view.ViewHolderHeader;
import org.udoo.udooblulib.exceptions.UdooBluException;
import org.udoo.udooblulib.interfaces.IBleDeviceListener;
import org.udoo.udooblulib.interfaces.IBluManagerCallback;
import org.udoo.udooblulib.interfaces.IReaderListener;
import org.udoo.udooblulib.interfaces.OnResult;
import org.udoo.udooblulib.manager.UdooBluManager;

import java.util.List;

/**
 * Created by harlem88 on 28/06/16.
 */

public class BluActivity extends AppCompatActivity implements IFragmentToBluActivity {
    public static final String EXTRA_BLU_DEVICE = "BLU_ITEM";
    private BluItem mBluItem;
    private UdooBluManager mUdooBluManager;
    private BluActivityLayoutBinding mViewBinding;
    private String mTitle;
    private int mPositionSelected;
    private Handler mHandler;


    public enum ITEM_SELECTED {HOME, TEMPERATURE, AMBLIGHT, HUMIDITY, BAROMETER, ACCELEROMETER, GYROSCOPE, MAGNETOMETER, IOPins, SHOP, OAD, NOITEM}

    private ITEM_SELECTED mItemSelected;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = DataBindingUtil.setContentView(this, R.layout.blu_activity_layout);

        if (savedInstanceState == null) {
            mItemSelected = ITEM_SELECTED.NOITEM;
            mBluItem = getIntent().getParcelableExtra(EXTRA_BLU_DEVICE);
        } else {
            mPositionSelected = savedInstanceState.getInt("POSITION_SELECTED");
            String item_selected = savedInstanceState.getString("ITEM_SELECTED");
            if (item_selected != null && item_selected.length() > 0)
                mItemSelected = ITEM_SELECTED.valueOf(item_selected);
        }

        setSupportActionBar(mViewBinding.toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mBluItem.name);
        }

        mHandler = new Handler();
        mViewBinding.pbBusy.setVisibility(View.VISIBLE);

        mViewBinding.navView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);
        new ViewHolderHeader(mViewBinding.navView);

        final UdoobluHeaderBinding navDrawerHeaderBinding = UdoobluHeaderBinding.bind(mViewBinding.navView.getHeaderView(0).findViewById(R.id.nav_header_root));
        navDrawerHeaderBinding.setBluItem(mBluItem);

        mUdooBluManager = ((BluHomeApplication) getApplication()).getBluManager();

        if (!mBluItem.isConnected())
            connect();
        else {
            onNavigationItemSelectedListener.onNavigationItemSelected(getMenuItem(ITEM_SELECTED.HOME.ordinal()));
            mViewBinding.pbBusy.setVisibility(View.GONE);

            mUdooBluManager.readFirmwareVersion(mBluItem.address, new IReaderListener<byte[]>() {
                @Override
                public void oRead(byte[] value) {
                    mBluItem.version = new String(value);
                    navDrawerHeaderBinding.setBluItem(mBluItem);
                }

                @Override
                public void onError(UdooBluException runtimeException) {
                }
            });
            populateMenuItem();
        }
    }

    private void populateMenuItem() {
        getMenuItem(1).setVisible(mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.TEMP));
        getMenuItem(2).setVisible(mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.AMB_LIG));
        getMenuItem(3).setVisible(mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.HUM));
        getMenuItem(4).setVisible(mUdooBluManager.isSensorDetected(UdooBluManager.SENSORS.BAR));
    }


    public void showMainToolbar() {
        if (mViewBinding.toolbar.getVisibility() != View.VISIBLE) {
            mViewBinding.toolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(mViewBinding.toolbar);
        } else {
            mViewBinding.toolbar.getMenu().clear();
        }
        mViewBinding.toolbar.setTitle(mBluItem.name);

    }

    public MenuItem getMenuItem(int position) {
        int resMenu;
        switch (position) {
            case 1:
                resMenu = R.id.nav_temperature;
                break;
            case 2:
                resMenu = R.id.nav_ambLight;
                break;
            case 3:
                resMenu = R.id.nav_humidity;
                break;
            case 4:
                resMenu = R.id.nav_barometer;
                break;
            case 5:
                resMenu = R.id.nav_accelerometer;
                break;
            case 6:
                resMenu = R.id.nav_gyroscope;
                break;
            case 7:
                resMenu = R.id.nav_magnetometer;
                break;
            case 8:
                resMenu = R.id.nav_io_pins;
                break;
            case 9:
                resMenu = R.id.nav_shop;
                break;
            case 10:
                resMenu = R.id.nav_oad;
                break;
            default:
                resMenu = R.id.nav_home;
        }

        return mViewBinding.navView.getMenu().findItem(resMenu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean res = super.onCreateOptionsMenu(menu);
        getMenuItem(0).setTitle(mBluItem.name);
        return res;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mViewBinding.drawerLayoutRoot.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            if (mItemSelected != null) {
                MenuItem preMenuSelected = getMenuItem(mItemSelected.ordinal());

                if (preMenuSelected.isChecked())
                    preMenuSelected.setChecked(false);
            }
            menuItem.setChecked(true);

            switch (menuItem.getItemId()) {
                case R.id.nav_temperature:
                    if (mItemSelected != ITEM_SELECTED.TEMPERATURE) {
                        mTitle = getString(R.string.temperature);
                        showMainToolbar();
                        replaceFragmentAndInit(TemperatureBrickFragment.Builder(mBluItem.address), ITEM_SELECTED.TEMPERATURE.name(), false);
                        mItemSelected = ITEM_SELECTED.TEMPERATURE;
                    }
                    break;
                case R.id.nav_ambLight:
                    if (mItemSelected != ITEM_SELECTED.AMBLIGHT) {
                        mTitle = getString(R.string.ambient_light);
                        showMainToolbar();
                        replaceFragmentAndInit(AmbientLuxBrickFragment.Builder(mBluItem.address), ITEM_SELECTED.AMBLIGHT.name(), false);
                        mItemSelected = ITEM_SELECTED.AMBLIGHT;
                    }
                    break;
                case R.id.nav_humidity:
                    if (mItemSelected != ITEM_SELECTED.HUMIDITY) {
                        mTitle = getString(R.string.humidity);
                        showMainToolbar();
                        replaceFragmentAndInit(HumidityBrickFragment.Builder(mBluItem.address), ITEM_SELECTED.HUMIDITY.name(), false);
                        mItemSelected = ITEM_SELECTED.HUMIDITY;
                    }
                    break;
                case R.id.nav_barometer:
                    if (mItemSelected != ITEM_SELECTED.BAROMETER) {
                        mTitle = getString(R.string.barometer);
                        showMainToolbar();
                        replaceFragmentAndInit(BarometerBrickFragment.Builder(mBluItem.address), ITEM_SELECTED.BAROMETER.name(), false);
                        mItemSelected = ITEM_SELECTED.IOPins;
                    }
                    break;
                case R.id.nav_accelerometer:
                    if (mItemSelected != ITEM_SELECTED.ACCELEROMETER) {
                        mTitle = getString(R.string.title_section2);
                        showMainToolbar();
                        replaceFragmentAndInit(AccelerometerFragment.Builder(mBluItem.address), ITEM_SELECTED.ACCELEROMETER.name(), false);
                        mItemSelected = ITEM_SELECTED.ACCELEROMETER;
                    }
                    break;
                case R.id.nav_gyroscope:
                    if (mItemSelected != ITEM_SELECTED.GYROSCOPE) {
                        mTitle = getString(R.string.title_section3);
                        showMainToolbar();
                        replaceFragmentAndInit(GyroscopeFragment.Builder(mBluItem.address), ITEM_SELECTED.GYROSCOPE.name(), false);
                        mItemSelected = ITEM_SELECTED.GYROSCOPE;
                    }
                    break;
                case R.id.nav_magnetometer:
                    if (mItemSelected != ITEM_SELECTED.MAGNETOMETER) {
                        mTitle = getString(R.string.title_section4);
                        showMainToolbar();
                        replaceFragmentAndInit(MagnetometerFragment.Builder(mBluItem.address), ITEM_SELECTED.MAGNETOMETER.name(), false);
                        mItemSelected = ITEM_SELECTED.MAGNETOMETER;
                    }
                    break;
                case R.id.nav_io_pins:
                    if (mItemSelected != ITEM_SELECTED.IOPins) {
                        mTitle = getString(R.string.title_section5);
                        showMainToolbar();
                        replaceFragmentAndInit(ManagerIOPinsFragment.Builder(mBluItem.address), ITEM_SELECTED.IOPins.name(), false);
                        mItemSelected = ITEM_SELECTED.IOPins;
                    }
                    break;
                case R.id.nav_shop:
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.udoo.org/udoo-bricks/")));
                    break;
                case R.id.nav_oad:
                    if (mItemSelected != ITEM_SELECTED.OAD) {
//                        mTitle = getString(R.string.title_section5);
//                        showMainToolbar();
//                        replaceFragmentAndInit(new ManagerIOPinsFragment(), ITEM_SELECTED.IOPins.name(), false);
                        mItemSelected = ITEM_SELECTED.OAD;
                        Intent intent = new Intent(getBaseContext(), OADActivity.class);
                        intent.putExtra(EXTRA_BLU_DEVICE, mBluItem);
                        startActivity(intent);
                    }
                    break;
                default:
                    if (mItemSelected != ITEM_SELECTED.HOME) {
                        mTitle = getString(R.string.title_section1);
                        showMainToolbar();
                        replaceFragmentAndInit(ManagerHomeSensorFragment.Builder(mBluItem.address), ITEM_SELECTED.HOME.name(), false);
                        mItemSelected = ITEM_SELECTED.HOME;
                    }
            }

            new Handler(Looper.getMainLooper()) {
                public void handleMessage(Message msg) {
                    mViewBinding.drawerLayoutRoot.closeDrawers();
                }
            }.sendEmptyMessageDelayed(-1, 50);
            return true;
        }
    };

    @Override
    public void onBackPressed() {
        if (mItemSelected != ITEM_SELECTED.HOME && mItemSelected != ITEM_SELECTED.NOITEM) {
            onNavigationItemSelectedListener.onNavigationItemSelected(getMenuItem(ITEM_SELECTED.HOME.ordinal()));
        } else
            super.onBackPressed();
    }

    /**
     * @param fragment  fragment class
     * @param tag       name class fragment
     * @param backstack enable move to backStack
     */
    private void replaceFragmentAndInit(final Fragment fragment, final String tag, final boolean backstack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        if (tag != null)
            ft.replace(R.id.container, fragment, tag);
        else
            ft.replace(R.id.container, fragment);

        if (backstack)
            ft.addToBackStack(null);
        else if (fragmentManager.getBackStackEntryCount() > 0)
            fragmentManager.popBackStack();

        ft.commit();
    }

    private void sendEventsToFragments(boolean connectionEvent) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fr : fragments) {
                if (fr instanceof BluListener) {
                    if (connectionEvent) ((BluListener) fr).onConnect();
                    else ((BluListener) fr).onDisconnect();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        onNavigationItemSelectedListener.onNavigationItemSelected(getMenuItem(mItemSelected.ordinal()));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("POSITION_SELECTED", mPositionSelected);

        if (mItemSelected != null)
            outState.putString("ITEM_SELECTED", mItemSelected.name());
    }


    private void connect() {
        mUdooBluManager.setIBluManagerCallback(new IBluManagerCallback() {
            @Override
            public void onBluManagerReady() {
                mUdooBluManager.connect(mBluItem.address, iBleDeviceListener);
            }
        });
    }

    private IBleDeviceListener iBleDeviceListener = new IBleDeviceListener() {
        @Override
        public void onDeviceConnected() {
            mBluItem.setConnected(true);
            mUdooBluManager.getBluItem(getBaseContext(), mBluItem.address, new OnResult<String>() {
                @Override
                public void onSuccess(String o) {
                    if (o == null || o.length() == 0) {
//                        showChangeNameDialog();
                        mViewBinding.pbBusy.setVisibility(View.GONE);
                    } else {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "Blu Connected", Toast.LENGTH_LONG).show();
                                onNavigationItemSelectedListener.onNavigationItemSelected(getMenuItem(ITEM_SELECTED.HOME.ordinal()));
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendEventsToFragments(true);
                                    }
                                }, 500);

                                mViewBinding.pbBusy.setVisibility(View.GONE);
                            }
                        }, 500);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    mViewBinding.pbBusy.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void onDeviceDisconnect() {
            mBluItem.setConnected(false);
            Toast.makeText(getBaseContext(), "Blu disconnected...", Toast.LENGTH_LONG).show();
            reTryConnection();
        }

        @Override
        public void onError(UdooBluException runtimeException) {
            mBluItem.setConnected(false);
            mViewBinding.pbBusy.setVisibility(View.GONE);
            if (!isFinishing()) reTryConnection();

        }
    };

    private void reTryConnection() {
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), "Retry connection...", Toast.LENGTH_LONG).show();
                mUdooBluManager.connect(mBluItem.address, iBleDeviceListener);
            }
        }, 5000);
    }

    @Override
    public void onBluSensorClicked(ITEM_SELECTED itemSelected) {
        onNavigationItemSelectedListener.onNavigationItemSelected(getMenuItem(itemSelected.ordinal()));
    }
}
