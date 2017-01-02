package org.udoo.bluhomeexample.model;

import org.udoo.udooblulib.model.IOPin;

/**
 * Created by harlem88 on 27/12/16.
 */

public class IOPinStepModel {
    public String text;
    public String step;
    public IOPin ioPin;
    public int layout;

    public static IOPinStepModel Builder(String idx, String text, int layout, IOPin ioPin){
        IOPinStepModel ioPinStepModel = new IOPinStepModel();
        ioPinStepModel.step = idx;
        ioPinStepModel.text = text;
        ioPinStepModel.layout = layout;
        ioPinStepModel.ioPin = ioPin;
        return ioPinStepModel;
    }
}
