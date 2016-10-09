package org.udoo.bluhomeexample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.udoo.bluhomeexample.databinding.HomeBluItemLayoutBinding;
import org.udoo.bluhomeexample.model.BluItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harlem88 on 16/02/16.
 */

public class BluItemAdapter extends RecyclerView.Adapter<BluItemAdapter.BluItemViewHolder> {
    private List<BluItem> mDataset;
    private ItemCLickListener mItemCLickListener;

    public void clear() {
        mDataset.clear();
        notifyDataSetChanged();
    }

    public void setItemCLickListener(ItemCLickListener itemCLickListener) {
        this.mItemCLickListener = itemCLickListener;
    }

    public void addDevice(BluItem bluItem) {
        mDataset.add(bluItem);
    }

    public void addDevices(List<BluItem> bluItems) {
        mDataset.clear();
        mDataset.addAll(bluItems);
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
    }

    @Override
    public BluItemViewHolder onCreateViewHolder(ViewGroup parent,
                                                int viewType) {
        return new BluItemViewHolder(HomeBluItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(BluItemViewHolder holder, final int position) {
        final BluItem blu = mDataset.get(position);
        if (blu != null) {
            holder.getBinding().setBlu(blu);
        }
        holder.getBinding().getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemCLickListener != null)
                    mItemCLickListener.onItemClickListener(blu);
            }
        });
//        if(blu.isConnected())
//            holder.itemBinding.getRoot().setBackgroundColor(blu.color);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}



