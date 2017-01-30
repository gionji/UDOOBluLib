package org.udoo.bluhomeexample;

import android.animation.Animator;
import android.content.res.ColorStateList;
import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import org.udoo.bluhomeexample.databinding.HomeBluItemLayoutBinding;
import org.udoo.bluhomeexample.model.BluItem;
import org.udoo.bluhomeexample.model.Led;
import org.udoo.udooblulib.sensor.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by harlem88 on 16/02/16.
 */

public class BluItemAdapter extends RecyclerView.Adapter<BluItemAdapter.BluItemViewHolder> {
    private List<BluItem> mDataset;
    private ItemCLickListener mItemCLickListener;
    private Map<String, Led[]> ledItemMap;

    public BluItemAdapter(List<BluItem> myDataset) {
        mDataset = new ArrayList<>(myDataset.size());

        for(BluItem bluItem : myDataset)
            mDataset.add(BluItem.Builder(bluItem));

        ledItemMap = new HashMap<>();
        Collections.sort(mDataset, BluItemComparator);

        for (BluItem bluItem : mDataset) {
            ledItemMap.put(bluItem.address, buildLeds());
        }

    }

    public void clear() {
        mDataset.clear();
        ledItemMap.clear();
        notifyDataSetChanged();
    }

    public void setItemCLickListener(ItemCLickListener itemCLickListener) {
        this.mItemCLickListener = itemCLickListener;
    }

    public void addDevice(BluItem bluItem) {
        mDataset.add(BluItem.Builder(bluItem));
        Collections.sort(mDataset, BluItemComparator);
        notifyDataSetChanged();

        ledItemMap.put(bluItem.address, buildLeds());
    }

    public void addDevices(List<BluItem> bluItems) {
        clear();
        mDataset = new ArrayList<>(bluItems.size());

        for(BluItem bluItem : bluItems)
            mDataset.add(BluItem.Builder(bluItem));

        Collections.sort(mDataset, BluItemComparator);
        notifyDataSetChanged();

        for (BluItem bluItem : mDataset) {
            ledItemMap.put(bluItem.address, buildLeds());
        }
    }

    public void updateDevice(BluItem bluItem) {
        boolean reorder = false;
        int idx = 0;
        for (int i = 0; i < mDataset.size(); i++) {
            if (mDataset.get(i).address.equalsIgnoreCase(bluItem.address)) {
                BluItem tmpBlu = mDataset.get(i);
                reorder = (bluItem.isFound() != tmpBlu.isFound()) || (bluItem.isConnected() != tmpBlu.isConnected());
                mDataset.remove(i);
                mDataset.add(i, BluItem.Builder(bluItem));
                idx = i;
                i = mDataset.size();
            }
        }
        if (reorder) {
            Collections.sort(mDataset, BluItemComparator);
            notifyDataSetChanged();
        }else {
            notifyItemChanged(idx);
        }
    }

    public interface ItemCLickListener {
        void onItemClickListener(BluItem bluItem);

        void onItemLedClickListener(BluItem bluItem, Led led);
    }

    public static class BluItemViewHolder extends RecyclerView.ViewHolder {

        public BluItemViewHolder(View itemView) {
            super(itemView);
        }

        public HomeBluItemLayoutBinding itemBinding;

        public BluItemViewHolder(HomeBluItemLayoutBinding viewBind) {
            super(viewBind.getRoot());
            itemBinding = viewBind;
        }

        public HomeBluItemLayoutBinding getBinding() {
            return itemBinding;
        }
    }

    @Override
    public BluItemViewHolder onCreateViewHolder(ViewGroup parent,
                                                int viewType) {
        return new BluItemViewHolder(HomeBluItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final BluItemViewHolder holder, final int position) {
        final BluItem blu = mDataset.get(position);
        HomeBluItemLayoutBinding binding = holder.getBinding();
        if (blu != null) {
            binding.setBlu(blu);
            Led[] leds = ledItemMap.get(blu.address);

            binding.setGreenLed(leds[0]);
            binding.setYellowLed(leds[1]);
            binding.setRedLed(leds[2]);

            binding.llBluItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemCLickListener != null)
                        mItemCLickListener.onItemClickListener(blu);
                }
            });

            binding.btnLedGreen.setOnClickListener(ledClickListener(blu, leds[0]));
            binding.btnLedYellow.setOnClickListener(ledClickListener(blu, leds[1]));
            binding.btnLedRed.setOnClickListener(ledClickListener(blu, leds[2]));
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private View.OnClickListener ledClickListener(final BluItem bluItem, final Led led) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemCLickListener != null) {
                    led.blink.set(!led.blink.get());
                    mItemCLickListener.onItemLedClickListener(bluItem, led);
                }
            }
        };
    }

    private Led[] buildLeds() {
        Led[] leds = new Led[3];
        Led led = Led.BuilderDefault();
        led.color.set(Color.GREEN);
        led.led = Constant.GREEN_LED;
        leds[0] = led;
        led = Led.BuilderDefault();
        led.color.set(Color.YELLOW);
        led.led = Constant.YELLOW_LED;
        leds[1] = led;
        led = Led.BuilderDefault();
        led.color.set(Color.RED);
        led.led = Constant.RED_LED;
        leds[2] = led;
        return leds;
    }

    public static Comparator<BluItem> BluItemComparator
            = new Comparator<BluItem>() {

        public int compare(BluItem blu1, BluItem blu2) {
            if (blu1.isConnected() && blu2.isConnected()) {
                return blu1.name.compareTo(blu2.name);
            }
            if (blu1.isConnected()) return -1;
            else if (blu2.isConnected()) return 1;
            else {
                if (blu1.isFound() && blu2.isFound()) {
                    return blu1.name.compareTo(blu2.name);
                }
                if (blu1.isFound()) return -1;
                else if (blu2.isFound()) return 1;

                return blu1.name.compareTo(blu2.name);
            }
        }

    };

    @BindingAdapter("bluState")
    public static void setBluState(ViewGroup view, BluItem bluItem) {
        if (bluItem.isConnected()) {
            animateRevealColor(view, bluItem.color);
        } else {
            view.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), android.R.color.white));
        }
    }

    private static void animateRevealColor(ViewGroup viewLayer, int color) {
        int cx = (viewLayer.getLeft() + viewLayer.getRight()) / 2;
        int cy = (viewLayer.getTop() + viewLayer.getBottom()) / 2;
        animateRevealColorFromCoordinates(viewLayer, cx, cy, color);
    }

    private static void animateRevealColorFromCoordinates(ViewGroup viewRoot, int x, int y, int color) {
        float finalRadius = (float) Math.hypot(viewRoot.getWidth(), viewRoot.getHeight());
        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, x, y, 0, finalRadius);
        viewRoot.setBackgroundTintList(ColorStateList.valueOf(color));
        anim.setDuration(600);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();

    }
}