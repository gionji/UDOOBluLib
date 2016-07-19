package org.udoo.udoobluwearexample.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridPagerAdapter;

import org.udoo.udoobluwearexample.R;
import org.udoo.udoobluwearexample.fragment.AccelerometerFragment;
import org.udoo.udoobluwearexample.fragment.ExternalSensorFragment;
import org.udoo.udoobluwearexample.fragment.GyroscopeFragment;
import org.udoo.udoobluwearexample.fragment.MagnetometerFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class VerticalGridPagerAdapter extends FragmentGridPagerAdapter {
    private static final int TRANSITION_DURATION_MILLIS = 100;
    private final Context mContext;
    private List<Row> mRows;
    private boolean firstItem;

    public interface TabsPageListener {
        void onPageSelected();
        void onPageDeselect();
        void onFragmentResume(Callable<Void> voidCallable);
    }


    public VerticalGridPagerAdapter(Context ctx, FragmentManager fm, String bluaddress) {
        super(fm);
        mContext = ctx;

        mRows = new ArrayList<>();
        mRows.add(new Row(AccelerometerFragment.Builder(bluaddress)));
        mRows.add(new Row(GyroscopeFragment.Builder(bluaddress)));
        mRows.add(new Row(MagnetometerFragment.Builder(bluaddress)));
        mRows.add(new Row(ExternalSensorFragment.Builder(bluaddress)));

    }

    /**
     * A convenient container for a row of fragments.
     */
    private class Row {
        final List<Fragment> columns = new ArrayList<Fragment>();

        public Row(Fragment fragment) {
            add(fragment);
        }

        public void add(Fragment f) {
            columns.add(f);
        }

        Fragment getColumn(int i) {
            return columns.get(i);
        }

        public int getColumnCount() {
            return columns.size();
        }
    }

    @Override
    public Fragment getFragment(final int row, final int col) {
        Row adapterRow = mRows.get(row);
        Fragment fragment = adapterRow.getColumn(0);
        TabsPageListener tabsPageListener = (TabsPageListener) fragment;
        return fragment;
    }


    @Override
    public int getRowCount() {
        return mRows.size();
    }

    @Override
    public int getColumnCount(int rowNum) {
        return 1;
    }

    // Obtain the background image for the specific page
    @Override
    public Drawable getBackgroundForPage(int row, int column) {

        Drawable backDrawable = GridPagerAdapter.BACKGROUND_NONE;
        switch (row){
            case 0 : backDrawable = mContext.getResources().getDrawable(R.drawable.accelerometer_01, null);break;
            case 1 : backDrawable = mContext.getResources().getDrawable(R.drawable.gyroscope, null);break;
            case 2 : backDrawable = mContext.getResources().getDrawable(R.drawable.magnetometer_02, null);break;
            case 3 : backDrawable = mContext.getResources().getDrawable(R.drawable.tempbaro, null);break;
        }
        return backDrawable;
//        if( row == 2 && column == 1) {
//            // Place image at specified position
//            return mContext.getResources().getDrawable(R.drawable.close_button, null);
//        } else {
//            // Default to background image for row
//            return GridPagerAdapter.BACKGROUND_NONE;
//        }
    }
}
