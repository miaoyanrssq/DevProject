package cn.zgy.multilist.binder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import cn.zgy.multilist.R;
import cn.zgy.multilist.bean.Data;
import cn.zgy.multitype.ItemViewBinder;

public class DataTypeBinder1 extends ItemViewBinder<Data, DataTypeBinder1.DataHolder>{

    private int lastShownAnimationPosition;


    class DataHolder extends RecyclerView.ViewHolder {
        TextView text;
        public DataHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
        }
    }

    @NonNull
    @Override
    protected DataHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_text, parent, false);
        return new DataHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull DataHolder holder, @NonNull Data item) {
        holder.text.setText(item.getName());

        // should show animation, ref: https://github.com/drakeet/MultiType/issues/149
        setAnimation(holder.itemView, holder.getAdapterPosition());
    }

    private void setAnimation(@NonNull View viewToAnimate, int position) {
        if (position > lastShownAnimationPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastShownAnimationPosition = position;
        }
    }


    @Override
    public void onViewDetachedFromWindow(@NonNull DataHolder holder) {
        holder.itemView.clearAnimation();
    }
}
