package org.udoo.bluhomeexample.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.stringtemplate.v4.misc.STModelAdaptor;
import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.databinding.ExtSensorItemBinding;
import org.udoo.bluhomeexample.databinding.IntSensorItemBinding;
import org.udoo.bluhomeexample.databinding.IopinInputOutputLayoutBinding;
import org.udoo.bluhomeexample.databinding.IopinLayoutRowBinding;
import org.udoo.bluhomeexample.databinding.IopinPwmRowLayoutBinding;
import org.udoo.bluhomeexample.model.BluSensor;
import org.udoo.udooblulib.model.IOPin;

import java.util.List;

/**
 * Created by harlem88 on 04/07/16.
 */

public class IOPinAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int VIEW_IOA_TYPE = 0, VIEW_PWM_TYPE = 1;
    private List<IOPin> mDataSet;
    private IIOPinValueCallback mIioPinValueCallback;
    private static final int FREQ_MULT = 1000;

    public interface IIOPinValueCallback{
        void onDigitalOutputPinValueListener(IOPin.IOPIN_PIN pin, IOPin.IOPIN_DIGITAL_VALUE value);
        void onPwmPinListener(IOPin.IOPIN_PIN pin, int freq, int duty);
    }

    public void setIioPinValueCallback(IIOPinValueCallback ioPinValueCallback) {
        this.mIioPinValueCallback = ioPinValueCallback;
    }

    private class IOAViewHolder extends RecyclerView.ViewHolder {
        private IopinInputOutputLayoutBinding mViewBinding;

        IOAViewHolder(IopinInputOutputLayoutBinding itemView) {
            super(itemView.getRoot());
            mViewBinding = itemView;
        }
    }

    private class PWMViewHolder extends RecyclerView.ViewHolder {
        private IopinPwmRowLayoutBinding mViewBinding;

        PWMViewHolder(IopinPwmRowLayoutBinding itemView) {
            super(itemView.getRoot());
            mViewBinding = itemView;
        }
    }

    public IOPinAdapter(List<IOPin> ioPins){
        mDataSet = ioPins;
    }

    public void addIOPin(IOPin ioPin){
        for(int i =0; i< mDataSet.size(); i++){
            if(mDataSet.get(i).pin == ioPin.pin)
                mDataSet.remove(i);
        }

        mDataSet.add(ioPin);
        notifyDataSetChanged();
    }

    public void remove(int pos){
        mDataSet.remove(pos);
        notifyItemRemoved(pos);
    }

    public void addIOPins(List<IOPin> ioPins){
        mDataSet.clear();
        mDataSet.addAll(ioPins);
        notifyDataSetChanged();
    }

    public void updateIOPinDigital(IOPin ioPin) {
        IOPin tmpPin;
        for (int i = 0; i < mDataSet.size(); i++) {
            tmpPin = mDataSet.get(i);
            if (tmpPin.mode.compareTo(IOPin.IOPIN_MODE.DIGITAL_INPUT) == 0 &&
                    tmpPin.pin == ioPin.pin) {
                tmpPin.digitalValue = ioPin.digitalValue;
                notifyItemChanged(i);

            }
        }
    }

    public void updateIOPinAnalog(IOPin ioPin) {
        IOPin tmpPin;
        for (int i = 0; i < mDataSet.size(); i++) {
            tmpPin = mDataSet.get(i);
            if (tmpPin.mode.compareTo(IOPin.IOPIN_MODE.ANALOG) == 0 &&
                    tmpPin.pin == ioPin.pin) {
                tmpPin.analogValue = ioPin.analogValue;
                notifyItemChanged(i);

            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewType == VIEW_IOA_TYPE ? new IOAViewHolder((IopinInputOutputLayoutBinding) DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.iopin_input_output_layout, null, false)) :
                new PWMViewHolder((IopinPwmRowLayoutBinding) DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.iopin_pwm_row_layout, null, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final IOPin ioPin = mDataSet.get(position);
        if (ioPin.mode.compareTo(IOPin.IOPIN_MODE.PWM) == 0) {
            final PWMViewHolder pwmViewHolder = ((PWMViewHolder)holder);
            pwmViewHolder.mViewBinding.setIopin(ioPin);

            pwmViewHolder.mViewBinding.seekBarDutyCycle.setOnSeekBarChangeListener(setSeekbar(pwmViewHolder.mViewBinding.titleValueDuty, " %", 1));
            pwmViewHolder.mViewBinding.seekBarFrequency.setOnSeekBarChangeListener(setSeekbar(pwmViewHolder.mViewBinding.titleValueFreq, " Hz", FREQ_MULT));

            pwmViewHolder.mViewBinding.btnSyncPwm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setOnPwmChanged(ioPin.pin, pwmViewHolder.mViewBinding.seekBarFrequency, pwmViewHolder.mViewBinding.seekBarDutyCycle);
                }
            });
        } else {
            final IOAViewHolder ioaViewHolder = ((IOAViewHolder)holder);
            ioaViewHolder.mViewBinding.setIopin(ioPin);
            ioaViewHolder.mViewBinding.switchValue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    setOnDigitalOutputChanged(ioPin.pin, b ? IOPin.IOPIN_DIGITAL_VALUE.HIGH : IOPin.IOPIN_DIGITAL_VALUE.LOW);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSet.get(position).mode.compareTo(IOPin.IOPIN_MODE.PWM)
                == 0 ? VIEW_PWM_TYPE : VIEW_IOA_TYPE;
    }

    private SeekBar.OnSeekBarChangeListener setSeekbar(final TextView textView, final String append, final int multiply){
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textView.setText(i * multiply + append);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };
    }

    private void setOnPwmChanged(IOPin.IOPIN_PIN pin, SeekBar freqBar, SeekBar dutyBar){
        if(mIioPinValueCallback != null)
            mIioPinValueCallback.onPwmPinListener(pin, freqBar.getProgress() * FREQ_MULT, dutyBar.getProgress());
    }

    private void setOnDigitalOutputChanged(IOPin.IOPIN_PIN pin, IOPin.IOPIN_DIGITAL_VALUE value){
        if(mIioPinValueCallback != null)
            mIioPinValueCallback.onDigitalOutputPinValueListener(pin, value);
    }
}
