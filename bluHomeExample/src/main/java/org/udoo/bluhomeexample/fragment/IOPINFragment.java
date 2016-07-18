package org.udoo.bluhomeexample.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.databinding.FragmentIopinLayoutBinding;

/**
 * Created by harlem88 on 03/07/16.
 */

public class IOPINFragment extends UdooFragment{

    private FragmentIopinLayoutBinding mViewBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_iopin_layout, container, false);
        return mViewBinding.root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewBinding.list.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
