package org.udoo.udoobluwearexample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.wearable.view.CardFragment;
import org.udoo.udooblulib.manager.UdooBluManager;
import org.udoo.udoobluwearexample.BluWearApplication;
import org.udoo.udoobluwearexample.adapter.VerticalGridPagerAdapter;

import java.util.concurrent.Callable;

/**
 * Created by harlem88 on 03/03/16.
 */
public class BaseSensorCardFragment extends CardFragment implements VerticalGridPagerAdapter.TabsPageListener {
    private Callable callableOnFragmentResume;

    private static final String ADDRESS = "addr";
    protected String mBluAddress;
    protected UdooBluManager mUdooBluManager;

    protected static BaseSensorCardFragment Builder(BaseSensorCardFragment udooFragment, String address){
        if (udooFragment != null) {
            Bundle bundle = new Bundle();
            bundle.putString(ADDRESS, address);
            udooFragment.setArguments(bundle);
        }
        return udooFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBluAddress = getArguments().getString(ADDRESS);
    }

    @Override
    public void onStart() {
        super.onStart();
        mUdooBluManager = ((BluWearApplication)getActivity().getApplication()).getBluManager();
    }

    @Override
    public void onPageSelected() {

    }

    @Override
    public void onPageDeselect() {

    }

    @Override
    public void onFragmentResume(Callable<Void> voidCallable) {

    }
}
