/*
 *
 * MenuAdapter.java
 * 
 * Created by Wuwang on 2016/11/14
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.seu.magiccamera.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.seu.magiccamera.R;
import com.seu.magiccamera.helper.MSTHelper;
import com.seu.magiccamera.helper.MSTType;

import java.util.ArrayList;

/**
 * Description:
 */
public class MenuImgAdapter extends RecyclerView.Adapter<MenuImgAdapter.MenuHolder> {

    private Context mContext;
    public ArrayList<MSTType> data;
    public int checkPos=0;
    private int selected = 0;

    public MenuImgAdapter(Context context, ArrayList<MSTType> data){
        this.mContext=context;
        this.data=data;
    }

    @Override
    public MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_small_menu_img,
                parent, false);
        //实例化ViewHolder
        MenuImgAdapter.MenuHolder holder = new MenuImgAdapter.MenuHolder(view);
        //绑定控件
        holder.tv= view.findViewById(R.id.mMenu_Img);
        holder.bgm = view.findViewById(R.id.mMenu_bgm);
        holder.fm = view.findViewById(R.id.stItem_root);
        return holder;
    }

    @Override
    public void onBindViewHolder(MenuHolder holder, final int position) {
        holder.tv.setBackgroundResource(MSTHelper.MSTType2Type(data.get(position)));
        if (position==selected){
            holder.bgm.setVisibility(View.VISIBLE);
        }
        else{
            holder.bgm.setVisibility(View.GONE);
        }

        holder.fm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected == position)
                    return;
                int lastSelected = selected;
                selected = position;
                notifyItemChanged(lastSelected);
                notifyItemChanged(position);
                onMstListener.onMstChanged(data.get(position));
            }
        });


    }

    public interface onMstChangeListener{
        void onMstChanged(MSTType mstData);
    }

    private onMstChangeListener onMstListener;

    public void setOnMstChangeListener(onMstChangeListener onMstListener){
        this.onMstListener = onMstListener;
    }

    @Override
    public int getItemCount() {
        return data!=null?data.size():0;
    }

    public class MenuHolder extends RecyclerView.ViewHolder{

        private ImageView tv;
        private ImageView bgm;
        private FrameLayout fm;

        public MenuHolder(View itemView) {
            super(itemView);
        }
    }

}
