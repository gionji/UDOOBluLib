package org.udoo.udoobluwearexample.model;

import android.graphics.Color;

import org.udoo.udoobluwearexample.util.BindableBoolean;
import org.udoo.udoobluwearexample.util.BindableInt;

public class Led{

    public BindableInt color;
    public BindableBoolean onoff;
    public BindableBoolean blink;

    public Led(){
        onoff = new BindableBoolean();
        blink = new BindableBoolean();
        color = new BindableInt();
    }

    public static Led BuilderDefault(){
        Led led = new Led();
        led.color.set(Color.WHITE);
        led.blink.set(false);
        led.onoff.set(false);
        return led;
    }

    public static Led Builder(byte status, int color){
        Led led = new Led();

        if(status == 2){
            led.blink.set(true);
            led.onoff.set(true);
        }
        else if (status == 1){
            led.blink.set(false);
            led.onoff.set(true);
        }else {
            led.blink.set(false);
            led.onoff.set(true);
        }
        led.color.set(color);
        return led;
    }


}
