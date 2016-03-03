package com.yangyang.http_json;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by asus on 2016/3/2.
 *
 */
public class ImageLoader {
    private ImageView imageView;
    private String Url;
    LruCache<String,Bitmap> mCache;//创建cache

    public ImageLoader() {
        int MaxMemory= (int) Runtime.getRuntime().maxMemory();
        int cachesize=MaxMemory/4;
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
    public void ShowImageByAsyncask(ImageView imageView,String Url){
            MyAsyncTask task=new MyAsyncTask(imageView,Url);
            task.execute(Url);

    }
    private class MyAsyncTask extends AsyncTask<String,Void,Bitmap>{
        private ImageView im;
        private String url;
        public MyAsyncTask(ImageView imageView1,String Url) {
            super();
            im=imageView1;
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
              if(bitmap!=null&&im.getTag().equals(url)){
                  im.setImageBitmap(bitmap);
              }
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
