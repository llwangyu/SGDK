package com.mwj.lhn.sgdk.mwj;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mwj.lhn.sgdk.R;
import com.mwj.lhn.sgdk.pub.URLManage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.mwj.lhn.sgdk.pub.Basic.jsonlogin;

public class WtlbActivity extends AppCompatActivity implements
        View.OnClickListener {
    private ListView list = null;
    private String msg, tel,pid,wtlb;

    private static final int MESSAGETYPE_01 = 0x0001;
    private static final String TAG ="22222" ;
    public static ProgressDialog progressDialog = null;
    public static boolean STOP = true;
    public Button wtlr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomTitle.getCustomTitle(this, "问题列表");
        setContentView(R.layout.activity_wtlb);

        Bundle bundle = this.getIntent().getExtras();
        msg = bundle.getString("msg");
        tel = bundle.getString("tel");
        pid = bundle.getString("pid");
        wtlr=findViewById(R.id.buttonwtlr1);
        wtlr.setOnClickListener(this);
        getreflash();
    }
    public void onStart() {
        super.onStart();
        getreflash();

    }
    private void getreflash(){

        RequestParams params = new RequestParams();// 绑定参数
        params.put("moreid", pid);
      //  showProgress(WtlbActivity.this);
        URLManage.showInfos("findProblems.json",params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject  responseBody) {
                // TODO Auto-generated method stub
                super.onSuccess(statusCode, headers, responseBody);
                closeProgress();
                try {
                    msg = responseBody.toString();
                    if(msg.length()>40){
                        init(msg);
                    }
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
    private void init(String info) {

      if(info.length()>40) {
          List<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
          try {
              JSONObject jsonObject = jsonlogin(info);
              String _value = jsonObject.getString("FDayPlanProblemList");
              JSONArray arr = new JSONArray(_value);

              for (int i = 0; i < arr.length(); i++) {
                  JSONObject jsonObject2 = arr.getJSONObject(i);
                  {
                      HashMap<String, String> map = new HashMap<String, String>();
                      map.put("itemTitle", (i + 1) + "." + jsonObject2.getString("fxys"));
                      map.put("itemText", jsonObject2.getString("fxwt"));

                      mylist.add(map);
                  }
              }

          } catch (Exception e) {
          }
          list = (ListView) findViewById(R.id.listview);
          SimpleAdapter adapter = new SimpleAdapter(this,
                  mylist,//数据源
                  R.layout.phote_vlist,//显示布局
                  new String[]{"itemTitle", "itemText"}, //数据源的属性字段
                  new int[]{R.id.itemTitle, R.id.itemText}); //布局里的控件id
          //添加并且显示
          list.setAdapter(adapter);
          ListClickListener listn = new ListClickListener();
          list.setOnItemClickListener(listn);
      }
    }

    class ListClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String text = parent.getItemAtPosition(position) + "";
          //  nextdo(text);
        }
    }
    @Override
    public void onClick(View v) {
        System.out.println("0001111---");
        if (v.getId() == R.id.buttonwtlr1) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("pid", pid);
            System.out.println(pid+"pid");
            intent.putExtras(bundle);
            intent.setClass(WtlbActivity.this,WtlrActivity.class);
            startActivity(intent);
        }

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
}