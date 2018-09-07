package cn.zgy.banner.adapter;

import android.content.Context;
import android.view.View;

import cn.zgy.banner.BannerView;


public interface BannerBaseAdapter<T> {
    View getView(BannerView bannerView, Context context, int position, T data);
}
