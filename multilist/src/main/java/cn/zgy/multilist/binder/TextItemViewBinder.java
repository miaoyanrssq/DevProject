/*
 * Copyright 2016 drakeet. https://github.com/drakeet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.zgy.multilist.binder;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import cn.zgy.base.listener.OnItemClickListener;
import cn.zgy.multilist.R;
import cn.zgy.multilist.bean.TextItem;
import cn.zgy.multitype.ItemViewBinder;


/**
 * @author drakeet
 */
public class TextItemViewBinder extends ItemViewBinder<TextItem, TextItemViewBinder.TextHolder> {

  private int lastShownAnimationPosition;
  OnItemClickListener click;


  class TextHolder extends RecyclerView.ViewHolder {

    TextView text;


    TextHolder(@NonNull View itemView) {
      super(itemView);
      text = itemView.findViewById(R.id.text);
      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          click.onItemClick(view, getAdapterPosition());
        }
      });
    }
  }

  public TextItemViewBinder(OnItemClickListener click) {
    this.click = click;
  }

  @Override
  protected @NonNull
  TextHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
    View root = inflater.inflate(R.layout.item_text, parent, false);
    return new TextHolder(root);
  }


  @Override
  @SuppressLint("SetTextI18n")
  protected void onBindViewHolder(@NonNull TextHolder holder, @NonNull TextItem textItem) {
    holder.text.setText(textItem.text);

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
  public void onViewDetachedFromWindow(@NonNull TextHolder holder) {
    holder.itemView.clearAnimation();
  }
}
