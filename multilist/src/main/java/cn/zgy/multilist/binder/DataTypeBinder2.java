package cn.zgy.multilist.binder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import cn.zgy.multilist.R;
import cn.zgy.multilist.bean.Data;
import cn.zgy.multitype.ItemViewBinder;

public class DataTypeBinder2 extends ItemViewBinder<Data, DataTypeBinder2.DataHolder>{




    class DataHolder extends RecyclerView.ViewHolder {
        ImageView image;
        public DataHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    @NonNull
    @Override
    protected DataHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_image, parent, false);
        return new DataHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull DataHolder holder, @NonNull Data item) {
        holder.image.setImageResource(item.getResId());
    }
}
