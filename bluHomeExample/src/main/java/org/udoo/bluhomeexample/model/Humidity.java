package org.udoo.bluhomeexample.model;

import org.udoo.bluhomeexample.util.BindableBoolean;
import org.udoo.bluhomeexample.util.BindableString;

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
