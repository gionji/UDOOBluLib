package org.udoo.bluhomeexample;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.udoo.bluhomeexample.databinding.HomeBluItemLayoutBinding;
import org.udoo.bluhomeexample.model.BluItem;
import org.udoo.bluhomeexample.model.Led;
import org.udoo.udooblulib.sensor.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.udoo.udooblulib.sensor.Constant.GREEN_LED;
import static org.udoo.udooblulib.sensor.Constant.RED_LED;
import static org.udoo.udooblulib.sensor.Constant.YELLOW_LED;

/**
 * Created by harlem88 on 16/02/16.
 */

public class BluItemAdapter extends RecyclerView.Adapter<BluItemAdapter.BluItemViewHolder> {
    private List<BluItem> mDataset;
    private ItemCLickListener mItemCLickListener;
    private Map<String, Led[]> ledItemMap;

    public void clear() {
        mDataset.clear();
        ledItemMap.clear();
        notifyDataSetChanged();
    }

    public void setItemCLickListener(ItemCLickListener itemCLickListener) {
        this.mItemCLickListener = itemCLickListener;
    }

    public void addDevice(BluItem bluItem) {
        mDataset.add(bluItem);
        ledItemMap.put(bluItem.address, buildLeds());
    }

    public void addDevices(List<BluItem> bluItems) {
        mDataset.clear();
        mDataset.addAll(bluItems);

        for(BluItem bluItem : mDataset){
            ledItemMap.put(bluItem.address, buildLeds());
        }
    }

    public void updateDevice(BluItem bluItem) {
        for(int i = 0; i<mDataset.size(); i++) {
            if(mDataset.get(i).address.equalsIgnoreCase(bluItem.address)){
                mDataset.remove(i);
                mDataset.add(i, bluItem);
                notifyItemChanged(i);
                i = mDataset.size();
            }
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

    public BluItemAdapter(List<BluItem> myDataset) {
        mDataset = myDataset;
        ledItemMap = new HashMap<>();

        for(BluItem bluItem : mDataset){
            ledItemMap.put(bluItem.address, buildLeds());
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
//        if(blu.isConnected())
//            holder.itemBinding.getRoot().setBackgroundColor(blu.color);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private View.OnClickListener ledClickListener(final BluItem bluItem, final Led led){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mItemCLickListener != null){
                    led.blink.set(!led.blink.get());
                    mItemCLickListener.onItemLedClickListener(bluItem, led);
                }
            }
        };
    }


    private Led[] buildLeds(){
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
}



