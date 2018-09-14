package cn.zgy.media.binder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cn.zgy.media.R;
import cn.zgy.media.bean.MediaEntity;
import cn.zgy.media.listener.OnItemClickListener;
import cn.zgy.multitype.ItemViewBinder;

public class MediaBinder extends ItemViewBinder<MediaEntity, MediaBinder.MediaHolder> {


    CompoundButton.OnCheckedChangeListener listener;
    OnItemClickListener onItemClickListener;


    class MediaHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView iv_picture;
        CheckBox check_box;
        public MediaHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            iv_picture = itemView.findViewById(R.id.iv_picture);
            check_box = itemView.findViewById(R.id.check_box);
            check_box.setOnCheckedChangeListener(listener);
        }

        @Override
        public void onClick(View view) {
            if(view == itemView){
                if(onItemClickListener != null) {
                    onItemClickListener.onItemClick(view, getAdapterPosition());
                }
            }
        }
    }


    public MediaBinder(CompoundButton.OnCheckedChangeListener listener, OnItemClickListener onItemClickListener) {
        this.listener = listener;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    protected MediaHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.module_item_local_media_select, parent, false);
        return new MediaHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull MediaHolder holder, @NonNull MediaEntity item) {
        if(TextUtils.isEmpty(item.getThumbnail())) {
            Glide.with(holder.iv_picture).load(item.getPath()).into(holder.iv_picture);
        }else{
            Glide.with(holder.iv_picture).load(item.getThumbnail()).into(holder.iv_picture);
        }
        holder.check_box.setTag(null);
        holder.check_box.setChecked(item.isSelected());
        holder.check_box.setTag(item);

    }





}
