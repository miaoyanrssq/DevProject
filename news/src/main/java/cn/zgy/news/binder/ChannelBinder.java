package cn.zgy.news.binder;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.zgy.multitype.ItemViewBinder;
import cn.zgy.news.R;
import cn.zgy.news.bean.ChannelBean;

public class ChannelBinder extends ItemViewBinder<ChannelBean, ChannelBinder.ChannelHolder>{





    EditOpt editOpt;
    ClickListener clickListener;

    class ChannelHolder extends RecyclerView.ViewHolder{

        TextView tv_text;
        ImageView iv_cross;

        public ChannelHolder(View itemView) {
            super(itemView);
            tv_text = itemView.findViewById(R.id.tv_text);
            iv_cross = itemView.findViewById(R.id.iv_cross);
            itemView.setOnClickListener(onClickListener);
            itemView.setOnLongClickListener(onLongClickListener);
        }

        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return clickListener.onItemLongClick(itemView, getAdapterPosition());
            }
        };

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick(itemView, getAdapterPosition());
            }
        };
    }

    public ChannelBinder(ClickListener clickListener, EditOpt editOpt) {
        this.clickListener = clickListener;
        this.editOpt = editOpt;
    }

    @NonNull
    @Override
    protected ChannelHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.module_news_item_channel, parent, false);
        return new ChannelHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChannelHolder holder, @NonNull ChannelBean item) {

        holder.tv_text.setText(item.getName());
        Drawable background = holder.itemView.getBackground();
        if(background != null){
            if (item.isCollapsed()) {
                background.setLevel(3);
            } else if (item.isSelected()) {
                background.setLevel(1);
            } else {
                background.setLevel(2);
            }
        }
        holder.iv_cross.setImageResource(R.drawable.module_news_ic_channel_cross);
        if(editOpt.isEditable()){
            holder.iv_cross.setVisibility(View.VISIBLE);
        }else{
            holder.iv_cross.setVisibility(View.GONE);
        }

    }


    public interface ClickListener{
        void onItemClick(View view, int position);
        boolean onItemLongClick(View view, int position);
    }

    public interface EditOpt{
        boolean isEditable();

        void switchEditState(boolean editable);
    }

}
