package com.seu.magiccamera.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.seu.magiccamera.R;
import com.seu.magiccamera.helper.AudioItemHelper;
import com.seu.magiccamera.helper.AudioItemType;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder>{

    private AudioItemType[] mlist;
    private int selected = 0;
    private Context context;
    public  AudioAdapter(Context context, AudioItemType[] mlist) {
        this.mlist = mlist;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        //绑定行布局
        View view = LayoutInflater.from(context).inflate(R.layout.audio_item_layout,
                parent, false);
        //实例化ViewHolder
        ViewHolder holder = new ViewHolder(view);
        //绑定控件
        holder.imageView = (ImageView) view.findViewById(R.id.item_image);
        holder.audio_selected_view = (FrameLayout)view.findViewById(R.id.item_image_selected);
        holder.audioItem_root = (FrameLayout)view.findViewById(R.id.audioItem_root);
        return holder;
    }

    //设置数据
    @Override
    public void onBindViewHolder(AudioAdapter.ViewHolder holder, final int position){
        //获取当前实体类对象
        holder.imageView.setImageResource(AudioItemHelper.AudioType2Type(mlist[position]));
        if (position==selected){
            holder.audio_selected_view.setVisibility(View.VISIBLE);
        }
        else{
            holder.audio_selected_view.setVisibility(View.GONE);
        }

        holder.audioItem_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected == position)
                    return;
                int lastSelected = selected;
                selected = position;
                notifyItemChanged(lastSelected);
                notifyItemChanged(position);
                onAudioChangeListener.onAudioChanged(mlist[position]);
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
        FrameLayout audio_selected_view;
        FrameLayout audioItem_root;
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface onAudioChangeListener{
        void onAudioChanged(AudioItemType filterType);
    }

    private onAudioChangeListener onAudioChangeListener;

    public void setOnAudioChangeListener(onAudioChangeListener onAudioChangeListener){
        this.onAudioChangeListener = onAudioChangeListener;
    }
}
