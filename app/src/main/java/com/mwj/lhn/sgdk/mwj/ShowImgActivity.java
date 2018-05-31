package com.mwj.lhn.sgdk.mwj;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mwj.lhn.sgdk.R;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.mwj.lhn.sgdk.pub.Basic.jsonlogin;

public class ShowImgActivity extends AppCompatActivity {
    String imageUrl = "";
    Bitmap bmImg;
    Bitmap wbt;
    ImageView imView;
    String title,imtext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomTitle.getCustomTitle(this, "图片信息");

        setContentView(R.layout.activity_show_img);
        imView = (ImageView) findViewById(R.id.tpimageView);
        TextView twv=(TextView)findViewById(R.id.tptitle) ;
        Bundle bundle = this.getIntent().getExtras();
         title = bundle.getString("info");
         imageUrl= bundle.getString("imtext");
         imtext= bundle.getString("imageUrl");
        twv.setText(imtext);
         init();
    }

    private void init() {
        String showinfo=imageUrl;
        System.out.println("show----"+showinfo.indexOf("storage"));
        if(showinfo.indexOf("storage")>0){
            showinfo=imageUrl;
        }
        else{
            showinfo=imtext;
        }

        imView.setImageURI(Uri.parse(showinfo));
        //创建客户端对象


    }


}
