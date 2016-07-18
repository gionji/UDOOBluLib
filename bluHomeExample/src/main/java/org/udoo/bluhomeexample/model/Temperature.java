package org.udoo.bluhomeexample.model;

import org.udoo.bluhomeexample.util.BindableBoolean;
import org.udoo.bluhomeexample.util.BindableString;

/**
 * Created by harlem88 on 17/02/16.
 */
public class Temperature {
    public BindableString value;
    public BindableBoolean detect;

    public Temperature() {
        value = new BindableString();
        value.set("");
        detect = new BindableBoolean();
        detect.set(false);
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
