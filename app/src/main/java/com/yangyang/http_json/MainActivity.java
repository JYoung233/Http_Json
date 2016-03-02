package com.yangyang.http_json;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ListView mlist;
    private static String Url="http://www.imooc.com/api/teacher?type=4& num=30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mlist= (ListView) findViewById(R.id.list1);
        new MyAsyncTask().execute(Url);

    }
    class MyAsyncTask extends AsyncTask<String,Void,List<NewsBean>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<NewsBean> newsBeans) {
            super.onPostExecute(newsBeans);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected List<NewsBean> doInBackground(String... params) {
            return getJsonData(params[0]);//根据url获取json数据
        }
    }

    private List<NewsBean> getJsonData(String url) {//根据inputstream获取json格式的字符串
        List<NewsBean> list=new ArrayList<>();
        try {
            String jsonstring=readStream(new URL(url).openStream());
            Log.d("xys",jsonstring);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
    private String readStream(InputStream is){
        InputStreamReader isr;
        String result="";
        try {
            String line="";
            isr=new InputStreamReader(is,"utf-8");
            BufferedReader br=new BufferedReader(isr);
            try {
                while((line=br.readLine())!=null){
                    result+=line;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;

    }

}
