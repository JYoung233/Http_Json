package com.yangyang.http_json;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by asus on 2016/3/2.
 *
 */
public class ImageLoader {
    private ImageView imageView;
    private String Url;
    private ListView mlistview;
    private Set<MyAsyncTask> mtask;
    LruCache<String,Bitmap> mCache;//创建cache
    //构造函数
    public ImageLoader(ListView listView) {
        int MaxMemory= (int) Runtime.getRuntime().maxMemory();
        int cachesize=MaxMemory/4;
        mlistview=listView;
        mtask=new HashSet<>();
        mCache=new LruCache<String,Bitmap>(cachesize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //在每次存入缓存时调用，这里要加强理解。
                return value.getByteCount();
            }
        };
    }
    //增加存入和取出缓冲中图像的方法。
    public void addBitmapToCache(String Url,Bitmap bitmap){
        if(getBitmapFromCache(Url)==null){//注意判断条件。
            mCache.put(Url,bitmap);
        }

    }
    public Bitmap getBitmapFromCache(String Url){

        return mCache.get(Url);

    }
    //加载指定
    public void LoadImages(int start,int end){
        for (int i = start; i <end; i++) {
            String Url=NewsAdapter.Urls[i];//获得Url
            Bitmap bitmap=getBitmapFromCache(Url);
            if(bitmap==null){
                MyAsyncTask task=new MyAsyncTask(Url);//因为现在是通过tag找到imageview，所以直接通过imageview设置就不合理
                mtask.add(task);
            }else{
              ImageView imagview= (ImageView) mlistview.findViewWithTag(Url);
                imagview.setImageBitmap(bitmap);

            }
        }

    }
    public void CancelAllTask(){
        if(mtask!=null){
            for (MyAsyncTask task:mtask) {
                task.cancel(false);
            }
        }
    }


    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(imageView.getTag().equals(Url)) {
                imageView.setImageBitmap((Bitmap) msg.obj);//不可以在子线程中直接修改UI，单线程模型，创建handler
            }
        }
    };
    public void ImageLoadThread(ImageView im,final String IconUrl){
        Url=IconUrl;
        imageView=im;

            new Thread() {
                @Override
                public void run() {
                    super.run();
                    Bitmap bitmap = BitmapFromUrl(IconUrl);

                    Message msg = Message.obtain();//使用现有的回收message,提高使用效率
                    msg.obj = bitmap;
                    mHandler.sendMessage(msg);

                }
            }.start();

    }
    //不通过这里触发。
    public void ShowImageByAsyncask(ImageView imageView,String Url){
            Bitmap bitmap=getBitmapFromCache(Url);
        if(bitmap==null){
            imageView.setImageResource(R.mipmap.ic_launcher);

        }
        else{
            imageView.setImageBitmap(bitmap);
        }

    }
    private class MyAsyncTask extends AsyncTask<String,Void,Bitmap>{
        //private ImageView im;
        private String url;
        public MyAsyncTask(String Url) {
            super();
            //im=imageView1;
            url=Url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
              if(getBitmapFromCache(url)==null){
                  addBitmapToCache(url,bitmap);
              }
             ImageView im= (ImageView) mlistview.findViewWithTag(Url);
              if(bitmap!=null&&im!=null&&im.getTag().equals(url)){
                  im.setImageBitmap(bitmap);
              }
            mtask.remove(this);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
           if(getBitmapFromCache(params[0])==null){
            return BitmapFromUrl(params[0]);}
            else{
               return getBitmapFromCache(params[0]);
           }
        }
    }
    public Bitmap BitmapFromUrl(String url){
        Bitmap bm=null;
        InputStream is=null;
        try {
            HttpURLConnection hcn= (HttpURLConnection) new URL(url).openConnection();
            is=new BufferedInputStream(hcn.getInputStream());
            bm= BitmapFactory.decodeStream(is);

            hcn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bm;
    }
}
