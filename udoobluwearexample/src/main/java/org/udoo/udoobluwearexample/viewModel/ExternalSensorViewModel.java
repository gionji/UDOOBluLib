package org.udoo.udoobluwearexample.viewModel;

import android.databinding.BindingAdapter;
import android.widget.TextView;

import org.udoo.udoobluwearexample.util.BindableString;

/**
 * Created by harlem88 on 19/02/16.
 */
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
}
