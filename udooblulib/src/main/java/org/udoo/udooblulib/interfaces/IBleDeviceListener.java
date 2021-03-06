package org.udoo.udooblulib.interfaces;


import org.udoo.udooblulib.exceptions.UdooBluException;

/**
 * Created by harlem88 on 17/02/16.
 */
public interface IBleDeviceListener {
    void onDeviceConnected();
    void onDeviceDisconnect();
    void onError(UdooBluException runtimeException);
}
