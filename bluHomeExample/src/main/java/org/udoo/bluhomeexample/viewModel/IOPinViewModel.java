package org.udoo.bluhomeexample.viewModel;

import android.provider.MediaStore;
import android.widget.RadioGroup;

import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.model.IOPinModel;

/**
 * Created by harlem88 on 03/07/16.
 */

public class IOPinViewModel {
    public IOPinModel mIoPinModel;

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int value = 0;
            switch (checkedId) {
                case R.id.iopin_dig_out:
                    value = 0;
                    break;
                case R.id.iopin_dig_in:
                    value = 1;
                    break;
                case R.id.iopin_analog_in:
                    value = 2;
                    break;
                case R.id.iopin_pwm:
                    value = 3;
                    break;
            }
            mIoPinModel.pinMode.set(value);
        }
    };



    public RadioGroup.OnCheckedChangeListener getOnCheckedChangeListener() {
        return mOnCheckedChangeListener;
    }
}
