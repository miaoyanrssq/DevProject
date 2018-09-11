package cn.zgy.news;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import android.support.design.widget.CompatTabLayout;

import java.util.ArrayList;
import java.util.List;

import cn.zgy.autoview.CompatViewPager;
import cn.zgy.base.BaseFragment;
import cn.zgy.news.adapter.HomePagerAdapter;
import cn.zgy.news.bean.ChannelBean;

public class HomeFragment extends BaseFragment {

    @BindView(R2.id.tab_layout)
    CompatTabLayout mTabLayout;
    @BindView(R2.id.iv_plus)
    ImageView ivPlus;
    @BindView(R2.id.view_pager)
    CompatViewPager mViewPager;
    Unbinder unbinder;


    private HomePagerAdapter mPagerAdapter;
    private OnPageChangeListener mOnPageChangeListener;
    private List<ChannelBean> channels = new ArrayList<>();

    int mPosition = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.news_fragment_home, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initChannels();
        initViewPager(channels);
        mOnPageChangeListener = new OnPageChangeListener();
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);
    }

    private void initChannels() {
        channels.clear();
        channels.add(new ChannelBean((long)1, "新闻", true, true, "normal", "", 1));
        channels.add(new ChannelBean((long)2, "视频", true, true, "normal", "", 2));
        channels.add(new ChannelBean((long)3, "直播", true, true, "normal", "", 3));
        channels.add(new ChannelBean((long)4, "图片", true, true, "normal", "", 4));
        channels.add(new ChannelBean((long)5, "作者", true, true, "author", "", 5));
        channels.add(new ChannelBean((long)1, "新闻2", true, true, "normal", "", 6));
        channels.add(new ChannelBean((long)2, "视频2", true, true, "normal", "", 7));
        channels.add(new ChannelBean((long)3, "直播2", true, true, "normal", "", 8));
        channels.add(new ChannelBean((long)4, "图片2", true, true, "normal", "", 9));
        channels.add(new ChannelBean((long)5, "作者2", true, true, "author", "", 10));
        channels.add(new ChannelBean((long)1, "新闻3", true, true, "normal", "", 11));
        channels.add(new ChannelBean((long)2, "视频3", true, true, "normal", "", 12));
        channels.add(new ChannelBean((long)3, "直播3", true, true, "normal", "", 13));
        channels.add(new ChannelBean((long)4, "图片3", true, true, "normal", "", 14));
        channels.add(new ChannelBean((long)5, "作者3", true, true, "author", "", 15));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R2.id.iv_plus)
    public void onViewClicked() {
    }



    private void initViewPager(List<ChannelBean> channels) {
        try {
            if (channels != null && !channels.isEmpty()) {
                if (mPagerAdapter == null) {
                mPagerAdapter = new HomePagerAdapter(getChildFragmentManager(), channels);
                mViewPager.setAdapter(mPagerAdapter);
                mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                mTabLayout.setupWithViewPager(mViewPager);
                mPagerAdapter.setTabLayout(mTabLayout);
                if (mPosition < mPagerAdapter.getCount()) {
                    mViewPager.setCurrentItem(mPosition);
//                    mOnPageChangeListener.onPageSelected(mPosition);
                }
                } else {
                    mPagerAdapter.updateDataSet(channels);
                    if (mViewPager.getCurrentItem() < mPagerAdapter.getCount()) {
//                        mOnPageChangeListener.onPageSelected(mViewPager.getCurrentItem());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    class OnPageChangeListener implements ViewPager.OnPageChangeListener {


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            // TODO: 2018/9/10 做一些点击协同操作
            mPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
