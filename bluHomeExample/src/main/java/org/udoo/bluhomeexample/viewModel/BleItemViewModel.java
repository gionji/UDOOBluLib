package org.udoo.bluhomeexample.viewModel;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.ImageView;

import org.udoo.bluhomeexample.R;
import org.udoo.udooblulib.utils.BindableBoolean;

/**
 * Created by harlem88 on 16/02/16.
 */
public class BleItemViewModel {

    @BindingAdapter({"connected"})
    public static void setConneted(View view, BindableBoolean isConnected) {
        if (isConnected != null) {

        }
    }

    @BindingAdapter({"rssi"})
    public static void setRssi(ImageView view, String sRrssi) {
        int rssi = Integer.parseInt(sRrssi);
        if (rssi > -55) {
            view.setImageResource(R.drawable.wifiquattro);
        } else if (rssi > -75) {
            view.setImageResource(R.drawable.wifitre);
        } else if (rssi > -85) {
            view.setImageResource(R.drawable.wifidue);
        } else if (rssi > -96) {
            view.setImageResource(R.drawable.wifiuno);
        } else {
            view.setImageResource(R.drawable.wifinone);
        }
    }
}
