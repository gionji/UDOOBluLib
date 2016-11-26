package org.udoo.bluhomeexample.interfaces;

import org.udoo.bluhomeexample.activity.BluActivity.ITEM_SELECTED;

/**
 * Created by harlem88 on 26/11/16.
 */

public interface IFragmentToBluActivity {
    void onBluSensorClicked(ITEM_SELECTED itemSelected);
}
