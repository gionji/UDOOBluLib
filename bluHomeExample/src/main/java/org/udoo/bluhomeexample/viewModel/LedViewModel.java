package org.udoo.bluhomeexample.viewModel;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;

import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.util.BindableBoolean;
import org.udoo.bluhomeexample.util.BindableInt;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by harlem88 on 09/02/16.
 */
public class LedViewModel {
    private static Map<Integer, ObjectAnimator> animation;


    @BindingAdapter({"blink"})
    public static void blinkColor(final View ledVIew, BindableBoolean isBlink) {
        if (isBlink.get()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation animation = new AlphaAnimation(1, 0);
                    animation.setDuration(300);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.setRepeatCount(Animation.INFINITE);
                    animation.setRepeatMode(Animation.REVERSE);
                    ledVIew.startAnimation(animation);
                }
            }, 100);
        } else {
            ledVIew.clearAnimation();
        }
    }


    @BindingAdapter({"onoff"})
    public static void setOnOff(View ledVIew, BindableBoolean isOnOff) {
        if (isOnOff != null) {
            ledVIew.setAlpha(isOnOff.get() ? 1f : 0.2f);
        }
    }
}
