package cn.zgy.launcher.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import cn.zgy.base.BaseFragment;
import cn.zgy.launcher.R;
import cn.zgy.launcher.widget.CircleProgress;

public class AdFragment  extends BaseFragment implements  CircleProgress.OnCircleProgressListener {

    CircleProgress mCircleProgress;
    public AdFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.launcher_fragment_ad, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCircleProgress = findViewById(R.id.splash_circle_progress);
        mCircleProgress.setOnCircleProgressListener(this);
        mCircleProgress.setDuration(3000);
        mCircleProgress.start();
    }

    @Override
    public void onFinish() {
        toHome();
    }

    @Override
    public void onClick(View view) {
        toHome();
    }

    private void toHome(){
        toPath("/HomeActivity");
        getActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCircleProgress != null) {
            mCircleProgress.cancel();
        }
    }
}
