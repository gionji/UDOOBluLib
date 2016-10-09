package org.udoo.bluhomeexample.model;

import android.graphics.drawable.Drawable;

/**
 * Created by harlem88 on 09/10/16.
 */

public class BluSensor {
    public String name;
    public Drawable resourceImg;
    public boolean isDetect;
    public String value;

    public BluSensor(){}

    public static BluSensor Builder(String name, Drawable imgResource, boolean isDetect){
        BluSensor bluSensor = new BluSensor();
        bluSensor.name = name;
        bluSensor.resourceImg = imgResource;
        bluSensor.isDetect = isDetect;
        return bluSensor;
    }
}
