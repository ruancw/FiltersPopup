package com.rcw.popuplib;

import android.content.Context;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;


/**
 * Created by ruancw on 2018/5/31.
 *
 */

public class FilterAdapter extends CommonAdapter<String> {
    private int selection;

    public FilterAdapter(Context context, int layoutId, List<String> dataList) {
        super(context,layoutId,dataList);
        this.selection = -1;
    }

    @Override
    protected void convert(ViewHolder holder, String value, int position) {
        holder.setText(R.id.tv_des,value);
        if(position == selection) {
            holder.setBackgroundRes(R.id.tv_des, R.color.press);
        }else {
            holder.setBackgroundRes(R.id.tv_des, R.color.normal);
        }
    }

    /**
     * 记录选中的position位置
     * @param position 上一次点击的位置
     */
    public void recordSelectPosition(int position) {
        this.selection = position;
    }

}
