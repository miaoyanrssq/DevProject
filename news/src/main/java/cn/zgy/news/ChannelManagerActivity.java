package cn.zgy.news;

import android.app.Service;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.zgy.autoview.GridSpacingItemDecoration;
import cn.zgy.base.BaseActivity;
import cn.zgy.multitype.Items;
import cn.zgy.multitype.MultiTypeAdapter;
import cn.zgy.news.bean.ChannelBean;
import cn.zgy.news.binder.ChannelBinder;
import cn.zgy.news.callback.DefaultItemTouchCallback;
import cn.zgy.news.callback.ItemTouchCallback;

public class ChannelManagerActivity extends BaseActivity implements ChannelBinder.ClickListener, ChannelBinder.EditOpt, ItemTouchCallback{

    private List<ChannelBean> channels1 = new ArrayList<>();
    private List<ChannelBean> channels2 = new ArrayList<>();

    RecyclerView recyclerView;
    MultiTypeAdapter adapter;
    Items items = new Items();

    boolean isEditAble = false;

    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_managerchannel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initChannels();

        initView();
        mItemTouchHelper = new ItemTouchHelper(new DefaultItemTouchCallback(this));
        mItemTouchHelper.attachToRecyclerView(recyclerView);

    }



    private void initChannels() {
        channels1.clear();
        channels1.add(new ChannelBean((long)1, "新闻", true, true, "normal", "", 1));
        channels1.add(new ChannelBean((long)2, "视频", true, true, "normal", "", 2));
        channels1.add(new ChannelBean((long)3, "直播", true, true, "normal", "", 3));
        channels1.add(new ChannelBean((long)4, "图片", true, true, "normal", "", 4));
        channels1.add(new ChannelBean((long)5, "作者", true, true, "author", "", 5));
        channels1.add(new ChannelBean((long)1, "新闻2", true, true, "normal", "", 6));
        channels1.add(new ChannelBean((long)2, "视频2", true, true, "normal", "", 7));
        channels1.add(new ChannelBean((long)3, "直播2", true, true, "normal", "", 8));
        channels1.add(new ChannelBean((long)4, "图片2", true, true, "normal", "", 9));
        channels1.add(new ChannelBean((long)5, "作者2", true, true, "author", "", 10));
        channels1.add(new ChannelBean((long)1, "新闻3", true, true, "normal", "", 11));
        channels1.add(new ChannelBean((long)2, "视频3", true, true, "normal", "", 12));
        channels1.add(new ChannelBean((long)3, "直播3", true, true, "normal", "", 13));
        channels1.add(new ChannelBean((long)4, "图片3", true, true, "normal", "", 14));
        channels1.add(new ChannelBean((long)5, "作者3", true, true, "author", "", 15));

        channels2.clear();
        channels2.add(new ChannelBean((long)1, "新闻4", true, false, "normal", "", 1));
        channels2.add(new ChannelBean((long)2, "视频4", true, false, "normal", "", 2));
        channels2.add(new ChannelBean((long)3, "直播4", true, false, "normal", "", 3));
        channels2.add(new ChannelBean((long)4, "图片4", true, false, "normal", "", 4));

        items.addAll(channels1);
        items.addAll(channels2);

    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        adapter = new MultiTypeAdapter();
        adapter.register(ChannelBean.class, new ChannelBinder(this, this));
        recyclerView.setAdapter(adapter);

        final GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, 30, true));
        adapter.setItems(items);
        adapter.notifyDataSetChanged();


    }

    @Override
    public void onItemClick(View view, int position) {
        if(isEditAble){
            items.remove(position);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public boolean onItemLongClick(View view, int position) {
        RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
        mItemTouchHelper.startDrag(viewHolder);
        Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(100);

        switchEditState(!isEditAble);
        adapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean isEditable() {
        return isEditAble;
    }

    @Override
    public void switchEditState(boolean editable) {
        isEditAble = editable;

    }

    @Override
    public boolean onMove(int fromPosition, int toPosition) {
        Object o = items.get(toPosition);
        if (o instanceof ChannelBean) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(items, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(items, i, i - 1);
                }
            }
            adapter.notifyItemMoved(fromPosition, toPosition);
        }
        return true;
    }

    @Override
    public void onSwiped(int position) {

    }
}
