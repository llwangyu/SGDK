package com.mwj.lhn.sgdk.mwj;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mwj.lhn.sgdk.R;
import com.mwj.lhn.sgdk.pub.URLManage;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.mwj.lhn.sgdk.pub.Basic.jsonlogin;

public class PhotoListActivity extends Activity {
    private ListView list = null;
    private String msg,tel,sms;
    private static final int MESSAGETYPE_01 = 0x0001;
    private static final String TAG ="22222" ;
    public static ProgressDialog progressDialog = null;
    public static boolean STOP = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomTitle.getCustomTitle(this, "上传列表");

        Bundle bundle = this.getIntent().getExtras();
        msg = bundle.getString("msg");
        tel = bundle.getString("tel");
        sms = bundle.getString("sms");
        ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            getreflash();
        }
        else{
            dialog("网络链接异常，请检查网络！");
        }

    }

    private void getreflash(){
        RequestParams params = new RequestParams();// 绑定参数
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String dtime=format.format(date);
        params.put("tel_number", tel);
        params.put("sendyzm",sms);
        showProgress(PhotoListActivity.this);
        URLManage.showInfos("findDayPlans.json",params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject  responseBody) {
                // TODO Auto-generated method stub
                super.onSuccess(statusCode, headers, responseBody);
                closeProgress();
                try {
                    msg = responseBody.toString();
                    init(msg);

                } catch (Exception ex) {
                }

            }
            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable e, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, e, errorResponse);
                closeProgress();
            }
        });
    }
    private void init(String nmsg){
        List<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

        try {
            JSONObject jsonObject = jsonlogin(nmsg);
            String _value = jsonObject.getString("FDayPlanVOList");
            JSONArray arr = new JSONArray(_value);
            JSONObject jsonObject1 = arr.getJSONObject(0);
            String fdayPlanMores=jsonObject1.getString("fdayPlanMoreVO1s");
            JSONArray arr1 = new JSONArray(fdayPlanMores);
            for(int m=0;m<arr1.length();m++){
                JSONObject jsonObject2 = arr1.getJSONObject(m);
               {
                    if(jsonObject2.getString("tel_number").equals(tel)) {
                        String fdayimgs = jsonObject2.getString("fDayPlanImages");
                        JSONArray arr2 = new JSONArray(fdayimgs);
                        {
                            for (int i = 0; i < arr2.length(); i++) {
                                JSONObject jsonObject3 = arr2.getJSONObject(i);
                                if(!jsonObject3.getString("sdurl").equals("null")) {
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put("itemTitle", (i + 1) + jsonObject3.getString("title"));
                                    map.put("itemText", jsonObject3.getString("sdurl"));
                                    mylist.add(map);
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
        }
        setContentView(R.layout.activity_photo_list);
        list = (ListView) findViewById(R.id.listview);
        SimpleAdapter adapter = new SimpleAdapter(this,
                mylist,//数据源
                R.layout.phote_vlist,//显示布局
                new String[] {"itemTitle", "itemText"}, //数据源的属性字段
                new int[] {R.id.itemTitle,R.id.itemText}); //布局里的控件id
        //添加并且显示
        list.setAdapter(adapter);
        ListClickListener listn = new ListClickListener();
        list.setOnItemClickListener(listn);
    }
    class ListClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String text = parent.getItemAtPosition(position)+"";
            nextdo(text);
            Log.i("tag",text);
        }
    }
    public void nextdo(final String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PhotoListActivity.this);
        builder.setTitle("提示");
        builder.setPositiveButton("查看图片",
                new android.content.DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        try {
                            gotoinfo(title);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                        }

                    }
                });
        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();

    }

    public void gotoinfo(String title) {
        title=title.replace("{", "");
        title=title.replace("}", "");
        String[] msgs=title.split(",");

        String imageUrl=msgs[0].substring(msgs[0].indexOf("=")+1);
        String imtext=msgs[1].substring(msgs[1].indexOf("=")+1);

        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        bundle.putString("info", title);
        bundle.putString("imageUrl", imageUrl);
        bundle.putString("imtext", imtext);
        intent.putExtras(bundle);
        intent.setClass(PhotoListActivity.this, ShowImgActivity.class);
        startActivity(intent);
    }
    private static Handler handler = new Handler() {

        public void handleMessage(Message message) {
            switch (message.what) {
                case MESSAGETYPE_01:
                    // 刷新UI，显示数据，并关闭进度条
                    progressDialog.dismiss(); // 关闭进度条
                    break;
            }
        }
    };

    public static void showProgress(Context lg) {
        STOP = false;
        progressDialog = ProgressDialog.show(lg, "请稍候", "正在接收数据！！");
        progressDialog.setCancelable(true);

        Thread t = new Thread() {
            public void run() {
                while (!STOP) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception ex) {
                    }
                }
                Message msg_listData = new Message();
                msg_listData.what = MESSAGETYPE_01;
                handler.sendMessage(msg_listData);
            }
        };
        t.start();

    }

    public static void closeProgress() {
        STOP = true;
    }
    public  void dialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PhotoListActivity.this);
        builder.setMessage(text);
        builder.setTitle("提示");
        builder.setPositiveButton("确认",
                new android.content.DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();

    }
}


