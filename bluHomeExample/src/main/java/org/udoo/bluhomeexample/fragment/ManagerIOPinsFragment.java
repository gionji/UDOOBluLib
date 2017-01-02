package org.udoo.bluhomeexample.fragment;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;

import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import org.udoo.bluhomeexample.BluHomeApplication;
import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.adapter.IOPinAdapter;
import org.udoo.bluhomeexample.adapter.StepperIOPinAdapter;
import org.udoo.bluhomeexample.databinding.FragmentIopinLayoutBinding;
import org.udoo.bluhomeexample.dialog.AddIOPinDialog;
import org.udoo.bluhomeexample.interfaces.IIOPinView;
import org.udoo.bluhomeexample.presenter.IOPINPresenter;
import org.udoo.udooblulib.model.IOPin;

import java.util.ArrayList;

/**
 * Created by harlem88 on 03/07/16.
 */

public class ManagerIOPinsFragment extends UdooFragment implements IIOPinView, StepperIOPinAdapter.IOnStepperIOPin {

    private FragmentIopinLayoutBinding mViewBinding;
    private AddIOPinDialog mAddIOPinDialog;
    private IOPINPresenter mIOIopinPresenter;
    private Handler mUIHandler;
    private IOPinAdapter mIoPinAdapter;
    private ProgressDialog mProgressDialog;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean isStepperinView;


    public static UdooFragment Builder(String address) {
        return Builder(new ManagerIOPinsFragment(), address);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIOIopinPresenter = new IOPINPresenter(mBluAddress, ((BluHomeApplication) getActivity().getApplication()), this);
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

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mViewBinding.listGpio.setLayoutManager(mLinearLayoutManager);

        mIoPinAdapter = new IOPinAdapter(new ArrayList<IOPin>());
        StepperIOPinAdapter stepperIOPinAdapter = new StepperIOPinAdapter(getContext());
        stepperIOPinAdapter.setIOnStepperIOPin(this);
        mViewBinding.listGpio.setAdapter(stepperIOPinAdapter);
        isStepperinView = true;

        mIoPinAdapter.setIioPinValueCallback(mIOIopinPresenter);

        setListener();
    }

    private void setListener() {
        mViewBinding.btnAddIopin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revealDialog();
            }
        });
        mViewBinding.btnAddIopin.setVisibility(View.GONE);
    }

    public void showAddIOPinDialog(AddIOPinDialog ioPinDialog) {
        mAddIOPinDialog = ioPinDialog;
        getChildFragmentManager().beginTransaction().add(mAddIOPinDialog, "AddIOPinDialog").commit();
        mAddIOPinDialog.setResultCallback(mIOIopinPresenter);
    }

    @Override
    public void addIOPin(final IOPin ioPin) {
        if(isStepperinView){

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), mLinearLayoutManager.getOrientation());
            dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
            mViewBinding.listGpio.addItemDecoration(dividerItemDecoration);

            mViewBinding.listGpio.removeAllViews();
            mViewBinding.listGpio.setAdapter(mIoPinAdapter);
            isStepperinView = false;
        }
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                mIoPinAdapter.addIOPin(ioPin);
            }
        });
    }

    @Override
    public void showProgress(boolean show) {
        if (show && !mProgressDialog.isShowing())
            mProgressDialog.show();
        else if (!show && mProgressDialog.isShowing())
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

    @Override
    public void dismissAnimation() {
        mViewBinding.root.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));
    }

    @Override
    public void onStepperIOPinDone(boolean done, int pos) {
        if (done) {
            mViewBinding.btnAddIopin.show();
        } else {
            mViewBinding.btnAddIopin.hide();
        }
        mLinearLayoutManager.scrollToPosition(pos);
    }

    private void revealDialog() {
        final ViewGroup.LayoutParams originalParams = mViewBinding.btnAddIopin.getLayoutParams();
        Transition transition = TransitionInflater.from(getContext()).inflateTransition(R.transition.changebounds_with_arcmotion);
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                animateRevealColor(mViewBinding.root, R.color.accent);
//                body.setText(R.string.reveal_body3);
//                body.setTextColor(ContextCompat.getColor(RevealActivity.this, R.color.theme_red_background));
                mViewBinding.btnAddIopin.setLayoutParams(originalParams);
                showAddIOPinDialog(new AddIOPinDialog());
            }

            @Override
            public void onTransitionCancel(Transition transition) {
            }

            @Override
            public void onTransitionPause(Transition transition) {
            }

            @Override
            public void onTransitionResume(Transition transition) {
            }
        });

        TransitionManager.beginDelayedTransition(mViewBinding.root, transition);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        mViewBinding.btnAddIopin.setLayoutParams(layoutParams);
    }

    private void animateRevealColor(ViewGroup viewRoot, @ColorRes int color) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        animateRevealColorFromCoordinates(viewRoot, color, cx, cy);
    }

    private Animator animateRevealColorFromCoordinates(ViewGroup viewRoot, @ColorRes int color, int x, int y) {
        float finalRadius = (float) Math.hypot(viewRoot.getWidth(), viewRoot.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, x, y, 0, finalRadius);
        viewRoot.setBackgroundColor(ContextCompat.getColor(getContext(), color));
        anim.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
        return anim;
    }

}
