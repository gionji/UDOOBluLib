package org.udoo.bluhomeexample.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.udoo.bluhomeexample.interfaces.OnResult;
import org.udoo.bluhomeexample.model.BluItem;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 * Created by harlem88 on 08/10/16.
 */
public class Util {
    private static final String BLU_HOME_ADDRESS = "blu_home_address.xml";

    public static final int getColor(Context context, int id) {
        if (Build.VERSION.SDK_INT >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    public static void SaveBlusInPreferences(Context context, ArrayList<BluItem> blus) {
        SharedPreferences sharedPref = context.getSharedPreferences(BLU_HOME_ADDRESS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(blus);
        editor.putString("blus",json);
        editor.apply();
    }

    public static void LoadBlusFromPreferences(Context context, final OnResult<Map<String, BluItem>> onResult) {
        final SharedPreferences sharedPref = context.getSharedPreferences(BLU_HOME_ADDRESS, Context.MODE_PRIVATE);
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                Type type = new TypeToken<List<BluItem>>() {}.getType();
                String gsonString = sharedPref.getString("blus", "");
                List<BluItem> list = gson.fromJson(gsonString, type);
                Map<String, BluItem> map;
                if (list != null && list.size() > 0) {
                    map = new HashMap<>(list.size());
                    for (BluItem blu : list) {
                        map.put(blu.address, blu);
                    }
                } else {
                    map = new HashMap<>();
                }
                if (onResult != null)
                    onResult.onSuccess(map);
            }
        });

    }


   public static void EnterCircularReveal(View view) {

        int cx = view.getMeasuredWidth() / 2;
        int cy = view.getMeasuredHeight() / 2;

        int finalRadius = Math.max(view.getWidth(), view.getHeight()) / 2;

        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);

        view.setVisibility(View.VISIBLE);
        anim.start();
    }


    public static void ExitCircularReveal(final View view) {

        int cx = view.getMeasuredWidth() / 2;
        int cy = view.getMeasuredHeight() / 2;

        int initialRadius = view.getWidth() / 2;

        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.INVISIBLE);
            }
        });

        anim.start();
    }

}
