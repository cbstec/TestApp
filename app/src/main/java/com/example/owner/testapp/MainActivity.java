package com.example.owner.testapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Environment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.googlecode.tesseract.android.TessBaseAPI;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<String>{

    String TAG = MainActivity.class.getSimpleName();

    String EXSTORAGE_PATH = "";
    String TESSDATA_PATH = "";
    String TRAIN_LANG = "";
    String TRAINEDDATA = "";
    String checktxt = "";

    String urlStr = "http://www.mizuhobank.co.jp/takarakuji/loto/loto6/index.html";
    String kStr = "";
    String dStr = "";
    String[] noArray = new String[6];
    String luckyNo = "";

    AsyncHttpRequest as = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EXSTORAGE_PATH = String.format("%s/%s", getFilesDir().toString(), "TestApp/");
        TESSDATA_PATH = String.format("%s%s", EXSTORAGE_PATH, "tessdata/");
        TRAIN_LANG = "eng";
        TRAINEDDATA = String.format("%s.traineddata", TRAIN_LANG);

        urlStr = "http://www.mizuhobank.co.jp/takarakuji/loto/loto6/index.html";

        getSupportLoaderManager().initLoader(0, null, this);
        Log.d("", as.getUrl());

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Bitmap bmp = null;
                TextView tv = (TextView) findViewById(R.id.textView);
                /*ImageView iv = (ImageView) findViewById(R.id.imageView);
                AssetManager as = getResources().getAssets();

                tv.setText(checktxt);

                try {
                    InputStream ist = as.open("images/atariken0001.jpg");
                    bmp = BitmapFactory.decodeStream(ist);
                    iv.setImageBitmap(bmp);
                } catch (IOException e) {
                    Log.e("err", e.toString());
                }
                if(bmp == null){
                    return;
                }

                bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);

                TessBaseAPI ocr = new TessBaseAPI();

                ocr.init(EXSTORAGE_PATH, "eng");
                ocr.setImage(bmp);
                String ocrText = ocr.getUTF8Text();

                tv.setText(ocrText);
                ocr.end();*/

                //setElectedValue();
                tv.setText(compareNo());

            }
        });

    }

    private void prepareTrainedFileIfNotExist () throws Exception{
        String paths[] = {EXSTORAGE_PATH, EXSTORAGE_PATH + "tessdata"};

        for (String path : paths) {
            File dir = new File(path);
            Log.d(null, path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    checktxt = Boolean.toString(Environment.getExternalStorageDirectory().exists());
                    throw new Exception("ディレクトリ生成に失敗");

                }
            }
        }

        String traineddata_path = String.format("%s%s", TESSDATA_PATH, TRAINEDDATA);

        if ( (new File(traineddata_path).exists()))
            return;

        try {
            InputStream   in = getAssets().open("tessdata/" + TRAINEDDATA);
            OutputStream out = new FileOutputStream(traineddata_path);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            checktxt = traineddata_path;
            Log.e(TAG, e.toString());
            throw new Exception("アセットのコピーに失敗");
        }
    }

    public String compareNo (){
        String prize = "hazure";
        int matchCnt = 0;

        EditText[] edit ={ (EditText)findViewById(R.id.editText),
                (EditText)findViewById(R.id.editText2),
                (EditText)findViewById(R.id.editText3),
                (EditText)findViewById(R.id.editText4),
                (EditText)findViewById(R.id.editText5),
                (EditText)findViewById(R.id.editText6)};

        for(int i = 0; i < edit.length; i++){
            String no = edit[i].getText().toString();
            for(int j = 0; j < noArray.length; j++){
                if(no.equals(noArray[j])){
                    matchCnt++;
                    break;
                }
            }
        }

        if(matchCnt == 3){
            prize = "5等(1,000)";
        }else if(matchCnt == 4){
            prize = "4等(9,500)";
        }else if(matchCnt == 5){
            prize = "3等(500,000)";
        }else if (matchCnt == 6){
            prize = "1等(100,000,000)";
        }
        return prize;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        as = new AsyncHttpRequest(getApplication(), urlStr);
        as.forceLoad();
        return as;
    }

    @Override
    public void onLoadFinished(Loader<String> arg0, String str) {
        String[] srcArray = str.split("\\n");

        boolean outFlg = false;
        int recCnt = 0;
        int arrayNo = 0;
        String src = "";

        for (int i = 0; i < srcArray.length; i++) {
            src = srcArray[i];
            if (src.indexOf("bgf7f7f7") != -1) {
                outFlg = true;
            }

            if (outFlg) {
                if(recCnt < 20){
                    Log.d("", Integer.toString(recCnt));
                    if (recCnt == 0) {
                        kStr = src.replaceAll("<.+?>", "");
                    } else if (recCnt == 6) {
                        dStr = src.replaceAll("<.+?>", "");
                    } else if (recCnt >= 10 && recCnt <= 15) {
                        noArray[arrayNo] = src.replaceAll("<.+?>", "");
                        arrayNo++;
                    } else if (recCnt == 19) {
                        luckyNo = src.replaceAll("<.+?>", "");
                    }
                    recCnt++;

                } else{
                    return;
                }
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<String> arg0) {

    }
}
