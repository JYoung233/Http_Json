package com.yangyang.http_json;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by asus on 2016/3/2.
 */
public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener{
    private List<NewsBean> mlist;
    private LayoutInflater mlayoutInflater;
    private ImageLoader imageLoader;//保证不出现多个cache
    private int mStart,mEnd;//
    public static String[] Urls;
    private boolean mFirst;//用来判断是否是第一次启动

    public NewsAdapter(Context context, List<NewsBean> data,ListView listView) {
        mlist = data;
        mlayoutInflater = LayoutInflater.from(context);
        imageLoader=new ImageLoader(listView);
        Urls=new String[data.size()];//创建数组，这里要加强练习
        for (int i = 0; i < data.size(); i++) {
            Urls[i]=data.get(i).getNewsiconurl();
        }
        mFirst=true;//第一次启动
        listView.setOnScrollListener(this);//实现了监听器，一定要记得注册

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

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState==SCROLL_STATE_IDLE){
            //没有滚动项，开始加载
            imageLoader.LoadImages(mStart,mEnd);//没有进行预加载
        }else{
            //停止加载
            imageLoader.CancelAllTask();
        }

    }

    /**
     *
     * @param view 可见视图
     * @param firstVisibleItem 第一个可见元素
     * @param visibleItemCount 可见元素数目
     * @param totalItemCount list 中的总数
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mStart=firstVisibleItem;
        mEnd=firstVisibleItem+visibleItemCount;
        if(mFirst&&visibleItemCount>0){//当前列表第一次启动而且item已经绘出
            imageLoader.LoadImages(mStart,mEnd);
            mFirst=false;
        }

    }

    class ViewHolder{//这是类，不要再加括号了！！！！
        public TextView title,content;
        public ImageView im;

    }
}
