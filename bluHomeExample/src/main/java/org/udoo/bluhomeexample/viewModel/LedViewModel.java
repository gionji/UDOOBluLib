package org.udoo.bluhomeexample.viewModel;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import org.udoo.bluhomeexample.util.BindableBoolean;
import org.udoo.bluhomeexample.util.BindableInt;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by harlem88 on 09/02/16.
 */
public class LedViewModel {
    private static Map<Integer, ObjectAnimator> animation;


    @BindingAdapter({"blink", "color"})
    public static void blinkColor(View ledVIew, BindableBoolean isBlink, BindableInt color) {
        if (isBlink.get()) {
            if (animation == null) {
                animation = new HashMap<>();
            }

            ObjectAnimator objectAnimator = ObjectAnimator.ofObject(ledVIew, "backgroundColor", new ArgbEvaluator(), color.get(), Color.WHITE);
            objectAnimator.setDuration(1000);
            objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
            objectAnimator.setInterpolator(new AccelerateInterpolator());
            objectAnimator.start();
            animation.put(color.get(), objectAnimator);
            ledVIew.setAlpha(1f);
        } else {
            if (color != null) {
                ledVIew.setBackgroundColor(color.get());
                if (animation != null && animation.containsKey(color.get()))
                    animation.get(color.get()).cancel();
            }
            ledVIew.setAlpha(0.2f);
        }
    }


    @BindingAdapter({"onoff"})
    public static void setOnOff(View ledVIew, BindableBoolean isOnOff) {
        if (isOnOff != null) {
            ledVIew.setAlpha(isOnOff.get() ? 1f : 0.2f);
        }
    }
}
