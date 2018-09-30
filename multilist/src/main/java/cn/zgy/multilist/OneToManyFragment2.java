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
import cn.zgy.base.listener.OnItemClickListener;
import cn.zgy.multilist.bean.Data;
import cn.zgy.multilist.bean.ImageItem;
import cn.zgy.multilist.bean.RichItem;
import cn.zgy.multilist.bean.TextItem;
import cn.zgy.multilist.bean.TypeItem;
import cn.zgy.multilist.binder.DataTypeBinder1;
import cn.zgy.multilist.binder.DataTypeBinder2;
import cn.zgy.multilist.binder.DataTypeBinder3;
import cn.zgy.multilist.binder.ImageItemViewBinder;
import cn.zgy.multilist.binder.RichItemViewBinder;
import cn.zgy.multilist.binder.TextItemViewBinder;
import cn.zgy.multilist.json.TypeDeserializer;
import cn.zgy.multitype.ClassLinker;
import cn.zgy.multitype.ItemViewBinder;
import cn.zgy.multitype.Items;
import cn.zgy.multitype.Linker;
import cn.zgy.multitype.MultiTypeAdapter;
import cn.zgy.multitype.Parser;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

/**
 * 模拟网络拉取数据，one-to-many一对多的关系，根据数据中type来制定相应的binder
 *
 * @author zhengy
 * create at 2018/9/7 下午4:02
 **/
public class OneToManyFragment2 extends BaseFragment implements OnRefreshListener, OnLoadMoreListener {


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

        /**
         * 两种形式的一对多绑定
         */
//        adapter.register(Data.class).to(
//                new DataTypeBinder1(),
//                new DataTypeBinder2(),
//                new DataTypeBinder3()
//        ).withLinker(new Linker<Data>() {
//            @Override
//            public int index(int position, @NonNull Data data) {
//                if(Data.TYPE_1.equals(data.getType())){
//                    return 0;
//                }else if(Data.TYPE_2.equals(data.getType())){
//                    return 1;
//                }else if(Data.TYPE_3.equals(data.getType())){
//                    return 2;
//                }
//                return 0;
//            }
//        });

        adapter.register(Data.class).to(
                new DataTypeBinder1(),
                new DataTypeBinder2(),
                new DataTypeBinder3()
        ).withClassLinker(new ClassLinker<Data>() {
            @NonNull
            @Override
            public Class<? extends ItemViewBinder<Data, ?>> index(int position, @NonNull Data data) {
                if(Data.TYPE_1.equals(data.getType())){
                    return DataTypeBinder1.class;
                }else if(Data.TYPE_2.equals(data.getType())){
                    return DataTypeBinder2.class;
                }else if(Data.TYPE_3.equals(data.getType())){
                    return DataTypeBinder3.class;
                }
                return DataTypeBinder1.class;
            }
        });


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

    private void loadRemoteData() {
        for(int i=0 ; i<10 ; i++){
            items.add(new Data(Data.TYPE_1, "纯文字条目", 0));
            items.add(new Data(Data.TYPE_2, "", R.drawable.image_practice_repast_1));
            items.add(new Data(Data.TYPE_3, "左图右文列表条目", R.drawable.image_avatar_1));
        }
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
    }


}
