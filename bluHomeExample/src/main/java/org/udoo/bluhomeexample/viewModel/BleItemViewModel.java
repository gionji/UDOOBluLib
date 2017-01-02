package org.udoo.bluhomeexample.viewModel;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.ImageView;

import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.util.BindableBoolean;

/**
 * Created by harlem88 on 16/02/16.
 */
public class BleItemViewModel {

    @BindingAdapter({"connected"})
    public static void setConneted(View view, BindableBoolean isConnected) {
        if (isConnected != null) {}
    }

    @BindingAdapter({"bind:rssi", "bind:connect"})
    public static void setRssi(ImageView view, String sRrssi, boolean isConnected) {
        if (sRrssi != null) {
            int rssi = Integer.parseInt(sRrssi);
            int res ;
            if (rssi > -55) {
                res = isConnected ? R.drawable.rssi4_b : R.drawable.signal_4;
            } else if (rssi > -75) {
                res = isConnected ? R.drawable.rssi3_b : R.drawable.signal_3;
            } else if (rssi > -85) {
                res = isConnected ? R.drawable.rssi2_b : R.drawable.signal_2;
            } else if (rssi > -96) {
                res = isConnected ? R.drawable.rssi1_b : R.drawable.signal_1;
            } else {
                res = isConnected ? R.drawable.norssi_b : R.drawable.signal_none;
            }
            view.setImageResource(res);
        }
    }
}
