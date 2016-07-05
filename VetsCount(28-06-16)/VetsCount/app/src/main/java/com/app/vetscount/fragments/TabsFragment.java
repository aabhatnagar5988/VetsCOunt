package com.app.vetscount.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.vetscount.R;
import com.app.vetscount.activities.BusinessOffersActivity;
import com.app.vetscount.datacontroller.DataManager;


public class TabsFragment extends Fragment {

    private ViewPager viewPager;
    private PagerSlidingTabStrip tabs;
    private MyPagerAdapter adapter;
    public OffersFragment offersFragment;
    public BusinessFragment businessFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tabs, null);
        getUiComponents(v);
        return v;
    }

    private void getUiComponents(View v) {
        System.out.println("oncreate>>>>>");

        viewPager = (ViewPager) v.findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) v.findViewById(R.id.tabs);
        adapter = new MyPagerAdapter(getChildFragmentManager());

        viewPager.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                        .getDisplayMetrics());
        viewPager.setPageMargin(pageMargin);

        tabs.setViewPager(viewPager);
        tabs.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub

                System.out.println("arg0>>>" + arg0);

                if (arg0 == 0) {

                    ((BusinessOffersActivity) getActivity()).isOfferTabSelected = true;
                    if (DataManager.getInstance().getofferList().isEmpty()) {
                        offersFragment.getBusiness();
                    }
                } else {
                    ((BusinessOffersActivity) getActivity()).isOfferTabSelected = false;
                    if (DataManager.getInstance().getBusinessList().isEmpty()) {
                        businessFragment.getBusiness();
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });

        // viewPager.setOnPageChangeListener(new OnPageChangeListener() {
        //
        // @Override
        // public void onPageSelected(int arg0) {
        // // TODO Auto-generated method stub
        //
        // System.out.println("arg0>>>>"+arg0);
        //
        // }
        //
        // @Override
        // public void onPageScrolled(int arg0, float arg1, int arg2) {
        // // TODO Auto-generated method stub
        //
        // }
        //
        // @Override
        // public void onPageScrollStateChanged(int arg0) {
        // // TODO Auto-generated method stub
        //
        // }
        // });

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        System.out.println("onresume>>>>>");
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"Offers", "Business"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (offersFragment == null)
                        offersFragment = new OffersFragment();

                    return offersFragment;
                case 1:
                    if (businessFragment == null)
                        businessFragment = new BusinessFragment();

                    return businessFragment;

                default:
                    return null;
            }

        }
    }

}