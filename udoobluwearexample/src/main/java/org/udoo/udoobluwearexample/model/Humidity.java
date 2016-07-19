package org.udoo.udoobluwearexample.model;


import org.udoo.udoobluwearexample.util.BindableBoolean;
import org.udoo.udoobluwearexample.util.BindableString;

/**
 * Created by harlem88 on 18/07/16.
 */

public class Humidity {
    public BindableString value;
    public BindableBoolean detect;

    public Humidity() {
        value = new BindableString();
        value.set("");
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public void setDetect(Boolean detect) {
        this.detect.set(detect);
    }

    public BindableBoolean getDetect() {
        return detect;
    }
}
