package cn.zgy.news.binder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.zgy.multitype.ItemViewBinder;
import cn.zgy.news.R;
import cn.zgy.news.bean.TextBean;

public class TextBinder extends ItemViewBinder<TextBean, TextBinder.TextHolder>{


    public class TextHolder extends RecyclerView.ViewHolder{
        TextView name;
        public TextHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
        }

    }

    @NonNull
    @Override
    public TextHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_name, parent, false);
        return new TextHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull TextHolder holder, @NonNull TextBean item) {
        holder.name.setText(item.name);

    }


}
