package com.yangyang.http_json;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by asus on 2016/3/2.
 */
public class ImageLoader {
    private ImageView imageView;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            imageView.setImageBitmap((Bitmap) msg.obj);
        }
    };
    public void ImageLoadThread(ImageView im,final String IconUrl){
        imageView=im;
        new Thread(){
            @Override
            public void run() {
                super.run();
                Bitmap bitmap=BitmapFromUrl(IconUrl);
                Message msg=Message.obtain();
                msg.obj=bitmap;
                mHandler.sendMessage(msg);

            }
        }.start();
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
