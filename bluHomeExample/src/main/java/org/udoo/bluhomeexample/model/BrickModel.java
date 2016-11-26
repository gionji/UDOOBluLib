package org.udoo.bluhomeexample.model;

import android.graphics.drawable.Drawable;

import org.udoo.bluhomeexample.util.BindableString;

/**
 * Created by harlem88 on 26/11/16.
 */

public class BrickModel {
    public Drawable resourceImg;
    public String name;
    public BindableString value;
    public String urlBrick;

    private BrickModel(){
        value = new BindableString();
    }

    public static BrickModel Builder(String name, Drawable resourceImg, String urlBrick){
        BrickModel brickModel =  new BrickModel();
        brickModel.name = name;
        brickModel.resourceImg = resourceImg;
        brickModel.urlBrick = urlBrick;
        return brickModel;
    }
}
