package org.udoo.bluhomeexample.adapter;

/**
 * Created by harlem88 on 17/02/16.
 */
//public class XYZSensorSummaryAdapter extends RecyclerView.Adapter<XYZSensorSummaryAdapter.XYZSensorItemViewHolder> {
//    private List<XYZSensor> mDataset;
//    private SensorViewClickListener mSensorViewClickListener;
//
//    public interface SensorViewClickListener {
//        void onSensorViewClicked(String name);
//    }
//
//    public void setSensorViewClickListener(SensorViewClickListener mSensorViewClickListener) {
//        this.mSensorViewClickListener = mSensorViewClickListener;
//    }
//
//    public XYZSensorSummaryAdapter(List<XYZSensor> list) {
//        mDataset = list;
//    }
//
//    @Override
//    public XYZSensorItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return new XYZSensorItemViewHolder(FragmentHomeXyzSensorItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
//    }
//
//    @Override
//    public void onBindViewHolder(XYZSensorItemViewHolder holder, int position) {
//        final XYZSensor sensor = mDataset.get(position);
//        if (sensor != null) {
//            holder.getBinding().setSensor(sensor);
//        }
//        holder.getBinding().getRoot().setOnClickListener((new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mSensorViewClickListener != null)
//                    mSensorViewClickListener.onSensorViewClicked(sensor.name);
//            }
//        }));
//    }
//
//    @Override
//    public int getItemCount() {
//        return mDataset.size();
//    }
//
//    public static class XYZSensorItemViewHolder extends RecyclerView.ViewHolder {
//        public FragmentHomeXyzSensorItemBinding itemBinding;
//
//        public XYZSensorItemViewHolder(FragmentHomeXyzSensorItemBinding binding) {
//            super(binding.getRoot());
//            itemBinding = binding;
//        }
//
//        public FragmentHomeXyzSensorItemBinding getBinding() {
//            return itemBinding;
//        }
//    }
//}
