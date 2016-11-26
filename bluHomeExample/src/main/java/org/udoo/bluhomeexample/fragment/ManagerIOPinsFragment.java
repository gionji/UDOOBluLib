package org.udoo.bluhomeexample.fragment;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.udoo.bluhomeexample.BluHomeApplication;
import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.adapter.IOPinAdapter;
import org.udoo.bluhomeexample.databinding.FragmentIopinLayoutBinding;
import org.udoo.bluhomeexample.decoration.MarginDecoration;
import org.udoo.bluhomeexample.dialog.AddIOPinDialog;
import org.udoo.bluhomeexample.dialog.BluSaveDialog;
import org.udoo.bluhomeexample.interfaces.IIOPinView;
import org.udoo.bluhomeexample.presenter.IOPINPresenter;
import org.udoo.udooblulib.model.IOPin;

import java.util.ArrayList;

/**
 * Created by harlem88 on 03/07/16.
 */

public class ManagerIOPinsFragment extends UdooFragment implements IIOPinView{

    private FragmentIopinLayoutBinding mViewBinding;
    private AddIOPinDialog mAddIOPinDialog;
    private IOPINPresenter mIOIopinPresenter;
    private Handler mUIHandler;
    private IOPinAdapter mIoPinAdapter;
    private ProgressDialog mProgressDialog;


    public static UdooFragment Builder(String address){
        return Builder(new ManagerIOPinsFragment(), address);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIOIopinPresenter = new IOPINPresenter(mBluAddress, ((BluHomeApplication)getActivity().getApplication()), this);
        mUIHandler = new Handler();
        mProgressDialog = new ProgressDialog(getContext());
    }

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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mViewBinding.listGpio.setLayoutManager(linearLayoutManager);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                linearLayoutManager.getOrientation());

        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));
        mViewBinding.listGpio.addItemDecoration(dividerItemDecoration);

        mIoPinAdapter = new IOPinAdapter(new ArrayList<IOPin>());
        mViewBinding.listGpio.setAdapter(mIoPinAdapter);
        mIoPinAdapter.setIioPinValueCallback(mIOIopinPresenter);

        setListener();
    }

    private void setListener(){
        mViewBinding.btnAddIopin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddIOPinDialog(new AddIOPinDialog());
            }
        });
    }

    public void showAddIOPinDialog(AddIOPinDialog ioPinDialog) {
        mAddIOPinDialog = ioPinDialog;
        getChildFragmentManager().beginTransaction().add(mAddIOPinDialog, "AddIOPinDialog").commit();
        mAddIOPinDialog.setResultCallback(mIOIopinPresenter);
    }

    @Override
    public void addIOPin(final IOPin ioPin) {
        mViewBinding.textEmpty.setVisibility(View.GONE);
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                mIoPinAdapter.addIOPin(ioPin);
            }
        });
    }

    @Override
    public void showProgress(boolean show) {
        if(show && !mProgressDialog.isShowing())
            mProgressDialog.show();
        else if(!show && mProgressDialog.isShowing())
             mProgressDialog.dismiss();
    }

    @Override
    public void updateIOPinDigital(final IOPin ioPin) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                mIoPinAdapter.updateIOPinDigital(ioPin);
            }
        });
    }

    @Override
    public void updateIOPinAnalog(final IOPin ioPin) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                mIoPinAdapter.updateIOPinAnalog(ioPin);
            }
        });
    }

}
