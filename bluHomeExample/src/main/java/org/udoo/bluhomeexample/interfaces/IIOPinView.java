package org.udoo.bluhomeexample.interfaces;

import org.udoo.udooblulib.model.IOPin;

/**
 * Created by harlem88 on 23/11/16.
 */

public interface IIOPinView {
    void addIOPin(IOPin ioPin);
    void showProgress(boolean show);
    void updateIOPinDigital(IOPin ioPin);
    void updateIOPinAnalog(IOPin ioPin);
}
