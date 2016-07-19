package org.udoo.udoobluwearexample.adapter;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.udoo.udoobluwearexample.R;
import org.udoo.udoobluwearexample.model.BluItem;

import java.util.List;

/**
 * Created by harlem88 on 05/03/16.
 */
public class BluItemAdapter extends WearableListView.Adapter {
    private List<BluItem> mDataset;
    private final Context mContext;
    private final LayoutInflater mInflater;

    public BluItemAdapter(Context context, List<BluItem> bleItems) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDataset = bleItems;
    }

    public static class ItemViewHolder extends WearableListView.ViewHolder {
        private TextView textView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.name);
        }
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(mInflater.inflate(R.layout.ble_scan_item, null));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder,
                                 int position) {
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        TextView view = itemHolder.textView;
        view.setText(mDataset.get(position).address);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void addItem(BluItem bluItem) {
        mDataset.add(bluItem);
        notifyDataSetChanged();
    }

    public BluItem getItem(int pos) {
        return mDataset.get(pos);
    }
}