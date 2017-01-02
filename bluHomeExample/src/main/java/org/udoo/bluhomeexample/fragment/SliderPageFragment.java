package org.udoo.bluhomeexample.fragment;

/**
 * Created by harlem88 on 19/08/16.
 */

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.databinding.FragmentTutorialBinding;
import org.udoo.bluhomeexample.model.TutorialBluModel;

public class SliderPageFragment extends Fragment {
    private FragmentTutorialBinding mViewBinding;

    public static SliderPageFragment Builder(int position) {
        SliderPageFragment sliderPageFragment = new SliderPageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        sliderPageFragment.setArguments(bundle);
        return sliderPageFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tutorial,container, false);
        return mViewBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setImage(getArguments().getInt("position"));
    }


    public void setImage(int position){
        Resources res = getResources();
        String title = res.getStringArray(R.array.tutorial_blu_titles)[position];
        String text = res.getStringArray(R.array.tutorial_blu_texts)[position];

        TypedArray images = res.obtainTypedArray(R.array.tutorial_blu_images);
        int imageRes = images.getResourceId(position, -1);

        mViewBinding.setModel(TutorialBluModel.Builder(title, text, imageRes));

        images.recycle();
    }
}
