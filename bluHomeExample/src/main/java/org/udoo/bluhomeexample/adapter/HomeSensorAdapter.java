package org.udoo.bluhomeexample.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.activity.BluActivity.ITEM_SELECTED;
import org.udoo.bluhomeexample.databinding.ExtSensorItemBinding;
import org.udoo.bluhomeexample.databinding.IntSensorItemBinding;
import org.udoo.bluhomeexample.interfaces.ITouchOnList;
import org.udoo.bluhomeexample.model.BluSensor;
import org.udoo.bluhomeexample.model.IntBluSensor;

import java.util.List;

/**
 * Created by harlem88 on 09/10/16.
 */

public class HomeSensorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int VIEW_EXT_TYPE = 0, VIEW_INT_TYPE = 1;
    private final static int LAST_EXT_POS = 4;
    private List<BluSensor> mDataSet;
    private ITouchOnList<BluSensor> mITouchOnList;

    public void setITouchOnList(ITouchOnList<BluSensor> iTouchOnList) {
        mITouchOnList = iTouchOnList;
    }

    private class ExternalSensorViewHolder extends RecyclerView.ViewHolder {
        private ExtSensorItemBinding mViewBinding;

        ExternalSensorViewHolder(ExtSensorItemBinding itemView) {
            super(itemView.getRoot());
            mViewBinding = itemView;
        }
    }

    private class InternalSensorViewHolder extends RecyclerView.ViewHolder {
        private IntSensorItemBinding mViewBinding;

        InternalSensorViewHolder(IntSensorItemBinding itemView) {
            super(itemView.getRoot());
            mViewBinding = itemView;
        }
    }

    public HomeSensorAdapter(List<BluSensor> bluSensors) {
        mDataSet = bluSensors;
    }

    public void addSensors(List<BluSensor> bluSensors) {
        mDataSet.clear();
        mDataSet.addAll(bluSensors);
        notifyDataSetChanged();
    }

    public void updateSensor(int position, BluSensor bluSensor) {
        mDataSet.remove(position);
        mDataSet.add(position, bluSensor);
        notifyItemChanged(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position < LAST_EXT_POS ? VIEW_EXT_TYPE : VIEW_INT_TYPE;
    }

    public GridLayoutManager.SpanSizeLookup getSpanSizeLookup() {
        return spanSizeLookup;
    }

    private GridLayoutManager.SpanSizeLookup spanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            return position < LAST_EXT_POS ? 2 : 1;
        }
    };

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewType == VIEW_EXT_TYPE ? new ExternalSensorViewHolder((ExtSensorItemBinding) DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.ext_sensor_item, null, false))
                : new InternalSensorViewHolder((IntSensorItemBinding) DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.int_sensor_item, null, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BluSensor bluSensor = mDataSet.get(position);
        if (holder instanceof ExternalSensorViewHolder) {
            ((ExternalSensorViewHolder) holder).mViewBinding.setSensorExt(bluSensor);
            ((ExternalSensorViewHolder) holder).mViewBinding.getRoot().setOnClickListener(onClickListener(bluSensor));
        } else if (holder instanceof InternalSensorViewHolder) {
            InternalSensorViewHolder internalSensorViewHolder = ((InternalSensorViewHolder) holder);
            internalSensorViewHolder.mViewBinding.setSensorInt((IntBluSensor) bluSensor);
            internalSensorViewHolder.mViewBinding.getRoot().setOnClickListener(onClickListener(bluSensor));
            if (bluSensor.itemSelected == ITEM_SELECTED.IOPins) {
                internalSensorViewHolder.mViewBinding.containerExtValue.setVisibility(View.GONE);
                internalSensorViewHolder.mViewBinding.imageBck.setAlpha(1f);
            } else {
                internalSensorViewHolder.mViewBinding.containerExtValue.setVisibility(View.VISIBLE);
                internalSensorViewHolder.mViewBinding.imageBck.setAlpha(0.2f);
            }
        }
    }


    private View.OnClickListener onClickListener(final BluSensor bluSensor) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mITouchOnList != null)
                    mITouchOnList.onClickItem(bluSensor);
            }
        };
    }
}