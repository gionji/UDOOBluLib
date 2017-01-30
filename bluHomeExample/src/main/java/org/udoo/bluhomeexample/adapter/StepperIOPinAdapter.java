package org.udoo.bluhomeexample.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.databinding.IopinInputOutputLayoutBinding;
import org.udoo.bluhomeexample.databinding.IopinPwmRowLayoutBinding;
import org.udoo.bluhomeexample.databinding.IopinStepperItemLayoutBinding;
import org.udoo.bluhomeexample.model.BluSensor;
import org.udoo.bluhomeexample.model.IOPinStepModel;
import org.udoo.udooblulib.model.IOPin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harlem88 on 27/12/16.
 */

public class StepperIOPinAdapter extends RecyclerView.Adapter<StepperIOPinAdapter.StepperIOPinViewHolder> {
    private List<IOPinStepModel> mDataSet;
    private String[] mStepperTexts;
    private IOnStepperIOPin mIOnStepperIOPin;

    public interface IOnStepperIOPin {
        void onStepperIOPinDone(boolean done, int pos);
    }

    public void setIOnStepperIOPin(IOnStepperIOPin iOnStepperIOPin) {
        mIOnStepperIOPin = iOnStepperIOPin;
    }

    public static class StepperIOPinViewHolder extends RecyclerView.ViewHolder {
        private IopinStepperItemLayoutBinding mViewBinding;

        StepperIOPinViewHolder(IopinStepperItemLayoutBinding itemView) {
            super(itemView.getRoot());
            mViewBinding = itemView;
        }
    }

    public StepperIOPinAdapter(Context context) {
        mDataSet = new ArrayList<>();
        mStepperTexts = context.getResources().getStringArray(R.array.stepper_io_array);
        buildNextStep(0);
    }

    public void addSteppers(List<BluSensor> bluSensors) {
        mDataSet.clear();
        notifyDataSetChanged();
    }

    private void addIOPinStep(IOPinStepModel ioPinStepModel) {
        mDataSet.add(ioPinStepModel);
        notifyItemInserted(mDataSet.size() - 1);
    }

    private void removeIOPinStep(int position) {
        mDataSet.remove(position);
        if (position <= 1) {
            notifyItemRemoved(position);
            notifyDataSetChanged();
        } else {
            notifyItemRemoved(position);
            notifyItemRangeChanged(position - 1, position);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public StepperIOPinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StepperIOPinViewHolder((IopinStepperItemLayoutBinding) DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.iopin_stepper_item_layout, null, false));
    }

    @Override
    public void onBindViewHolder(final StepperIOPinViewHolder holder, final int position) {

        IOPinStepModel ioPinStepModel = mDataSet.get(position);
        holder.mViewBinding.setStepModel(ioPinStepModel);
        holder.mViewBinding.btnStepperCancel.setVisibility(View.VISIBLE);
        holder.mViewBinding.btnStepperContinue.setVisibility(View.VISIBLE);


        holder.mViewBinding.stepperContainer.removeAllViews();

        if (ioPinStepModel.layout > 0) {
            holder.mViewBinding.stepperContainer.setVisibility(View.VISIBLE);
            View view;
            if (ioPinStepModel.ioPin != null) {
                ViewDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(holder.mViewBinding.getRoot().getContext()), ioPinStepModel.layout, null, false);
                if (viewDataBinding instanceof IopinInputOutputLayoutBinding) {
                    ((IopinInputOutputLayoutBinding) (viewDataBinding)).setIopin(ioPinStepModel.ioPin);
                    ((IopinInputOutputLayoutBinding) (viewDataBinding)).divider.setVisibility(View.GONE);
                } else if (viewDataBinding instanceof IopinPwmRowLayoutBinding) {
                    ((IopinPwmRowLayoutBinding) (viewDataBinding)).setIopin(ioPinStepModel.ioPin);
                    ((IopinPwmRowLayoutBinding) (viewDataBinding)).divider.setVisibility(View.GONE);
                }
                view = viewDataBinding.getRoot();
            } else {
                view = LayoutInflater.from(holder.mViewBinding.getRoot().getContext()).inflate(ioPinStepModel.layout, null);
            }
            view.setAlpha(0.8f);
            holder.mViewBinding.stepperContainer.addView(view);
        } else {
            holder.mViewBinding.stepperContainer.setVisibility(View.GONE);
        }

        holder.mViewBinding.btnStepperContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIOnStepperIOPin != null)
                    mIOnStepperIOPin.onStepperIOPinDone(position == (mStepperTexts.length - 1), mDataSet.size());

                buildNextStep(position + 1);
                v.setVisibility(View.INVISIBLE);
                holder.mViewBinding.btnStepperCancel.setVisibility(View.INVISIBLE);
            }
        });

        holder.mViewBinding.btnStepperCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeIOPinStep(position);
            }
        });
        holder.mViewBinding.stepperContainer.setOnTouchListener(mOnTouchListener);
    }

    private void buildNextStep(int position) {
        IOPinStepModel ioPinStepModel = null;
        switch (position) {
            case 0:
                ioPinStepModel = IOPinStepModel.Builder("1", mStepperTexts[position], -1, null);
                break;
            case 1:
                ioPinStepModel = IOPinStepModel.Builder("2", mStepperTexts[position], R.layout.stepper_iopin_dialog_layout, null);
                break;
            case 2:
                ioPinStepModel = IOPinStepModel.Builder("3.1", mStepperTexts[position], R.layout.iopin_input_output_layout, IOPin.Builder(IOPin.IOPIN_PIN.A0, IOPin.IOPIN_MODE.DIGITAL_OUTPUT));
                break;
            case 3:
                ioPinStepModel = IOPinStepModel.Builder("3.2", mStepperTexts[position], R.layout.iopin_input_output_layout, IOPin.Builder(IOPin.IOPIN_PIN.A0, IOPin.IOPIN_MODE.DIGITAL_INPUT));
                break;
            case 4:
                ioPinStepModel = IOPinStepModel.Builder("3.3", mStepperTexts[position], R.layout.iopin_input_output_layout, IOPin.Builder(IOPin.IOPIN_PIN.A0, IOPin.IOPIN_MODE.ANALOG));
                break;
            case 5:
                ioPinStepModel = IOPinStepModel.Builder("3.4", mStepperTexts[position], R.layout.iopin_pwm_row_layout, IOPin.Builder(IOPin.IOPIN_PIN.A0, IOPin.IOPIN_MODE.PWM));
                break;
            case 6:
                ioPinStepModel = IOPinStepModel.Builder("4", mStepperTexts[position], -1, null);
                break;


        }
        if (ioPinStepModel != null) addIOPinStep(ioPinStepModel);
    }

    public final View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent rawEvent) {
            return false;
        }
    };
}