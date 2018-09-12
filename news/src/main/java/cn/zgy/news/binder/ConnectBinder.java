package cn.zgy.news.binder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.zgy.multilist.bean.ImageItem;
import cn.zgy.multilist.binder.ImageItemViewBinder;
import cn.zgy.multitype.ItemViewBinder;
import cn.zgy.news.R;
import cn.zgy.news.bean.ConnectBean;

public class ConnectBinder extends ItemViewBinder<ConnectBean, ConnectBinder.ConnectHolder> {


    class ConnectHolder extends RecyclerView.ViewHolder {

       private TextView name;
       private TextView number;


        ConnectHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            number = itemView.findViewById(R.id.number);

        }
    }


    @Override
    protected @NonNull
    ConnectBinder.ConnectHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_connect, parent, false);
        return new ConnectBinder.ConnectHolder(root);
    }


    @Override
    protected void onBindViewHolder(@NonNull ConnectBinder.ConnectHolder holder, @NonNull ConnectBean connectBean) {
        holder.name.setText(connectBean.name);
        holder.number.setText(connectBean.number);
    }
}
