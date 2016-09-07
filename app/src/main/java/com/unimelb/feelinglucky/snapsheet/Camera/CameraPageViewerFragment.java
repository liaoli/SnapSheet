package com.unimelb.feelinglucky.snapsheet.Camera;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unimelb.feelinglucky.snapsheet.R;
import com.unimelb.feelinglucky.snapsheet.View.CustomizedViewPager;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Created by leveyleonhardt on 8/31/16.
 */
public class CameraPageViewerFragment extends Fragment {
    private VerticalViewPager viewPager;
    private List<Fragment> fragmentList = new ArrayList<>();
    private CustomizedViewPager customizedviewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customizedviewPager = (CustomizedViewPager) getActivity().findViewById(R.id.activity_fragment_view_pager);

        fragmentList.add(new ProfileFragment());
        fragmentList.add(new CameraFragment());
        fragmentList.add(new MemoryFragment());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera_pageviewer, container, false);
        viewPager = (VerticalViewPager) view.findViewById(R.id.fragment_camera_vertical_viewpager);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
        viewPager.setCurrentItem(1);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    customizedviewPager.setCanScroll(true);
                } else {
                    customizedviewPager.setCanScroll(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

}