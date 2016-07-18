package org.udoo.bluhomeexample.viewModel;

/**
 * Created by harlem88 on 29/06/16.
 */

import android.databinding.BindingAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.udoo.bluhomeexample.util.BindableBoolean;
import org.udoo.bluhomeexample.util.BindableString;

public class ExternalSensorViewModel {

    @BindingAdapter({"temperature"})
    public static void setTemperature(TextView view, BindableString value) {
        if (value != null) {
            view.setText(value.get() + "°");
        }
    }

    @BindingAdapter({"barometer"})
    public static void setBarometer(TextView view, BindableString value) {
        if (value != null) {
            view.setText(value.get() + " kPa");
        }
    }

    @BindingAdapter({"detect"})
    public static void setDetect(RelativeLayout view, BindableBoolean value) {
        if (value != null) {
            view.setAlpha(value.get() ? 1 : 0.1f);
        }
    }
}
