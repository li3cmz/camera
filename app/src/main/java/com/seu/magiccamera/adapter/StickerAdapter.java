package com.seu.magiccamera.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.seu.magiccamera.R;
import com.seu.magiccamera.helper.StickerItemHelper;
import com.seu.magiccamera.helper.StickerItemType;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder>{

    private StickerItemType[] mlist;
    private int selected = 0;
    private Context context;
    public StickerAdapter(Context context, StickerItemType[] mlist) {
        this.mlist = mlist;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        //绑定行布局
        View view = LayoutInflater.from(context).inflate(R.layout.sticker_item_layout,
                parent, false);
        //实例化ViewHolder
        ViewHolder holder = new ViewHolder(view);
        //绑定控件
        holder.imageView = (ImageView) view.findViewById(R.id.item_image1);
        holder.sticker_selected_view = (FrameLayout)view.findViewById(R.id.item_image_selected1);
        holder.stickerItem_root = (FrameLayout)view.findViewById(R.id.stickerItem_root);
        return holder;
    }

    //设置数据
    @Override
    public void onBindViewHolder(StickerAdapter.ViewHolder holder, final int position){
        //获取当前实体类对象
        holder.imageView.setImageResource(StickerItemHelper.StickerType2Type(mlist[position]));
        if (position==selected){
            holder.sticker_selected_view.setVisibility(View.VISIBLE);
        }
        else{
            holder.sticker_selected_view.setVisibility(View.GONE);
        }

        holder.stickerItem_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected == position)
                    return;
                int lastSelected = selected;
                selected = position;
                notifyItemChanged(lastSelected);
                notifyItemChanged(position);
                onStickerChangeListener.onStickerChanged(mlist[position]);
            }
        });
    }
    //数量
    @Override
    public int getItemCount() {
        return mlist == null ? 0 : mlist.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        FrameLayout sticker_selected_view;
        FrameLayout stickerItem_root;
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface onStickerChangeListener{
        void onStickerChanged(StickerItemType filterType);
    }

    private onStickerChangeListener onStickerChangeListener;

    public void setOnStickerChangeListener(onStickerChangeListener onStickerChangeListener){
        this.onStickerChangeListener = onStickerChangeListener;
    }
}
