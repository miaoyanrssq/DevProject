package cn.zgy.launcher.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhouyou.http.loadview.LoadViewHolder;

import cn.zgy.base.BaseFragment;
import cn.zgy.launcher.R;

public class TestFragment extends BaseFragment {


    TextView splash;

    public TestFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_splash, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        splash = findViewById(R.id.splash);
//        new LoadViewHolder(splash, null);
    }
}
