package org.udoo.bluhomeexample.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import org.udoo.bluhomeexample.databinding.FragmentLedManagerItemBinding;
import org.udoo.bluhomeexample.model.Led;

/**
 * Created by harlem88 on 09/02/16.
 */

public class LedAdapter extends RecyclerView.Adapter<LedAdapter.LedViewHolder> {
    private Led[] mDataset;
    private ItemCLickListner mItemCLickListner;

    public void setItemCLickListner(ItemCLickListner itemCLickListner) {
        this.mItemCLickListner = itemCLickListner;
    }

    public interface ItemCLickListner {
        void onItemClickListener(int pos);
        void onBlinkListener(int pos, boolean blink);
    }

    public static class LedViewHolder extends RecyclerView.ViewHolder {
        public FragmentLedManagerItemBinding itemBinding;

        public LedViewHolder(FragmentLedManagerItemBinding fragmentLedManagerItemBinding) {
//            super(fragmentLedManagerItemBinding);
            super(null);
            itemBinding = fragmentLedManagerItemBinding;
        }

        public FragmentLedManagerItemBinding getBinding() {
            return itemBinding;
        }
    }

    public LedAdapter(Led[] myDataset) {
        mDataset = myDataset;
    }

    @Override
    public LedViewHolder onCreateViewHolder(ViewGroup parent,
                                            int viewType) {
        return new LedViewHolder(FragmentLedManagerItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(LedViewHolder holder, final int position) {
        Led led = mDataset[position];
        if (led != null) {
            holder.getBinding().setLed(led);
        }else
            holder.getBinding().setLed(led);

        holder.getBinding().ledImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemCLickListner != null)
                    mItemCLickListner.onItemClickListener(position);
            }
        });

        holder.getBinding().switchBlink.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mItemCLickListner != null)
                    mItemCLickListner.onBlinkListener(position, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}

