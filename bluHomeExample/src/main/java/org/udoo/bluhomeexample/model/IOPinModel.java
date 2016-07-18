package org.udoo.bluhomeexample.model;

import org.udoo.bluhomeexample.util.BindableInt;

/**
 * Created by harlem88 on 03/07/16.
 */

public class IOPinModel {
    public BindableInt pinMode;

    public IOPinModel(){
        pinMode = new BindableInt();
        pinMode.set(0);
    }

    public void setValue(int value) {
        pinMode.set(value);
    }

    public int getPinMode() {
        return pinMode.get();
    }
}
