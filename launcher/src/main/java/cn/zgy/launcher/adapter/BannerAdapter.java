package cn.zgy.launcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import cn.zgy.banner.BannerView;
import cn.zgy.banner.adapter.BannerBaseAdapter;
import cn.zgy.launcher.R;


public class BannerAdapter implements BannerBaseAdapter<Integer> {
    private Context mContext;

    public BannerAdapter(Context context) {
        mContext=context;
    }

    @Override
    public View getView(final BannerView bannerView, final Context context, int position, Integer data) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.id_image);
        imageView.setImageResource(data);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }


}


