package cn.zgy.news;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.zgy.base.BaseActivity;
import cn.zgy.multitype.Items;
import cn.zgy.multitype.MultiTypeAdapter;
import cn.zgy.news.bean.ConnectBean;
import cn.zgy.news.bean.TextBean;
import cn.zgy.news.binder.ConnectBinder;
import cn.zgy.news.binder.TextBinder;
import cn.zgy.news.view.PinnedHeaderItemDecoration;

public class ConnectActivity extends BaseActivity {
    @BindView(R2.id.iv_back)
    ImageView ivBack;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.recycler)
    RecyclerView recycler;

    MultiTypeAdapter adapter;
    Items items = new Items();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_connect;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        tvTitle.setText("联系人");
        initRecycleView();
    }

    private void initRecycleView() {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.addItemDecoration(new PinnedHeaderItemDecoration());
        adapter = new MultiTypeAdapter();
        adapter.register(TextBean.class, new TextBinder());
        adapter.register(ConnectBean.class, new ConnectBinder());
        recycler.setAdapter(adapter);

        initData();
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
    }

    private void initData() {
        for(int i=0 ; i< 50 ; i++){
            if(i % 5 == 0){
                items.add(new TextBean("title" + i));
            }else{
                items.add(new ConnectBean("张三" + i, "12345678905"));
            }
        }
    }

    @OnClick(R2.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
