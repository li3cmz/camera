package com.seu.magiccamera.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seu.magiccamera.R;

import java.util.ArrayList;
import java.util.List;

public class ModeAdapter extends RecyclerView.Adapter<ModeAdapter.ViewHolder>{
    //构造
    private List<String> mlist;
    private List<Boolean> isClicks;// 控件是否位于位置1，也就是中间位置，转化为最后一个item是否为3,是的话拍照位于中间位置，否则是video位于中间位置。
                                    // 是默认为false，如果被点击，改变值，控件根据值改变自身颜色。


    public void setIsClicks(List<Boolean>curClicks){
        isClicks = curClicks;
    }

    public ModeAdapter(List<String> mList) {
        this.mlist = mList;
        isClicks = new ArrayList<>();
        for(int i = 0;i<mlist.size();i++){
            isClicks.add(false);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //绑定行布局
        View view = View.inflate(parent.getContext(), R.layout.mode_selected_item,null);
        //实例化ViewHolder
       ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    //设置数据
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //获取当前实体类对象
        String vo = mlist.get(position);
        //设置
        holder.text.setText(vo);
    }

    //数量
    @Override
    public int getItemCount() {
        return mlist.size();
    }

    //内部类
    class ViewHolder extends RecyclerView.ViewHolder{
        //行布局中的控件

        TextView text;
        public ViewHolder(View itemView) {
            super(itemView);
            //绑定控件
            text = (TextView) itemView.findViewById(R.id.recycleItemText);
        }
    }

}
