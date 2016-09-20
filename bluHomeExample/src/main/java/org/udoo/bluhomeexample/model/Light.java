package org.udoo.bluhomeexample.model;

import org.udoo.bluhomeexample.util.BindableBoolean;
import org.udoo.bluhomeexample.util.BindableString;

/**
 * Created by harlem88 on 18/07/16.
 */

public class Light {
    public BindableString value;
    public BindableBoolean detect;

    public Light() {
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