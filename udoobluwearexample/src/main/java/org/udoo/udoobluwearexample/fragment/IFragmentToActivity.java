package org.udoo.udoobluwearexample.fragment;

import org.udoo.udooblulib.exceptions.UdooBluException;
import org.udoo.udoobluwearexample.model.BluItem;

import java.util.Observer;

/**
 * Created by harlem88 on 19/07/16.
 */
public interface IFragmentToActivity {
    void onBluError(UdooBluException e);
    void onConnect(BluItem bluItem);
}
