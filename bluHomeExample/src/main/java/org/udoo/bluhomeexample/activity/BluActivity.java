package org.udoo.bluhomeexample.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
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
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.udoo.bluhomeexample.BluHomeApplication;
import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.databinding.BluActivityLayoutBinding;
import org.udoo.bluhomeexample.databinding.EditDialogBinding;
import org.udoo.bluhomeexample.databinding.UdoobluHeaderBinding;
import org.udoo.bluhomeexample.fragment.AccelerometerFragment;
import org.udoo.bluhomeexample.fragment.GyroscopeFragment;
import org.udoo.bluhomeexample.fragment.MagnetometerFragment;
import org.udoo.bluhomeexample.fragment.ManagerHomeSensorFragment;
import org.udoo.bluhomeexample.interfaces.BluListener;
import org.udoo.bluhomeexample.model.BluItem;
import org.udoo.bluhomeexample.view.ViewHolderHeader;
import org.udoo.udooblulib.exceptions.UdooBluException;
import org.udoo.udooblulib.interfaces.IBleDeviceListener;
import org.udoo.udooblulib.interfaces.IBluManagerCallback;
import org.udoo.udooblulib.interfaces.OnResult;
import org.udoo.udooblulib.manager.UdooBluManager;
import org.udoo.udooblulib.manager.UdooBluManagerImpl;
import org.udoo.udooblulib.scan.BluScanCallBack;

import java.util.List;

/**
 * Created by harlem88 on 28/06/16.
 */

public class BluActivity extends AppCompatActivity {
    public static final String EXTRA_BLU_DEVICE = "BLU_ITEM";
    private BluItem mBluItem;
    private boolean isConnected;
    private UdooBluManager mUdooBluManager;
    private BluActivityLayoutBinding mViewBinding;
    private String mTitle;
    private int mPositionSelected;
    private Handler mHandler;

    public enum ITEM_SELECTED {HOME, ACCELEROMETER, GYROSCOPE, MAGNETOMETER, IOPins, NOITEM}

    private ITEM_SELECTED mItemSelected;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = DataBindingUtil.setContentView(this, R.layout.blu_activity_layout);

        if (savedInstanceState == null) {
            mItemSelected = ITEM_SELECTED.NOITEM;
            mBluItem = getIntent().getParcelableExtra(EXTRA_BLU_DEVICE);
        }

        setSupportActionBar(mViewBinding.toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mHandler = new Handler();
        mViewBinding.pbBusy.setVisibility(View.VISIBLE);

        mViewBinding.navView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);
        new ViewHolderHeader(mViewBinding.navView);

        UdoobluHeaderBinding navDrawerHeaderBinding = UdoobluHeaderBinding.bind(mViewBinding.navView.getHeaderView(0).findViewById(R.id.nav_header_root));
        navDrawerHeaderBinding.setBleItem(mBluItem);

        mUdooBluManager = ((BluHomeApplication) getApplication()).getBluManager();

        if (!isConnected)
            connect();

    }

    public void showMainToolbar() {
        if (mViewBinding.toolbar.getVisibility() != View.VISIBLE) {
            mViewBinding.toolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(mViewBinding.toolbar);
        } else {
            mViewBinding.toolbar.getMenu().clear();
        }
        mViewBinding.toolbar.setTitle(mTitle);

    }

    public MenuItem getMenuItem(int position) {
        int resMenu;
        switch (position) {
            case 1:
                resMenu = R.id.nav_accelerometer;
                break;
            case 2:
                resMenu = R.id.nav_gyroscope;
                break;
            case 3:
                resMenu = R.id.nav_magnetometer;
                break;
            case 4:
                resMenu = R.id.nav_io_pins;
                break;
            default:
                resMenu = R.id.nav_home;
        }

        return mViewBinding.navView.getMenu().findItem(resMenu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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
//                        mTitle = getString(R.string.title_section5);
//                        showMainToolbar();
//                        replaceFragmentAndInit(new ManagerIOPinsFragment(), ITEM_SELECTED.IOPins.name(), false);
//                        mItemSelected = ITEM_SELECTED.IOPins;
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("POSITION_SELECTED", mPositionSelected);
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
            isConnected = true;
            mUdooBluManager.getBluItem(getBaseContext(), mBluItem.address, new OnResult<String>() {
                @Override
                public void onSuccess(String o) {
                    if (o == null || o.length() == 0) {
                        showChangeNameDialog();
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
            isConnected = false;
            Toast.makeText(getBaseContext(), "Blu disconnected...", Toast.LENGTH_LONG).show();
            reTryConnection();
        }

        @Override
        public void onError(UdooBluException runtimeException) {
            isConnected = false;
            mViewBinding.pbBusy.setVisibility(View.GONE);
            if(!isFinishing()) reTryConnection();

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

    public void showChangeNameDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BluActivity.this);
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                final EditDialogBinding viewDialogBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.edit_dialog, null, false);
                dialogBuilder.setView(viewDialogBinding.getRoot());

                dialogBuilder.setTitle("Blu name");
                dialogBuilder.setMessage("Insert name below");
                dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable edit = viewDialogBinding.edit.getText();
                        if (edit != null) {
                            String name = edit.toString();
                            if (name.length() > 0)
                                mUdooBluManager.saveBluItem(getBaseContext(), mBluItem.address, name);
                        }
                        onNavigationItemSelectedListener.onNavigationItemSelected(getMenuItem(ITEM_SELECTED.HOME.ordinal()));
                    }
                });
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        });
    }
}
