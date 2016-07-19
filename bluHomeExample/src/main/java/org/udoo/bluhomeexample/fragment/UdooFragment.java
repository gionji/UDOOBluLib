package org.udoo.bluhomeexample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import org.udoo.bluhomeexample.BluHomeApplication;
import org.udoo.udooblulib.manager.UdooBluManager;

/**
 * Created by harlem88 on 29/06/16.
 */

public class UdooFragment extends Fragment {

    private static final String ADDRESS = "addr";
    protected String mBluAddress;
    protected UdooBluManager mUdooBluManager;

    protected static UdooFragment Builder(UdooFragment udooFragment, String address){
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
        mUdooBluManager = ((BluHomeApplication)getActivity().getApplication()).getBluManager();
    }
}
