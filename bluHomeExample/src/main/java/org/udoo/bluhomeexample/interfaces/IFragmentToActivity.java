package org.udoo.bluhomeexample.interfaces;

import org.udoo.bluhomeexample.model.BluItem;
import org.udoo.udooblulib.exceptions.UdooBluException;

/**
 * Created by harlem88 on 27/06/16.
 */

public interface IFragmentToActivity {
    void onBluError(UdooBluException e);
    void onConnect(BluItem bluItem);
}
