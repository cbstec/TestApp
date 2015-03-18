package com.example.owner.testapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.googlecode.tesseract.android.TessBaseAPI;


public class MainActivity extends ActionBarActivity {

    String TAG = MainActivity.class.getSimpleName();

    String EXSTORAGE_PATH = null;
    String TESSDATA_PATH = null;
    String TRAIN_LANG = null;
    String TRAINEDDATA = null;
    String checktxt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EXSTORAGE_PATH = String.format("%s/%s", getFilesDir().toString(), "TestApp/");
        TESSDATA_PATH = String.format("%s%s", EXSTORAGE_PATH, "tessdata/");
        TRAIN_LANG = "eng";
        TRAINEDDATA = String.format("%s.traineddata", TRAIN_LANG);

        try {
            prepareTrainedFileIfNotExist();
        } catch (Exception e) {
            Log.e("err", e.toString());
        }

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bmp = null;
                TextView tv = (TextView) findViewById(R.id.textView);
                ImageView iv = (ImageView) findViewById(R.id.imageView);
                AssetManager as = getResources().getAssets();

                tv.setText(checktxt);

                try {
                    InputStream ist = as.open("images/HelloWorld.png");
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
                ocr.end();
            }
        });




    }

    private void prepareTrainedFileIfNotExist() throws Exception {

        // MEMO : Manifestの android.permission.WRITE_EXTERNAL_STORAGEを忘れずに

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
}
