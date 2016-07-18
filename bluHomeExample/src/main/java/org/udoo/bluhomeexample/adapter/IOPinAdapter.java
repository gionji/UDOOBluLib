package org.udoo.bluhomeexample.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.databinding.IopinLayoutRowBinding;

/**
 * Created by harlem88 on 04/07/16.
 */

public class IOPinAdapter extends RecyclerView.Adapter<IOPinAdapter.IOPinViewHolder> {

    @Override
    public IOPinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new IOPinViewHolder((IopinLayoutRowBinding) DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.iopin_layout_row, parent, false));
    }

    @Override
    public void onBindViewHolder(IOPinViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class IOPinViewHolder extends RecyclerView.ViewHolder {
        public IopinLayoutRowBinding itemBinding;

        public IOPinViewHolder(IopinLayoutRowBinding fragmentIopinsItemBinding) {
            super(fragmentIopinsItemBinding.getRoot());
            itemBinding = fragmentIopinsItemBinding;
        }

        public IopinLayoutRowBinding getBinding() {
            return itemBinding;
        }
    }

}
