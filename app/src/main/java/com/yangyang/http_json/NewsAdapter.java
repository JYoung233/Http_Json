package com.yangyang.http_json;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by asus on 2016/3/2.
 */
public class NewsAdapter extends BaseAdapter {
    private List<NewsBean> mlist;
    private LayoutInflater mlayoutInflater;
    private ImageLoader imageLoader=new ImageLoader();//保证不出现多个cache

    public NewsAdapter(Context context, List<NewsBean> data) {
        mlist = data;
        mlayoutInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
           ViewHolder viewHolder=null;
           if(convertView==null){
               viewHolder=new ViewHolder();
               convertView=mlayoutInflater.inflate(R.layout.item,null);
               viewHolder.im= (ImageView) convertView.findViewById(R.id.im_icon);
               viewHolder.title= (TextView) convertView.findViewById(R.id.tv_title);
               viewHolder.content= (TextView) convertView.findViewById(R.id.tv_content);
               convertView.setTag(viewHolder);
           }
        else{
               viewHolder= (ViewHolder) convertView.getTag();
           }
         viewHolder.im.setImageResource(R.mipmap.ic_launcher);
         viewHolder.im.setTag(mlist.get(position).getNewsiconurl());//item图片出现错乱的原因是，list正确的item没有对应正确的url
        imageLoader.ShowImageByAsyncask(viewHolder.im,mlist.get(position).getNewsiconurl());
         viewHolder.content.setText(mlist.get(position).getNewscontent());
         viewHolder.title.setText(mlist.get(position).getNewstitle());
           return convertView;
    }
    class ViewHolder{//这是类，不要再加括号了！！！！
        public TextView title,content;
        public ImageView im;

    }
}
