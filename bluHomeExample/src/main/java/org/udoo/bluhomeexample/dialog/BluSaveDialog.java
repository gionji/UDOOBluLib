package org.udoo.bluhomeexample.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.databinding.BluEditDialogBinding;
import org.udoo.bluhomeexample.interfaces.OnResult;
import org.udoo.bluhomeexample.model.BluItem;
import org.udoo.bluhomeexample.util.Util;

/**
 * Created by harlem88 on 08/10/16.
 */

public class BluSaveDialog extends DialogFragment {
    private BluEditDialogBinding mViewBinding;
    private OnResult<BluItem> mOnResult;
    private BluItem mBluItem;

    public static BluSaveDialog Builder(String address) {
        BluSaveDialog bluSaveDialog = new BluSaveDialog();
        Bundle bundle = new Bundle();
        bundle.putString("address", address);
        bluSaveDialog.setArguments(bundle);
        return bluSaveDialog;
    }

    public void setResultCallback(OnResult<BluItem> onResult){
        mOnResult = onResult;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mViewBinding = DataBindingUtil.inflate(inflater, R.layout.blu_edit_dialog, container, false);
        return mViewBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBluItem = BluItem.Builder(getArguments().getString("address"), "", "", R.color.blue_500);
        mViewBinding.address.setText(mBluItem.address);
        mViewBinding.listColor.setHasFixedSize(true);
        mViewBinding.listColor.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        ColorBluAdapter colorBluAdapter = new ColorBluAdapter(getBluColors());
        mViewBinding.listColor.setAdapter(colorBluAdapter);

        mViewBinding.btnPair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mViewBinding.edit.getText() != null){
                    mBluItem.name = mViewBinding.edit.getText().toString();
                    if(mOnResult != null)
                        mOnResult.onSuccess(mBluItem);
                    dismiss();
                }
            }
        });
        colorBluAdapter.setBluColorClickListener(new BluColorClickListener() {
            @Override
            public void onClickItem(int color) {
                mBluItem.color = color;
                mViewBinding.btnPair.setBackgroundColor(color);
                Util.EnterCircularReveal(mViewBinding.btnPair);

            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public interface BluColorClickListener{
        void onClickItem(int pos);
    }

    private int[] getBluColors() {
        return getResources().getIntArray(R.array.blu_colors);
    }


    public class ColorBluAdapter extends RecyclerView.Adapter<ColorBluAdapter.ColorBluViewHolder> {
        private int[] colors;
        private BluColorClickListener mBluColorClickListener;



        public ColorBluAdapter(int... colors) {
            this.colors = colors;
        }

        public void setBluColorClickListener(BluColorClickListener bluColorClickListener){
            mBluColorClickListener = bluColorClickListener;
        }

        @Override
        public ColorBluViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ColorBluViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.color_blu_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ColorBluViewHolder holder, int position) {
            final int color = colors[position];
            holder.item.setBackgroundColor(colors[position]);
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mViewBinding.edit.getWindowToken(), 0);

                    if(mBluColorClickListener != null)
                        mBluColorClickListener.onClickItem(color);
                }
            });
        }

        @Override
        public int getItemCount() {
            return colors.length;
        }

        public class ColorBluViewHolder extends RecyclerView.ViewHolder {
            public View item;

            public ColorBluViewHolder(View v) {
                super(v);
                item = v.findViewById(R.id.blu_color_item);
            }
        }

    }


}
