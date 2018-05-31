package com.android.fisher.sgface.activity;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;

import com.mwj.lhn.sgdk.R;
import com.android.fisher.sgface.tool.PrefName;
import com.android.fisher.sgface.util.PreferencesUtils;
import com.android.fisher.sgface.util.ToolUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

public class WelcomeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome1);
        verifyStoragePermissions(WelcomeActivity.this);
        getBaiduToken();
    }

//读取百度的token后调用timer
    private void getBaiduToken(){
        String url = PrefName.BAIDU_AI_URL;
        OkHttpUtils
                .get()
                .url(url)
                .addParams("grant_type",PrefName.BAIDU_GRANT_TYPE)
                .addParams("client_id", PrefName.BAIDU_API_KEY)
                .addParams("client_secret",PrefName.BAIDU_SECRECT_KEY)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String baiduToken = ToolUtil.trimNull(jsonObject.get("access_token").getAsString());
                        if(!baiduToken.equals("")){
                            PreferencesUtils.putString(WelcomeActivity.this,"BAIDU_TOKEN",baiduToken);
                            startTimer();
                        }
                    }

                });
    }
//自动转到下一页
    private static Handler handler = new Handler(Looper.myLooper());
    private void startTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        goToNextActivity();
                    }
                });
            }
        },5000);
    }
    private void goToNextActivity(){
        JumpToActivity(LoginActivity.class);
        finish();
    }
//写权限
    private static final int REQUEST_EXTERNAL_STORAGE1 = 1;
    private static String[] PERMISSIONS_STORAGE1 = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    private static final int REQUEST_EXTERNAL_STORAGE2 = 1;
    private static String[] PERMISSIONS_STORAGE2 = {
            "android.permission.CAMERA" };
    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写SD卡的权限
            int permission1 = ActivityCompat.checkSelfPermission(activity,"android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission1 != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE1,REQUEST_EXTERNAL_STORAGE1);
            }
            //检测是否有相机的权限
            int permission2 = ActivityCompat.checkSelfPermission(activity,"android.permission.CAMERA");
            if (permission2 != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE2,REQUEST_EXTERNAL_STORAGE2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
