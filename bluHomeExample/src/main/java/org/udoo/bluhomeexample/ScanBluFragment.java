package org.udoo.bluhomeexample;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.udoo.bluhomeexample.databinding.FragmentBluScanBinding;
import org.udoo.bluhomeexample.dialog.BluSaveDialog;
import org.udoo.bluhomeexample.interfaces.IBluScanView;
import org.udoo.bluhomeexample.interfaces.IFragmentToActivity;
import org.udoo.bluhomeexample.manager.BluScanPresenter;
import org.udoo.bluhomeexample.model.BluItem;
import org.udoo.udooblulib.exceptions.UdooBluException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harlem88 on 02/04/16.
 */
public class ScanBluFragment extends Fragment implements IBluScanView{

    private BluItemAdapter mBleItemAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private IFragmentToActivity mIFragmentToActivity;
    private FragmentBluScanBinding mViewBinding;
    private BluScanPresenter mBluScanPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIFragmentToActivity = (IFragmentToActivity) getActivity();
        mBluScanPresenter = new BluScanPresenter
                (((BluHomeApplication) getActivity().getApplication()).getBluManager(), getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_blu_scan, container, false);
        return mViewBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewBinding.list.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mViewBinding.list.setLayoutManager(mLayoutManager);
        mBleItemAdapter = new BluItemAdapter(new ArrayList<BluItem>());
        mViewBinding.list.setAdapter(mBleItemAdapter);

        mBluScanPresenter.onViewCreated(this);
        mBleItemAdapter.setItemCLickListener(mBluScanPresenter);
        mViewBinding.swipe.setOnRefreshListener(mBluScanPresenter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBluScanPresenter.onResume();
    }


    @Override
    public void onError(UdooBluException runtimeException) {
        if (mIFragmentToActivity != null)
            mIFragmentToActivity.onBluError(runtimeException);
    }

    @Override
    public void onConnectPage(BluItem item) {
        if (mIFragmentToActivity != null)
            mIFragmentToActivity.onConnect(item);
    }

    @Override
    public void setRefresh(boolean refresh) {
        mViewBinding.swipe.setRefreshing(refresh);
    }

    @Override
    public void showDialog(BluSaveDialog bluSaveDialog) {
        getChildFragmentManager().beginTransaction().add(bluSaveDialog, "BluSaveDialog").commit();
    }

    @Override
    public void addDevice(BluItem bluItem) {
        mViewBinding.textNoBlus.setVisibility(View.GONE);
        mBleItemAdapter.addDevice(bluItem);
        mBleItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateDevice(BluItem bluItem) {
        mBleItemAdapter.updateDevice(bluItem);
    }

    @Override
    public void addDevices(List<BluItem> bluItems) {
        mViewBinding.textNoBlus.setVisibility(View.GONE);
        mBleItemAdapter.addDevices(bluItems);
        mBleItemAdapter.notifyDataSetChanged();
    }


}
