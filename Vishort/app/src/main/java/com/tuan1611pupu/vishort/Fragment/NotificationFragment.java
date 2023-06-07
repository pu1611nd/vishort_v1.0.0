package com.tuan1611pupu.vishort.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.tuan1611pupu.vishort.Adapter.NotificationPagerAdapter;
import com.tuan1611pupu.vishort.R;

public class NotificationFragment extends Fragment {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        viewPager = view.findViewById(R.id.pager);
        tabLayout = view.findViewById(R.id.tablayout);
        init();

        return  view;
    }

    private void init(){
        NotificationPagerAdapter mainViewPagerAdapter = new NotificationPagerAdapter(getChildFragmentManager(),getLifecycle());
        mainViewPagerAdapter.addFragment(new FragmentNoti());
        mainViewPagerAdapter.addFragment(new Fragment_List_Chat());
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setAdapter(mainViewPagerAdapter);
        tabLayout.addTab(tabLayout.newTab().setText("Notification"));
        tabLayout.addTab(tabLayout.newTab().setText("Chat"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
               viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

}
