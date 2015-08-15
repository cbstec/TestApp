package com.example.owner.testapp;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * activity_main内では外部にアクセスできないため作成した外部通信用のクラス
 */
public class AsyncHttpRequest extends AsyncTaskLoader<String> {

    /**
     * URL
     */
    private String urlStr = "";

    /**
     *  処理に必要な値をコンストラクタで指定
     * @param context MainActivityのgetApplication
     * @param urlStr 取得に必要なURL
     */
    public AsyncHttpRequest(Context context, String urlStr){
        super(context);
        this.urlStr = urlStr;
    }

    /**
     * URLからソースを取得する
     * @return
     */
    @Override
    public String loadInBackground(){

        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection connection = null;
        InputStream is = null;
        try {

            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            is = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder src = new StringBuilder("");

        try {
            while(true){
                byte[] line = new byte[1024];
                int size = 0;

                size = is.read(line);

                if (size <= 0) {
                    break;
                }
                src.append(new String(line, "UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            connection.disconnect();
        }
        return src.toString();
    }

    public String getUrl(){
        return urlStr;
    }
}
