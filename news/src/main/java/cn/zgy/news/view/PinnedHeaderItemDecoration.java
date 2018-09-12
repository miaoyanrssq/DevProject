package cn.zgy.news.view;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.w3c.dom.Text;

import java.util.Collections;

import cn.zgy.multitype.ItemViewBinder;
import cn.zgy.multitype.MultiTypeAdapter;
import cn.zgy.news.bean.TextBean;
import cn.zgy.news.binder.TextBinder;

/**
* 实现recycleview分组悬浮列表
* @author zhengy
* create at 2018/9/12 上午10:44
**/
public class PinnedHeaderItemDecoration extends RecyclerView.ItemDecoration{

    private Rect mPinnedHeaderRect     = null;
    private int  mPinnedHeaderPosition = -1;

    /**
     * 把固定的view绘制在上层
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if(parent.getChildCount() > 0){
            MultiTypeAdapter adapter = (MultiTypeAdapter) parent.getAdapter();
            //找到要固定的pin view
            View firstView = parent.getChildAt(0);
            int firstAdapterPosition = parent.getChildAdapterPosition(firstView);
            int  pinnedHeaderPosition = getBinderIndex(firstAdapterPosition, adapter);
            mPinnedHeaderPosition = pinnedHeaderPosition;
            if(pinnedHeaderPosition != -1){
                //获取viewHolder
                TextBinder.TextHolder pinnedHeaderViewHolder = (TextBinder.TextHolder) adapter.onCreateViewHolder(parent,
                        adapter.getItemViewType(pinnedHeaderPosition));
                //更新pinnedHeaderViewHolder中的内容
                TextBinder binder = (TextBinder) adapter.getTypePool().getItemViewBinder(adapter.getItemViewType(pinnedHeaderPosition));
                binder.onBindViewHolder(pinnedHeaderViewHolder, (TextBean) adapter.getItems().get(pinnedHeaderPosition));
                //要固定的view
                View pinnedHeaderView = pinnedHeaderViewHolder.itemView;
                ensurePinnedHeaderViewLayout(pinnedHeaderView, parent);
                int sectionPinOffset = 0;
                for(int index=0 ; index < parent.getChildCount() ; index++){
                    if(adapter.getTypePool().getItemViewBinder(adapter.getItemViewType(index)) instanceof TextBinder){
                        View sectionView = parent.getChildAt(index);
                        int sectionTop = sectionView.getTop();
                        int pinViewHeight = pinnedHeaderView.getHeight();
                        if(sectionTop < pinViewHeight && sectionTop > 0){
                            sectionPinOffset = sectionTop - pinViewHeight;
                        }
                    }
                }

                int saveCount = c.save();
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) pinnedHeaderView.getLayoutParams();
                if (layoutParams == null) {
                    throw new NullPointerException("PinnedHeaderItemDecoration");
                }
                c.translate(layoutParams.leftMargin, sectionPinOffset);
                c.clipRect(0, 0, parent.getWidth(), pinnedHeaderView.getMeasuredHeight());
                pinnedHeaderView.draw(c);
                c.restoreToCount(saveCount);
                if (mPinnedHeaderRect == null) {
                    mPinnedHeaderRect = new Rect();
                }
                mPinnedHeaderRect.set(0, 0, parent.getWidth(), pinnedHeaderView.getMeasuredHeight() + sectionPinOffset);


            }else {
                mPinnedHeaderRect = null;
            }

        }
    }


    private int getBinderIndex(int firstAdapterPosition, MultiTypeAdapter adapter){
        for(int index = firstAdapterPosition ; index >=0 ; index--){
            if(adapter.getTypePool().getItemViewBinder(adapter.getItemViewType(index)) instanceof TextBinder){

                return index;
            }
        }
        return -1;
    }


    private void ensurePinnedHeaderViewLayout(View pinView, RecyclerView recyclerView) {
        if (pinView.isLayoutRequested()) {
            /**
             * 用的是RecyclerView的宽度测量，和RecyclerView的宽度一样
             */
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) pinView.getLayoutParams();
            if (layoutParams == null) {
                throw new NullPointerException("PinnedHeaderItemDecoration");
            }
            int widthSpec = View.MeasureSpec.makeMeasureSpec(
                    recyclerView.getMeasuredWidth() - layoutParams.leftMargin - layoutParams.rightMargin, View.MeasureSpec.EXACTLY);

            int heightSpec;
            if (layoutParams.height > 0) {
                heightSpec = View.MeasureSpec.makeMeasureSpec(layoutParams.height, View.MeasureSpec.EXACTLY);
            } else {
                heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            }
            pinView.measure(widthSpec, heightSpec);
            pinView.layout(0, 0, pinView.getMeasuredWidth(), pinView.getMeasuredHeight());
        }
    }
}
