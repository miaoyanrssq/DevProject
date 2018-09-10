package cn.zgy.news.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import java.util.List;

import cn.zgy.multilist.NewsFragment;
import cn.zgy.multilist.OneToManyFragment;
import cn.zgy.news.bean.ChannelBean;

public class HomePagerAdapter extends TabPagerAdapter<ChannelBean> {

    public HomePagerAdapter(FragmentManager fm, @NonNull List<ChannelBean> channels) {
        super(fm, channels);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mList.get(position).getName();
    }

    @Override
    protected boolean isUpdateEntity(ChannelBean newVal, ChannelBean oldVal) {
        return !TextUtils.equals(newVal.getName(), oldVal.getName());
    }

    @Override
    protected int getId(ChannelBean data) {
        return data.getId().intValue();
    }

    @Override
    public Fragment newFragment(ChannelBean channel) {
        Fragment f;
        switch (channel.getNav_type() != null ? channel.getNav_type() : "") {
            case "normal":
                f = new NewsFragment();
                break;
            case "author":
                f = new OneToManyFragment();
                break;
            default:
                f = new NewsFragment();
                break;
        }
        return f;
    }

    @Override
    public String toKey(ChannelBean channel) {
        return channel.getName();
    }

}


