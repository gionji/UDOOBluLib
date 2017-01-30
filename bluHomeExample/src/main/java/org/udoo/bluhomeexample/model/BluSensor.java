package org.udoo.bluhomeexample.model;

import android.graphics.drawable.Drawable;

import org.udoo.bluhomeexample.activity.BluActivity.ITEM_SELECTED;

/**
 * Created by harlem88 on 09/10/16.
 */

public class BluSensor {
    public String name;
    public Drawable resourceImg;
    public boolean isDetect;
    public String value;
    public ITEM_SELECTED itemSelected;
    public String urlShop;

    public BluSensor(){}

    public static BluSensor Builder(String name, Drawable imgResource, boolean isDetect, ITEM_SELECTED itemSelected, String urlShop){
        BluSensor bluSensor = new BluSensor();
        bluSensor.name = name;
        bluSensor.resourceImg = imgResource;
        bluSensor.isDetect = isDetect;
        bluSensor.itemSelected = itemSelected;
        bluSensor.urlShop = urlShop;
        return bluSensor;
    }
}
