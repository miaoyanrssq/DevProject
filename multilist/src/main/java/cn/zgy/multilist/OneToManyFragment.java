package cn.zgy.multilist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import cn.zgy.base.BaseFragment;
import cn.zgy.multilist.bean.ImageItem;
import cn.zgy.multilist.bean.RichItem;
import cn.zgy.multilist.bean.TextItem;
import cn.zgy.multilist.bean.TypeItem;
import cn.zgy.multilist.binder.ImageItemViewBinder;
import cn.zgy.multilist.binder.RichItemViewBinder;
import cn.zgy.multilist.binder.TextItemViewBinder;
import cn.zgy.multilist.json.TypeDeserializer;
import cn.zgy.multitype.Items;
import cn.zgy.multitype.MultiTypeAdapter;
import cn.zgy.multitype.Parser;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;
/**
* 模拟网络拉取数据，one-to-many一对多的关系，根据数据中type来制定相应的binder
* @author zhengy
* create at 2018/9/7 下午4:02
**/
public class OneToManyFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener {


    Parser parser;
    private static final String JSONDATA = "[{\"text\":\"纯文字列表条目\",\"type\":\"TextItem\"},{\"resId\":2131165302,\"type\":\"ImageItem\"},{\"imageResId\":2131165300,\"text\":\"左图右文列表条目\",\"type\":\"RichItem\"},{\"text\":\"纯文字列表条目\",\"type\":\"TextItem\"},{\"resId\":2131165301,\"type\":\"ImageItem\"},{\"imageResId\":2131165300,\"text\":\"左图右文列表条目\",\"type\":\"RichItem\"},{\"text\":\"纯文字列表条目\",\"type\":\"TextItem\"},{\"resId\":2131165302,\"type\":\"ImageItem\"},{\"imageResId\":2131165300,\"text\":\"左图右文列表条目\",\"type\":\"RichItem\"},{\"text\":\"纯文字列表条目\",\"type\":\"TextItem\"},{\"resId\":2131165301,\"type\":\"ImageItem\"},{\"imageResId\":2131165300,\"text\":\"左图右文列表条目\",\"type\":\"RichItem\"},{\"text\":\"纯文字列表条目\",\"type\":\"TextItem\"},{\"resId\":2131165302,\"type\":\"ImageItem\"},{\"imageResId\":2131165300,\"text\":\"左图右文列表条目\",\"type\":\"RichItem\"},{\"text\":\"纯文字列表条目\",\"type\":\"TextItem\"},{\"resId\":2131165301,\"type\":\"ImageItem\"},{\"imageResId\":2131165300,\"text\":\"左图右文列表条目\",\"type\":\"RichItem\"},{\"text\":\"纯文字列表条目\",\"type\":\"TextItem\"},{\"resId\":2131165302,\"type\":\"ImageItem\"},{\"imageResId\":2131165300,\"text\":\"左图右文列表条目\",\"type\":\"RichItem\"},{\"text\":\"纯文字列表条目\",\"type\":\"TextItem\"},{\"resId\":2131165301,\"type\":\"ImageItem\"},{\"imageResId\":2131165300,\"text\":\"左图右文列表条目\",\"type\":\"RichItem\"},{\"text\":\"纯文字列表条目\",\"type\":\"TextItem\"},{\"resId\":2131165302,\"type\":\"ImageItem\"},{\"imageResId\":2131165300,\"text\":\"左图右文列表条目\",\"type\":\"RichItem\"},{\"text\":\"纯文字列表条目\",\"type\":\"TextItem\"},{\"resId\":2131165301,\"type\":\"ImageItem\"},{\"imageResId\":2131165300,\"text\":\"左图右文列表条目\",\"type\":\"RichItem\"}]";
    private View mCacheView;
    private View mEmptyLayout;
    private RecyclerView mRecyclerView;
    private RefreshLayout mRefreshLayout;

    private MultiTypeAdapter adapter;
    private Items items = new Items();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        if(mCacheView == null) {
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

        if (type == 0) {
            mRefreshLayout.autoRefresh();
            mRefreshLayout.finishRefresh(2000);
            items.clear();
        } else {
            mRefreshLayout.autoLoadMore();
            mRefreshLayout.finishLoadMore(2000);
        }
        loadRemoteData();
    }

    private void loadRemoteData(){
        parser = new TypeDeserializer();
        List<TypeItem> list = parser.fromJson(JSONDATA);

        items = new Items(items);
        items.addAll(list);
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
    }

}
