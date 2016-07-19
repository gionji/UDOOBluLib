package org.udoo.udoobluwearexample.model;

import org.udoo.udoobluwearexample.util.BindableInt;

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
