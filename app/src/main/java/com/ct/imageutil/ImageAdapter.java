package com.ct.imageutil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import image.ImageLoader;

/**
 * Created by koudai_nick on 2017/5/25.
 */

public class ImageAdapter extends BaseAdapter {


    private  Context context;
    private List<String> data;
    private String dirpath;
    private LayoutInflater mLayoutInFlater;



     public ImageAdapter(Context context, List<String> data,String dirpath){
         this.context=context;
         this.data=data;
         this.dirpath=dirpath;
         mLayoutInFlater=LayoutInflater.from(context);
     }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //重点  复用的处理
        ViewHolder viewHolde=null;
        if(convertView==null){
            convertView=mLayoutInFlater.inflate(R.layout.grid_item,parent,false);
            viewHolde=new ViewHolder();
            viewHolde.imageView= (ImageView) convertView.findViewById(R.id.id_item_image);
            convertView.setTag(viewHolde);
        }else{
            viewHolde= (ViewHolder) convertView.getTag();
        }
        viewHolde.imageView.setImageResource(R.drawable.pictures_no);
        ImageLoader.getInstance(context).loadImage(viewHolde.imageView,dirpath+"/"+data.get(position));
        return convertView;
    }
    class ViewHolder{
        //便于复用
        ImageView imageView;
    }
}
