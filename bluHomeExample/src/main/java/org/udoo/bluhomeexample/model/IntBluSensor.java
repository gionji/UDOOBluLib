package org.udoo.bluhomeexample.model;

import android.graphics.drawable.Drawable;

/**
 * Created by harlem88 on 09/10/16.
 */

public class IntBluSensor extends BluSensor {
    public String x;
    public String y;
    public String z;

    public IntBluSensor(){
        super();
    }

    public static IntBluSensor Builder(String name, Drawable imgResource){
        IntBluSensor intBluSensor = new IntBluSensor();
        intBluSensor.name = name;
        intBluSensor.resourceImg = imgResource;
        intBluSensor.isDetect = true;
        return intBluSensor;
    }
}
