package org.udoo.bluhomeexample.activity;

/**
 * Created by harlem88 on 19/08/16.
 */

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import org.udoo.bluhomeexample.MainActivity;
import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.databinding.TutorialLayoutBinding;
import org.udoo.bluhomeexample.fragment.SliderPageFragment;

public class ScreenSlidePagerActivity extends FragmentActivity {

    private static final int NUM_PAGES = 4;

    private PagerAdapter mPagerAdapter;
    private int mPosition;
    private TutorialLayoutBinding mViewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = DataBindingUtil.setContentView(this, R.layout.tutorial_layout);

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mViewBinding.pager.setAdapter(mPagerAdapter);
        mViewBinding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), MainActivity.class));
                finish();
            }
        });
        setColor(0, true);
        mViewBinding.pager.setPageTransformer(true, new ParallaxPageTransformer());
        mViewBinding.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setColor(position, position - mPosition > 0);
                mPosition = position;
                mViewBinding.btnContinue.setVisibility(mPosition == NUM_PAGES -1 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i("onPageScr ", " " +state);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mViewBinding.pager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mViewBinding.pager.setCurrentItem(mViewBinding.pager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return SliderPageFragment.Builder(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    private void setColor(final int position, boolean rise) {
        TranslateAnimation animation;

        if (position > 0 || !rise) {
            View viewOne, viewTwo;

            viewOne = mViewBinding.circleContainer.getChildAt(position + (rise ? -1 : 1));
            viewTwo = mViewBinding.circleContainer.getChildAt(position);

            animation = new TranslateAnimation(0, viewTwo.getX() - viewOne.getX(), 0, viewTwo.getY() - viewOne.getY());
            animation.setRepeatMode(0);
            animation.setDuration(400);
            animation.setFillBefore(true);
            animation.setFillEnabled(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    setAlpha(position);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            viewOne.startAnimation(animation);

        } else {
            setAlpha(position);
        }


    }

    private void setAlpha(int position){
        int size = mViewBinding.circleContainer.getChildCount();
        for (int i = 0; i < size; i++) {
            if (i == position){
//                ((ImageView) mViewBinding.circleContainer.getChildAt(i)).setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                ((ImageView) mViewBinding.circleContainer.getChildAt(i)).setAlpha(1f);

            }
            else
                ((ImageView) mViewBinding.circleContainer.getChildAt(i)).setAlpha(0.4f);
//                ((ImageView) mViewBinding.circleContainer.getChildAt(i)).setColorFilter(colorGrey);
        }
    }

    public class ParallaxPageTransformer implements ViewPager.PageTransformer {

        public void transformPage(View view, float position) {

            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(1);

            } else if (position <= 1) { // [-1,1]
//                dummyImageView.setTranslationX(-position * (pageWidth / 2)); //Half the normal speed

            } else {
                view.setAlpha(1);
            }


        }
    }
}

