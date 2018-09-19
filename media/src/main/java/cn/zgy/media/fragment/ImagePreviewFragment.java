package cn.zgy.media.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.zgy.base.BaseFragment;
import cn.zgy.imageloader.loader.ImageLoader;
import cn.zgy.media.R;
import cn.zgy.photoview.Info;
import cn.zgy.photoview.PhotoView;

public class ImagePreviewFragment extends BaseFragment implements View.OnClickListener{

    OnClick click;
    private static final String ARGS = "args";
    //图片url
    private String mUrl;

    private PhotoView preview;

    public static ImagePreviewFragment newInstance(String url) {
        ImagePreviewFragment fragment = new ImagePreviewFragment();

        Bundle args = new Bundle();
        args.putString(ARGS, url);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnClick(OnClick click)
    {
        this.click = click;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUrl = getArguments().getString(ARGS);

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preview = findViewById(R.id.preview);
        preview.enable();
        preview.enableRotate();
        preview.setOnClickListener(this);
        loadImage();
    }

    private void loadImage() {
        ImageLoader.with(getContext()).url(mUrl).into(preview);
    }

    @Override
    public void onClick(View view) {
        if(click != null){
            click.onPreviewClick(view);
        }
    }

    public interface OnClick{
        void onPreviewClick(View view);
    }
}
