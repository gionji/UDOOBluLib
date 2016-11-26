package org.udoo.bluhomeexample.fragment;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.databinding.BrickLayoutBinding;
import org.udoo.bluhomeexample.model.BrickModel;
import org.udoo.bluhomeexample.util.BindableString;
import org.udoo.udooblulib.exceptions.UdooBluException;
import org.udoo.udooblulib.interfaces.INotificationListener;
import org.udoo.udooblulib.interfaces.OnBluOperationResult;

/**
 * Created by harlem88 on 26/11/16.
 */

public abstract class ManagerBrickFragment extends UdooFragment{

    private BrickLayoutBinding mVieBinding;
    protected BrickModel mBrickModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mVieBinding = DataBindingUtil.inflate(inflater, R.layout.brick_layout, container, false);
        return mVieBinding.getRoot();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setBrickModel();
        mVieBinding.setBrick(mBrickModel);
    }

    @BindingAdapter({"brick"})
    public static void setBrick(TextView view, BindableString value) {
        if (value != null) {
            view.setText(value.get());
        }
    }


    protected INotificationListener<byte[]> iNotificationListener = new INotificationListener<byte[]>() {
        @Override
        public void onNext(byte[] value) {
             mBrickModel.value.set(conversionByte(value));
        }

        @Override
        public void onError(UdooBluException runtimeException) {

        }
    };


    protected OnBluOperationResult<Boolean> onBluOperationResult = new OnBluOperationResult<Boolean>() {
        @Override
        public void onSuccess(Boolean aBoolean) {

        }

        @Override
        public void onError(UdooBluException runtimeException) {

        }
    };

    @Override
    public void onStart() {
        super.onStart();
        subscribeOnStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unSubscribeOnDestroy();
    }

    public abstract void setBrickModel();

    public abstract void subscribeOnStart();

    public abstract void unSubscribeOnDestroy();

    public abstract String conversionByte(byte[] value);
}
