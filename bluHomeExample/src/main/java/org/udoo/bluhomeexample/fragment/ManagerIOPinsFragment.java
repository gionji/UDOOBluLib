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

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
    private ItemTouchHelper mItemTouchHelper;


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
        mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
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
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isStepperinView) {
                    mViewBinding.listGpio.removeAllViews();
                    mViewBinding.listGpio.setAdapter(mIoPinAdapter);
                    isStepperinView = false;
                    mItemTouchHelper.attachToRecyclerView(mViewBinding.listGpio);
                }
                mIoPinAdapter.addIOPin(ioPin);
            }
        });
    }

    private ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            int swipedPosition = viewHolder.getAdapterPosition();
            if(!isStepperinView) mIoPinAdapter.remove(swipedPosition);
        }
    };

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
        mViewBinding.layerAnimationIopin.setVisibility(View.GONE);
        mViewBinding.btnAddIopin.show();
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
                mViewBinding.btnAddIopin.hide();
                animateRevealColor(mViewBinding.root);
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

    private void animateRevealColor(ViewGroup viewLayer) {
        int cx = (viewLayer.getLeft() + viewLayer.getRight()) / 2;
        int cy = (viewLayer.getTop() + viewLayer.getBottom()) / 2;
        animateRevealColorFromCoordinates(viewLayer, cx, cy);
    }

    private Animator animateRevealColorFromCoordinates(ViewGroup viewRoot, int x, int y) {
        float finalRadius = (float) Math.hypot(viewRoot.getWidth(), viewRoot.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, x, y, 0, finalRadius);
        mViewBinding.layerAnimationIopin.setVisibility(View.VISIBLE);
        anim.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
        return anim;
    }

}
