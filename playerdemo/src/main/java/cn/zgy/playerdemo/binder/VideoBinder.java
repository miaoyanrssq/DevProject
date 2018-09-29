package cn.zgy.playerdemo.binder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kk.taurus.playerbase.entity.DataSource;

import cn.zgy.base.utils.UIUtils;
import cn.zgy.imageloader.loader.ImageLoader;
import cn.zgy.multitype.ItemViewBinder;
import cn.zgy.playerdemo.R;
import cn.zgy.playerdemo.bean.VideoBean;
import cn.zgy.playerdemo.play.AssistPlayer;

public class VideoBinder extends ItemViewBinder<VideoBean, VideoBinder.VideoHolder> {


    private Context context;
    private int mScreenUseW;
    private RecyclerView mRecycler;

    private OnListListener onListListener;
    private OnSwitchWindowListener onSwitchWindowListener;
    private int mPlayPosition = -1;
    private int mVerticalRecyclerStart;
    private int mScreenUseH;

    boolean isHide = true;//标志播放item是否隐藏，因为mPlayPosition = -1，初始化后，会调用onScrolled，因此在此设置未true，避免初始化时FloatWindow跳出来

    public static class VideoHolder extends RecyclerView.ViewHolder {
        View card;
        public FrameLayout layoutContainer;
        public RelativeLayout layoutBox;
        View albumLayout;
        ImageView albumImage;
        TextView title;

        public VideoHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            layoutContainer = itemView.findViewById(R.id.layoutContainer);
            layoutBox = itemView.findViewById(R.id.layBox);
            albumLayout = itemView.findViewById(R.id.album_layout);
            albumImage = itemView.findViewById(R.id.albumImage);
            title = itemView.findViewById(R.id.tv_title);
        }
    }

    public VideoBinder(final Context context, RecyclerView recyclerView) {
        this.context = context;
        this.mRecycler = recyclerView;
        mScreenUseW = UIUtils.getScreenW() - UIUtils.dip2px(2 * 6);
        mScreenUseH = UIUtils.getScreenH();
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int[] location = new int[2];
                mRecycler.getLocationOnScreen(location);
                mVerticalRecyclerStart = location[1];
                mRecycler.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int itemVisibleRectHeight = getItemVisibleRectHeight(mPlayPosition);
                if (itemVisibleRectHeight <= 0) {
                    getAdapter().notifyItemChanged(mPlayPosition);
                    if (onSwitchWindowListener != null && !isHide) {
                        onSwitchWindowListener.onSwitchWindow(true);
                    }
                    isHide = true;
                } else {
                    if (onSwitchWindowListener != null && isHide) {
                        onSwitchWindowListener.onSwitchWindow(false);
                    }
                    isHide = false;
                }
            }
        });
    }

    @NonNull
    @Override
    protected VideoHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new VideoHolder(inflater.inflate(R.layout.item_video, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull final VideoHolder holder, @NonNull final VideoBean item) {
        //阴影
        ViewCompat.setElevation(holder.card, UIUtils.dip2px(3));
        updateWH(holder);
        if (!TextUtils.isEmpty(item.getCover())) {
            ImageLoader.with(context)
                    .url(item.getCover())
                    .into(holder.albumImage);
        } else {
            ImageLoader.with(context)
                    .url(item.getPath())
                    .into(holder.albumImage);
        }
        holder.title.setText(item.getDisplayName());
        holder.layoutContainer.removeAllViews();
        holder.albumLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePlayPosition(getPosition(holder));
                playPosition(holder, item);
                if (onSwitchWindowListener != null && isHide) {
                    onSwitchWindowListener.onSwitchWindow(false);
                }
            }
        });

        if (onListListener != null) {
            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePlayPosition(getPosition(holder));
                    onListListener.onTitleClick(item, getPosition(holder));
                }
            });
        }


    }

    private void playPosition(final VideoHolder holder, VideoBean item) {

        final DataSource dataSource = new DataSource(item.getPath());
        dataSource.setTitle(item.getDisplayName());
        mRecycler.post(new Runnable() {
            @Override
            public void run() {
                AssistPlayer.get(context).play(holder.layoutContainer, dataSource);
            }
        });

    }

    public void updatePlayPosition(int position) {
        getAdapter().notifyItemChanged(mPlayPosition);
        mPlayPosition = position;
    }

    private void updateWH(VideoHolder holder) {
        ViewGroup.LayoutParams layoutParams = holder.layoutBox.getLayoutParams();
        layoutParams.width = mScreenUseW;
        layoutParams.height = mScreenUseW * 9 / 16;
        holder.layoutBox.setLayoutParams(layoutParams);
    }


    public void setOnListListener(OnListListener onListListener) {
        this.onListListener = onListListener;
    }

    public interface OnListListener {
        void onTitleClick(VideoBean item, int position);
    }

    public void setOnSwitchWindowListener(OnSwitchWindowListener onSwitchWindowListener) {
        this.onSwitchWindowListener = onSwitchWindowListener;
    }

    public interface OnSwitchWindowListener {
        void onSwitchWindow(boolean isWndow);
    }

    /**
     * 获取Item中渲染视图的可见高度
     *
     * @param position
     * @return
     */
    private int getItemVisibleRectHeight(int position) {
        VideoHolder itemHolder = getItemHolder(position);
        if (itemHolder == null)
            return 0;
        int[] location = new int[2];
        itemHolder.layoutBox.getLocationOnScreen(location);
        int height = itemHolder.layoutBox.getHeight();

        int visibleRect;
        if (location[1] <= mVerticalRecyclerStart) {
            visibleRect = location[1] - mVerticalRecyclerStart + height;
        } else {
            if (location[1] + height >= mScreenUseH) {
                visibleRect = mScreenUseH - location[1];
            } else {
                visibleRect = height;
            }
        }
        return visibleRect;
    }

    public VideoHolder getItemHolder(int position) {
        RecyclerView.ViewHolder viewHolder = mRecycler.findViewHolderForLayoutPosition(position);
        if (viewHolder != null && viewHolder instanceof VideoHolder) {
            return ((VideoHolder) viewHolder);
        }
        return null;
    }

    public VideoHolder getItemHolder() {
        RecyclerView.ViewHolder viewHolder = mRecycler.findViewHolderForLayoutPosition(mPlayPosition);
        if (viewHolder != null && viewHolder instanceof VideoHolder) {
            return ((VideoHolder) viewHolder);
        }
        return null;
    }
}
