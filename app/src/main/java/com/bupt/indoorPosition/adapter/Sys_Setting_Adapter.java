package com.bupt.indoorPosition.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.bupt.indooranalysis.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class Sys_Setting_Adapter extends BaseAdapter{
	private boolean isTicked;
    // 填充数据的list
    private ArrayList<String> list;
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer,Boolean> isSelected;
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;
    private View itemview;
    
    // 构造器
    public Sys_Setting_Adapter(ArrayList<String> list, Context context,boolean isTicked) {
        this.context = context;
        this.list = list;
        this.isTicked = isTicked;
        inflater = LayoutInflater.from(context);
        isSelected = new HashMap<Integer, Boolean>();
        // 初始化数据
        initDate();
    }

    // 初始化isSelected的数据
    private void initDate(){
        for(int i=0; i<list.size();i++) {
            getIsSelected().put(i,false);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
            if (convertView == null) {
            // 获得ViewHolder对象
            holder = new ViewHolder();
            // 导入布局并赋值给convertview
            convertView = inflater.inflate(R.layout.system_setting_item, null);
            holder.tv = (TextView) convertView.findViewById(R.id.item_tv);
            holder.cb = (CheckBox) convertView.findViewById(R.id.system_setting_checkBox);
            // 为view设置标签
            convertView.setTag(holder);
        } else {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
            }


        // 设置list中TextView的显示
        holder.tv.setText(list.get(position).toString());
        // 根据isSelected来设置checkbox的选中状况
        holder.cb.setChecked(isTicked);
        //holder.cb.setChecked(getIsSelected().get(position));
        itemview = convertView;
        return convertView;
    }
    public View getview(){
    	return itemview;
    }

    public static HashMap<Integer,Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer,Boolean> isSelected) {
    	Sys_Setting_Adapter.isSelected = isSelected;
    }
    public static class ViewHolder
    {
        TextView tv;
        CheckBox cb;
    }

}