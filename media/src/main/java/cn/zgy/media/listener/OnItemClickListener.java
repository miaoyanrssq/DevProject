package cn.zgy.media.listener;

import android.view.View;

import cn.zgy.photoview.Info;


public interface OnItemClickListener {

    /**
     * item 点击回调
     *
     * @param itemView {@link android.support.v7.widget.RecyclerView.ViewHolder#itemView}
     * @param position .
     */
    void onItemClick(View itemView, int position);
}
