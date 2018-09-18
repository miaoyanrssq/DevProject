package cn.zgy.media.fragment;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.DataSource;
//import com.bumptech.glide.load.engine.GlideException;
//import com.bumptech.glide.request.RequestListener;
//import com.bumptech.glide.request.target.Target;

import javax.sql.DataSource;

import cn.zgy.base.BaseFragment;
import cn.zgy.imageloader.loader.ImageLoader;
import cn.zgy.media.R;

public class ImagePreviewFragment extends BaseFragment implements View.OnClickListener{

    OnClick click;
    private static final String ARGS = "args";
    //图片url
    private String mUrl;

    private ImageView preview;

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
        preview.setOnClickListener(this);
        loadImage();
    }

    private void loadImage() {

//        Uri uri = Uri.parse(mUrl);
//        if (uri != null) {
//            try {
//                mUrl = uri.buildUpon().appendQueryParameter("support_spare", String.valueOf(false)).build().toString();
//            } catch (Exception e) {
//            }
//        }
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
