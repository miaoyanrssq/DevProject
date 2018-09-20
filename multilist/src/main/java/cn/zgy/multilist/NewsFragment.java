package cn.zgy.multilist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;



import java.util.ArrayList;
import java.util.List;

import cn.zgy.base.BaseFragment;
import cn.zgy.base.listener.OnItemClickListener;
import cn.zgy.multilist.bean.ImageItem;
import cn.zgy.multilist.bean.RichItem;
import cn.zgy.multilist.bean.TextItem;
import cn.zgy.multilist.binder.ImageItemViewBinder;
import cn.zgy.multilist.binder.RichItemViewBinder;
import cn.zgy.multilist.binder.TextItemViewBinder;
import cn.zgy.multitype.MultiTypeAdapter;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

/**
 * 刷新控件 + MultiType基本上使用
 *
 * @author zhengy
 * create at 2018/9/7 下午3:06
 **/
public class NewsFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener, OnItemClickListener {

    private View mCacheView;
    private View mEmptyLayout;
    private RecyclerView mRecyclerView;
    private SmartRefreshLayout mRefreshLayout;

    private MultiTypeAdapter adapter;
    private List<Object> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /**
         * mCacheView缓存策略，对fragment自身进行缓存，配合viewpager的本地缓存实现tablayout中页面的缓存功能{@link PagerCache}
         * @author zhengy
         * create at 2018/9/10 上午11:11
         **/
        if (mCacheView != null) {
            ViewParent parent = mCacheView.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(mCacheView);
            }
            return mCacheView;
        }
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mCacheView == null) {
            mCacheView = view;
            initView();
            mRefreshLayout.autoRefresh();


        }

    }

    private void initView() {

        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);
        mEmptyLayout = findViewById(R.id.empty);
        TextView text = mEmptyLayout.findViewById(R.id.empty_text);
        ((View) text.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doRefresh(0);
            }
        });
        initRecycleView();
    }

    private void initRecycleView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), VERTICAL));
        adapter = new MultiTypeAdapter();
        adapter.register(TextItem.class, new TextItemViewBinder(this));
        adapter.register(ImageItem.class, new ImageItemViewBinder(this));
        adapter.register(RichItem.class, new RichItemViewBinder(this));
        mRecyclerView.setAdapter(adapter);


    }


    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        doRefresh(0);

    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        doRefresh(1);
    }

    protected void doRefresh(int type) {
        if (type == 0) {
            mRefreshLayout.finishRefresh(2000);
            items.clear();
        } else {
            mRefreshLayout.finishLoadMore(2000);
        }
        TextItem textItem = new TextItem("纯文字列表条目");
        ImageItem imageItem = new ImageItem(R.drawable.image_practice_repast_1);
        ImageItem imageItem2 = new ImageItem(R.drawable.image_movie_header_48621499931969370);
        RichItem richItem = new RichItem("左图右文列表条目", R.drawable.image_avatar_1);

        for (int i = 0; i < 10; i++) {
            items.add(textItem);
            if(i % 2 == 0){
                items.add(imageItem);
            }else{
                items.add(imageItem2);
            }

            items.add(richItem);
        }

//        Log.e("Tga", new Gson().toJson(items));

        adapter.setItems(items);
        adapter.notifyDataSetChanged();
    }




    @Override
    public void onItemClick(View itemView, int position) {
        toPath("/BrowserActivity");
    }
}
