package org.udoo.bluhomeexample.view;

import android.view.View;
import android.widget.TextView;

import org.udoo.bluhomeexample.R;

/**
 * Created by harlem88 on 28/06/16.
 */

public class ViewHolderHeader {

    private TextView mName;

    public ViewHolderHeader(View v) {
        mName = (TextView) v.findViewById(R.id.sensor_name);
    }
}