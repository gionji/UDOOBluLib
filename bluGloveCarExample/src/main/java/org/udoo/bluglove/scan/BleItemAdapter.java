package org.udoo.bluglove.scan;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.udoo.bluglove.databinding.FragmentBleScanItemBinding;
import org.udoo.udooblulib.model.BleItem;

import java.util.List;

/**
 * Created by harlem88 on 16/02/16.
 */

public class BleItemAdapter extends RecyclerView.Adapter<BleItemAdapter.BleItemViewHolder> {
    private List<BleItem> mDataset;
    private ItemCLickListner mItemCLickListner;

    public void clear(){
        mDataset.clear();
        notifyDataSetChanged();
    }
    public void setItemCLickListner(ItemCLickListner itemCLickListner) {
        this.mItemCLickListner = itemCLickListner;
    }

    public void addDevice(BleItem bleItem) {
        mDataset.add(bleItem);
    }

    public interface ItemCLickListner {
        void onItemClickListener(BleItem bleItem);
    }

    public static class BleItemViewHolder extends RecyclerView.ViewHolder {
        public FragmentBleScanItemBinding itemBinding;

        public BleItemViewHolder(FragmentBleScanItemBinding fragmentBleScanItemBinding) {
            super(fragmentBleScanItemBinding.root);
            itemBinding = fragmentBleScanItemBinding;
        }

        public FragmentBleScanItemBinding getBinding() {
            return itemBinding;
        }
    }

    public BleItemAdapter(List<BleItem> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public BleItemViewHolder onCreateViewHolder(ViewGroup parent,
                                                int viewType) {
        return new BleItemViewHolder(FragmentBleScanItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(BleItemViewHolder holder, final int position) {
        final BleItem ble = mDataset.get(position);
        if (ble != null) {
            holder.getBinding().setBleItem(ble);
        }
        holder.getBinding().root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemCLickListner != null)
                    mItemCLickListner.onItemClickListener(ble);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}



