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

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.loadview.LoadViewHolder;

import java.util.ArrayList;
import java.util.List;

import cn.zgy.base.BaseFragment;
import cn.zgy.multilist.bean.ImageItem;
import cn.zgy.multilist.bean.RichItem;
import cn.zgy.multilist.bean.TextItem;
import cn.zgy.multilist.binder.ImageItemViewBinder;
import cn.zgy.multilist.binder.RichItemViewBinder;
import cn.zgy.multilist.binder.TextItemViewBinder;
import cn.zgy.multitype.MultiTypeAdapter;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;
/**
* 自定义实现网络加载页面及结果展示
* @author zhengy
* create at 2018/9/11 下午2:38
**/
public class Loading_NewsFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener

    {

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
//            mRefreshLayout.autoRefresh();
                login();


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
            adapter.register(TextItem.class, new TextItemViewBinder());
            adapter.register(ImageItem.class, new ImageItemViewBinder());
            adapter.register(RichItem.class, new RichItemViewBinder());
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
            login();
            if (type == 0) {
                mRefreshLayout.finishRefresh(2000);
                items.clear();
            } else {
                mRefreshLayout.finishLoadMore(2000);
            }
            TextItem textItem = new TextItem("纯文字列表条目");
            ImageItem imageItem = new ImageItem(R.drawable.image_practice_repast_1);
            RichItem richItem = new RichItem("左图右文列表条目", R.drawable.image_avatar_1);

            for (int i = 0; i < 10; i++) {
                items.add(textItem);
                items.add(imageItem);
                items.add(richItem);
            }

            adapter.setItems(items);
            adapter.notifyDataSetChanged();
        }

        private void login() {

            EasyHttp.post("/login")
                    .params("username", "yyf")
                    .params("password", "jnvm4639")
                    .execute(new SimpleCallBack<LoginEntity>() {
                        @Override
                        public void onError(ApiException e) {
                            Log.e("TGA", "error" + e);
                        }

                        @Override
                        public void onSuccess(LoginEntity loginEntity) {

                            Log.e("TGA", loginEntity.toString());
                        }
                        //LoadViewHolder可以绑定view和viewGroup，本项目中和SmartRefreshLayout有一定冲突，如何结合使用时
                        //请直接绑定mRefreshLayout，而不是其中的recycleview
                    }, new LoadViewHolder(mRefreshLayout, container));


        }
    }


