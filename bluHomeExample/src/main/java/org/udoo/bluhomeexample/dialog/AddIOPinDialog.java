package org.udoo.bluhomeexample.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;

import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.databinding.AddIopinDialogBinding;
import org.udoo.bluhomeexample.interfaces.OnResult;
import org.udoo.bluhomeexample.model.BluItem;
import org.udoo.udooblulib.model.IOPin;

import static android.R.attr.id;

/**
 * Created by harlem88 on 23/11/16.
 */

public class AddIOPinDialog extends DialogFragment {

    private static final String IOPINs[] = {"A0", "A1", "A2", "A3", "A4", "A5", "D6", "D7"};
    private ArrayAdapter<String> mAdapterIOPinType;
    private AddIopinDialogBinding mViewBinding;
    private IOPin.IOPIN_PIN mIOPin;
    private IOPin.IOPIN_MODE mIOPinMode;
    private OnResult<IOPin> mOnResult;

    public void setResultCallback(OnResult<IOPin> onResult) {
        mOnResult = onResult;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mViewBinding = DataBindingUtil.inflate(inflater, R.layout.add_iopin_dialog, container, false);
        return mViewBinding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnKeyListener(onKeyListener);
        return dialog;
    }

    private DialogInterface.OnKeyListener onKeyListener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (mOnResult != null) mOnResult.onSuccess(null);
                dismiss();
                return true;
            }
            return false;
        }
    };

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapterIOPinType = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, IOPINs);
        mAdapterIOPinType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mViewBinding.spinnerIopin.setAdapter(mAdapterIOPinType);

        mViewBinding.spinnerIopin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
                String iopin = adapter.getItemAtPosition(position).toString();

                if (position > IOPINs.length - 3) {
                    mViewBinding.radioGroupIopin.clearCheck();
                    mIOPinMode = null;
                    mViewBinding.radioBtnIopinAnalog.setEnabled(false);
                    mViewBinding.btnSave.setEnabled(false);
                } else {
                    mViewBinding.radioBtnIopinAnalog.setEnabled(true);
                    mViewBinding.btnSave.setEnabled(true);
                }
                mIOPin = IOPin.IOPIN_PIN.valueOf(iopin);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        mViewBinding.radioGroupIopin.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radio_btn_iopin_out:
                        mIOPinMode = IOPin.IOPIN_MODE.DIGITAL_OUTPUT;
                        break;
                    case R.id.radio_btn_iopin_in:
                        mIOPinMode = IOPin.IOPIN_MODE.DIGITAL_INPUT;
                        break;
                    case R.id.radio_btn_iopin_analog:
                        mIOPinMode = IOPin.IOPIN_MODE.ANALOG;
                        break;
                    case R.id.radio_btn_iopin_pwm:
                        mIOPinMode = IOPin.IOPIN_MODE.PWM;
                        break;
                }
                mViewBinding.btnSave.setEnabled(true);
            }
        });

        mViewBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (mOnResult != null) {
                    mOnResult.onSuccess(IOPin.Builder(mIOPin, mIOPinMode));
                }
            }
        });
    }
}
