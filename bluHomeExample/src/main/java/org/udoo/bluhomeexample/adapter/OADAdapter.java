package org.udoo.bluhomeexample.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.databinding.OadItemLayoutBinding;
import org.udoo.bluhomeexample.model.OADModel;

import java.util.List;

/**
 * Created by harlem88 on 20/10/16.
 */

public class OADAdapter extends RecyclerView.Adapter<OADAdapter.OADViewHolder> {
    private List<OADModel> mDataSet;


    public static class OADViewHolder extends RecyclerView.ViewHolder {
        private OadItemLayoutBinding mViewBinding;

        public OADViewHolder(OadItemLayoutBinding itemView) {
            super(itemView.getRoot());
            mViewBinding = itemView;
        }

        public OadItemLayoutBinding getViewBinding() {
            return mViewBinding;
        }
    }

    public OADAdapter(List<OADModel> bluSensors) {
        mDataSet = bluSensors;
    }

    public void addOADFirmwares(List<OADModel> bluSensors) {
        mDataSet.clear();
        mDataSet.addAll(bluSensors);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSet.size();
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public OADViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OADViewHolder((OadItemLayoutBinding) DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.oad_item_layout, null, false));
    }

    @Override
    public void onBindViewHolder(OADViewHolder holder, int position) {
        OADModel oadModel = mDataSet.get(position);
        holder.getViewBinding().setOadModel(oadModel);
    }
}
