package org.udoo.bluhomeexample.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.udoo.bluhomeexample.BluHomeApplication;
import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.databinding.BluActivityLayoutBinding;
import org.udoo.bluhomeexample.databinding.EditDialogBinding;
import org.udoo.bluhomeexample.databinding.UdoobluHeaderBinding;
import org.udoo.bluhomeexample.view.ViewHolderHeader;
import org.udoo.udooblulib.exceptions.UdooBluException;
import org.udoo.udooblulib.interfaces.IBleDeviceListener;
import org.udoo.udooblulib.interfaces.OnResult;
import org.udoo.udooblulib.manager.UdooBluManager;
import org.udoo.udooblulib.manager.UdooBluManagerImpl;
import org.udoo.udooblulib.model.BluItem;

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

    public enum ITEM_SELECTED {NOITEM, HOME, ACCELEROMETER, GYROSCOPE, MAGNETOMETER, IOPins}

    private ITEM_SELECTED mItemSelected;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
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
//                        mTitle = getString(R.string.title_section2);
//                        showMainToolbar();
//                        replaceFragmentAndInit(new ManagerAccelerometer(), ITEM_SELECTED.ACCELEROMETER.name(), false);
//                        mItemSelected = ITEM_SELECTED.ACCELEROMETER;
                    }
                    break;
                case R.id.nav_gyroscope:
                    if (mItemSelected != ITEM_SELECTED.GYROSCOPE) {
//                        mTitle = getString(R.string.title_section3);
//                        showMainToolbar();
//                        replaceFragmentAndInit(new ManagerGyroscopeFragment(), ITEM_SELECTED.GYROSCOPE.name(), false);
//                        mItemSelected = ITEM_SELECTED.GYROSCOPE;
                    }
                    break;
                case R.id.nav_magnetometer:
                    if (mItemSelected != ITEM_SELECTED.MAGNETOMETER) {
//                        mTitle = getString(R.string.title_section4);
//                        showMainToolbar();
//                        replaceFragmentAndInit(new ManagerMagnetometerFragment(), ITEM_SELECTED.MAGNETOMETER.name(), false);
//                        mItemSelected = ITEM_SELECTED.MAGNETOMETER;
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
//                        mTitle = getString(R.string.title_section1);
//                        showMainToolbar();
//                        ManagerHomeSensorFragment managerHomeSensorFragment = new ManagerHomeSensorFragment();
//                        managerHomeSensorFragment.setBleItem(mBleItem);
//                        replaceFragmentAndInit(managerHomeSensorFragment, ITEM_SELECTED.HOME.name(), false);
//                        mItemSelected = ITEM_SELECTED.HOME;
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
    private void replaceFragmentAndInit(Fragment fragment, String tag, boolean backstack) {

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("POSITION_SELECTED", mPositionSelected);
    }


    private void connect() {

        mUdooBluManager.setIBluManagerCallback(new UdooBluManagerImpl.IBluManagerCallback() {
            @Override
            public void onBluManagerReady() {
                mUdooBluManager.connect(mBluItem.address, new IBleDeviceListener() {
                    @Override
                    public void onDeviceConnected() {
                        isConnected = true;
                        mUdooBluManager.getBluItem(getBaseContext(), mBluItem.address, new OnResult<String>() {
                            @Override
                            public void onSuccess(String o) {
                                if (o == null || o.length() == 0)
                                    showChangeNameDialog();

                                mViewBinding.pbBusy.setVisibility(View.GONE);
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
                    }

                    @Override
                    public void onError(UdooBluException runtimeException) {
                        isConnected = false;
                        mViewBinding.pbBusy.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    public void showChangeNameDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final EditDialogBinding viewDialogBinding = DataBindingUtil.inflate(inflater, R.layout.edit_dialog, null, false);
        dialogBuilder.setView(viewDialogBinding.getRoot());

        dialogBuilder.setTitle("Custom dialog");
        dialogBuilder.setMessage("Enter text below");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Editable edit = viewDialogBinding.edit.getText();
                if (edit != null) {
                    String name = edit.toString();
                    if (name.length() > 0)
                        mUdooBluManager.saveBluItem(getBaseContext(), mBluItem.address, name);
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
}
