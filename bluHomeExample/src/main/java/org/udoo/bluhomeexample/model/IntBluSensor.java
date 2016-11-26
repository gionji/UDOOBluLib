package org.udoo.bluhomeexample.model;

import android.graphics.drawable.Drawable;

import org.udoo.bluhomeexample.activity.BluActivity.ITEM_SELECTED;

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

    public static IntBluSensor Builder(String name, Drawable imgResource, ITEM_SELECTED item_selected){
        IntBluSensor intBluSensor = new IntBluSensor();
        intBluSensor.name = name;
        intBluSensor.resourceImg = imgResource;
        intBluSensor.isDetect = true;
        intBluSensor.itemSelected = item_selected;
        return intBluSensor;
    }
}
