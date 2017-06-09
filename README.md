
# android tablayout 
有不足之地，欢迎指点！
https://github.com/weishl110/Sample/raw/master/image/3.gif
可以设置图片，图片的位置 left/top/right/bottom
图片和文字的选择器，是否显示图片等功能，具体使用方法如下：

        <com.tablayoutlibrary.app.CommonTabLayout
                android:id="@id/common_tablayout"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_alignParentBottom="true"
                android:orientation="horizontal"
                android:padding="10dp"
                tl:tl_iconGravity="TOP"
                tl:tl_iconHeight="20dp"
                tl:tl_iconVisible="true"
                tl:tl_iconWidth="20dp"
                tl:tl_indicatorAnimEnable="true"
                tl:tl_indicatorColor="@color/colorPrimary"
                tl:tl_indicatorEnable="true"
                tl:tl_indicatorGravity="INDICATOR_TOP"
                tl:tl_indicatorMarginBottom="6dp"
                tl:tl_indicatorMarginTop="6dp"
                tl:tl_tabPadding="5dp"
                tl:tl_textSelectColor="@color/color_f39800"
                tl:tl_textUnSelectColor="@color/color_545454"
                tl:tl_textsize="12sp"/>
        
        
   在页面中设置，可以关联viewpager
        
          ArrayList<TabEntity> list = new ArrayList<>();
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
        
 自定义属性
      <attr name="tl_textsize" format="dimension"/>
        <attr name="tl_textSelectColor" format="reference"/>
        <attr name="tl_textUnSelectColor" format="reference"/>
        <attr name="tl_iconVisible" format="boolean"/>
        <attr name="tl_iconHeight" format="dimension"/>
        <attr name="tl_iconWidth" format="dimension"/>
        <attr name="tl_iconMargin" format="dimension"/>
        <attr name="tl_iconGravity">
            <enum name="LEFT" value="17"/>
            <enum name="TOP" value="18"/>
            <enum name="RIGHT" value="19"/>
            <enum name="BOTTOM" value="20"/>
        </attr>
        <attr name="tl_tabPadding" format="dimension"/>
        <attr name="tl_padding" format="dimension"/>

        <!-- indicator -->
        <attr name="tl_indicatorColor" format="color"/>
        <attr name="tl_indicatorHeight" format="dimension"/>
        <attr name="tl_indicatorWidth" format="reference"/>
        <attr name="tl_indicatorMarginLeft" format="reference"/>
        <attr name="tl_indicatorMarginTop" format="dimension"/>
        <attr name="tl_indicatorMarginRight" format="dimension"/>
        <attr name="tl_indicatorMarginBottom" format="dimension"/>
        <attr name="tl_indicatorGravity">
            <flag name="INDICATOR_LEFT" value="0x1001"/>
            <flag name="INDICATOR_TOP" value="0x1002"/>
            <flag name="INDICATOR_RIGHT" value="0x1003"/>
            <flag name="INDICATOR_BOTTOM" value="0x1004"/>
        </attr>
        <attr name="tl_indicatorEnable" format="boolean"/>
        <attr name="tl_indicatorAnimEnable" format="boolean"/>
        
