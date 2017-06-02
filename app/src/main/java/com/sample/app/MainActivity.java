package com.sample.app;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.sample.app.adapter.MainPagerAdapter;
import com.tablayoutlibrary.app.CommonTabLayout;
import com.tablayoutlibrary.app.TabEntity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String[] TITLE = {"首页", "美女", "视频", "关注"};
    private int[] mIconUnselectIds = {
            R.mipmap.ic_home_normal, R.mipmap.ic_girl_normal, R.mipmap.ic_video_normal, R.mipmap.ic_care_normal};
    private int[] mIconSelectIds = {
            R.mipmap.ic_home_selected, R.mipmap.ic_girl_selected, R.mipmap.ic_video_selected, R.mipmap.ic_care_selected};
    private CommonTabLayout tablayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tablayout = (CommonTabLayout) findViewById(R.id.common_tablayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        initTabAndPager();
    }

    private void initTabAndPager() {

        final ArrayList<TabEntity> list = new ArrayList<>();
        for (int i = 0; i < TITLE.length; i++) {
            TabEntity tabEntity = new TabEntity();
            tabEntity.tabTitle = TITLE[i];
            tabEntity.selectedIcon = mIconSelectIds[i];
            tabEntity.unSelectedIcon = mIconUnselectIds[i];
            list.add(tabEntity);
        }
        tablayout.setTabData(list);
        tablayout.setOnSelectorListener(new CommonTabLayout.OnSelectorListener() {
            @Override
            public void onSeletor(int position) {
//                viewPager.setCurrentItem(position);
            }
        });

        ArrayList<BaseFragment> fragments = new ArrayList<>();
        fragments.add(BaseFragment.newInstance(this, OneFragment.class));
        fragments.add(BaseFragment.newInstance(this, TwoFragment.class));
        fragments.add(BaseFragment.newInstance(this, NewsFragment.class));
        fragments.add(BaseFragment.newInstance(this, FourFragment.class));
        viewPager.setAdapter(new MainPagerAdapter(getFragmentManager(), fragments));
        tablayout.setUpWithViewPager(viewPager);
    }
}
