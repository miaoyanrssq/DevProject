package cn.zgy.media;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.zgy.autoview.GridSpacingItemDecoration;
import cn.zgy.base.BaseActivity;
import cn.zgy.base.permission.AbsPermCallBack;
import cn.zgy.base.permission.Permission;
import cn.zgy.base.permission.PermissionManager;
import cn.zgy.media.bean.MediaEntity;
import cn.zgy.media.binder.MediaBinder;
import cn.zgy.media.dao.LocalMediaDaoHelper;
import cn.zgy.media.fragment.ImagePreviewFragment;
import cn.zgy.media.listener.OnItemClickListener;
import cn.zgy.multitype.Items;
import cn.zgy.multitype.MultiTypeAdapter;
import cn.zgy.photoview.Info;

public class MediaSelectActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, OnItemClickListener, ImagePreviewFragment.OnClick{


    TextView tvComplete;
    RecyclerView recycler;
    FrameLayout previewFrame;

    PreviewViewHolder previewHolder;


    MultiTypeAdapter adapter;
    ArrayList<MediaEntity> items = new ArrayList<>();

    /**
     * 可选择最大个数, 默认1个
     */
    private int maxNum = 1;
    private boolean isShowSelectedNum = true;
    private String mTakePicPath;
    private File mTakePicFile;

    /**
     * 最大个数
     */
    public static final String MAX_NUM = "max_num";
    /**
     * 选择的照片返回数据 key
     */
    public static final String KEY_DATA = "key_data";

    public static final String SHOW_SELECTED_NUM = "show_selected_num";


    private ArrayList<MediaEntity> selectedList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_media_select;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initState(savedInstanceState);
        initPermission();
    }

    private void initView() {
        tvComplete = findById(R.id.tv_complete);
        previewFrame = findById(R.id.frame);
        findViewById(R.id.iv_back).setOnClickListener(this);
        tvComplete.setOnClickListener(this);

        recycler = findById(R.id.recycleview);
        recycler.setLayoutManager(new GridLayoutManager(this, 3));
        recycler.addItemDecoration(new GridSpacingItemDecoration(3, 3, true));
        adapter = new MultiTypeAdapter();
        adapter.register(MediaEntity.class, new MediaBinder(this, this));

        recycler.setAdapter(adapter);

    }

    private void initState(Bundle savedState) {
        if (savedState != null) {
            maxNum = savedState.getInt(MAX_NUM, maxNum);
            isShowSelectedNum = savedState.getBoolean(SHOW_SELECTED_NUM, true);
        } else {
            maxNum = getIntent().getIntExtra(MAX_NUM, maxNum);
            isShowSelectedNum = getIntent().getBooleanExtra(SHOW_SELECTED_NUM, true);
        }
    }

    private void initPermission() {
        PermissionManager.get().request(this, new AbsPermCallBack() {
            @Override
            public void onGranted(boolean isAlreadyDef) {
                new MediaQueryTask().execute();
            }

            @Override
            public void onDenied(List<String> neverAskPerms) {
                Toast.makeText(getApplicationContext(), "选择照片需要访问权限",
                        Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }, Permission.STORAGE_READE);
    }




    /**
     * 异步查询资源文件
     *
     * @author a_liYa
     * @date 16/10/21 下午10:17.
     */
    private class MediaQueryTask extends AsyncTask<Integer, Integer, List<MediaEntity>> {

        @Override
        protected List<MediaEntity> doInBackground(Integer... params) {
            return new LocalMediaDaoHelper().queryImageMedia();
        }

        @Override
        protected void onPostExecute(List<MediaEntity> results) {
            items.addAll(results);
            adapter.setItems(items);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.iv_back){
            onBackPressed();
        }else if(view.getId() == R.id.tv_complete){
            complete();
        }

    }

    private void complete() {
        if (selectedList != null && selectedList.size() > 0) {
            Intent data = new Intent();
            data.putParcelableArrayListExtra(KEY_DATA, selectedList);
            setResult(RESULT_OK, data);
        }
        onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (previewFrame.getVisibility() == View.VISIBLE) {
                previewFrame.setVisibility(View.GONE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onItemClick(View itemView, int position) {
        if (previewHolder == null) {
            previewHolder = new PreviewViewHolder(items);
        }
        previewHolder.show(position);
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        Object tag = compoundButton.getTag();
        if(tag instanceof MediaEntity){
            if(isChecked){
                //已到最大个数
                if(selectedList.size() == maxNum && 1 != maxNum){
                    compoundButton.setOnCheckedChangeListener(null);
                    compoundButton.setChecked(false);
                    compoundButton.setOnCheckedChangeListener(this);
                    ((MediaEntity) tag).setSelected(false);
                    Toast.makeText(this, "你最多只能选择" + maxNum + "张照片", Toast.LENGTH_SHORT).show();
                    return;
                    //单选
                }else if( selectedList.size() == maxNum && 1 == maxNum){
                    clearSelected();
                }
                selectedList.add((MediaEntity) tag);
            }else {
                selectedList.remove(tag);
            }
        }

        if (selectedList == null || selectedList.isEmpty() || !isShowSelectedNum) {
            tvComplete.setText("完成");
        } else {
            tvComplete.setText(selectedList.size() + " 完成");
        }

    }

    private void clearSelected() {
        if (selectedList != null) {
            for (MediaEntity entity : selectedList) {
                entity.setSelected(false);
                int indexOf = items.indexOf(entity);
                if (indexOf >= 0) {
                    adapter.notifyItemChanged(indexOf);
                }
            }
            selectedList.clear();
        }
    }

    @Override
    public void onPreviewClick(View view) {
        previewFrame.setVisibility(View.GONE);
    }


    private class PreviewViewHolder {

        ViewPager viewPager;

        PagerAdapter pagerAdapter;

        List<MediaEntity> list;

        public PreviewViewHolder(List<MediaEntity> list) {
            this.list = list;
            viewPager = (ViewPager) previewFrame.findViewById(R.id.view_pager);
        }

        public void show(int index) {
            if (pagerAdapter == null) {
                pagerAdapter = new ImagePreviewAdapter(getSupportFragmentManager(),
                        getStringList(list));
                viewPager.setAdapter(pagerAdapter);
            }

            viewPager.setCurrentItem(index, false);
            previewFrame.setVisibility(View.VISIBLE);
        }


        /**
         * 获取图片List列表
         *
         * @param list 数据源
         * @return list集合
         */
        public List<String> getStringList(List<MediaEntity> list) {
            List<String> resultList = new ArrayList<>(list.size());
            for (MediaEntity data : list) {
                resultList.add(data.getPath());
            }
            return resultList;
        }

    }

    /**
     * 图片预览 ViewPager 适配器
     *
     * @author a_liYa
     * @date 16/10/25 12:30.
     */
    private class ImagePreviewAdapter extends FragmentStatePagerAdapter {

        private List<String> data;

        public ImagePreviewAdapter(FragmentManager fm, List<String> list) {
            super(fm);
            this.data = list;
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public Fragment getItem(int position) {
            // TODO: 2018/9/13
            ImagePreviewFragment imagePreviewFragment = ImagePreviewFragment.newInstance(data.get(position));
//            imagePreviewFragment.setOnClick(MediaSelectActivity.this);
            return imagePreviewFragment;
        }

    }
}
