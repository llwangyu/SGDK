package com.mwj.lhn.sgdk.mwj;

/**
 * Created by LHN on 2017/11/14.
 */

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mwj.lhn.sgdk.R;
import com.mwj.lhn.sgdk.db.DatabaseHelper;
import com.mwj.lhn.sgdk.db.UserDao;
import com.mwj.lhn.sgdk.pub.Basic;
import com.mwj.lhn.sgdk.pub.URLManage;
import com.mwj.lhn.sgdk.pub.UpdateManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;


@SuppressLint({ "ParserError", "ParserError", "ParserError" })
public class login extends Activity {

    private EditText uname = null;
    private EditText upswd = null;
    private Button login = null;
    private Button upbutton = null;
    private Button exitButton = null;
    private Intent intent;
    private CheckBox auto = null;
    private String nv="";
    private String dqsj="";
    private String tel="";
    private String sms,fZdZylcs,lc="";
    final int DATE_DIALOG = 1;
    private static final int MESSAGETYPE_01 = 0x0001;
    private static final String TAG ="22222" ;
    public static ProgressDialog progressDialog = null;
    public static boolean STOP = true;
    SharedPreferences sp = null;
    private  UserDao usd;
    private SQLiteDatabase db1=null;
    private int Idlxx;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login111);
        sp = this.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        first();

    }

    public void first(){
        DatabaseHelper dbHelper1 = new DatabaseHelper(login.this, "MY.db");
        //取得一个只读的数据库对象
         db1 = dbHelper1.getReadableDatabase();
        db1.close();
        init();

    }
    public void init() {
        usd=new UserDao();
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        dqsj=format.format(date);
        auto = (CheckBox) findViewById(R.id.auto);
        uname = (EditText) findViewById(R.id.username_info);
        upswd = (EditText) findViewById(R.id.password_info);
     //   upswd.setText("13893641419");
     //   uname.setText("353280");
        login = (Button) findViewById(R.id.login_ok);
        exitButton = (Button) findViewById(R.id.exit);
        upbutton= (Button) findViewById(R.id.button5);
        if (sp.getBoolean("auto", false)) {
            upswd.setText(sp.getString("upswd", null));
            uname.setText(sp.getString("uname", null));
            auto.setChecked(true);
        }
        listener();
    }

    public void listener() {

        exitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Basic.dialog(login.this);
            }
        });
        upbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                updialog(login.this);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatabaseHelper dbHelper1 = new DatabaseHelper(login.this, "MY.db");
                //取得一个只读的数据库对象
                final SQLiteDatabase db2 = dbHelper1.getReadableDatabase();

                sms = uname.getText().toString();
                tel=upswd.getText().toString();
                Idlxx= usd.getrk(db2,tel+sms);
                boolean autoLogin = auto.isChecked();
                ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
                Editor editor = sp.edit();

                if (autoLogin)  {
                    tel=upswd.getText().toString();
                    editor.putString("upswd", tel);
                    editor.putString("uname", sms);
                    editor.putBoolean("auto", true);
                    editor.commit();
                    final String nameValue = sp.getString("uname", null);
                    final String pswdValue = sp.getString("upswd", null);
                    final String numValue = sp.getString("snum", null);
                    if (Idlxx==0) {
                        if (networkInfo != null) {

                            if (!(upswd.getText().toString()).equals("")) {
                                if (Idlxx==0) {
                                    RequestParams params = new RequestParams();// 绑定参数
                                    params.put("tel_number", tel);
                                    params.put("sendyzm", sms);
                                    showProgress(login.this);
                                    URLManage.showInfos("findDayPlans.json", params, new JsonHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject responseBody) {
                                            // TODO Auto-generated method stub
                                            super.onSuccess(statusCode, headers, responseBody);
                                            closeProgress();
                                            try {
                                                String _value = responseBody.getString("FDayPlanVOList");
                                                nv = _value;
                                                //取信息
                                                JSONArray arr = new JSONArray(_value);
                                                JSONObject jsonObject1 = arr.getJSONObject(0);
                                                String fdayPlanMores = jsonObject1.getString("fdayPlanMoreVO1s");
                                                JSONArray arr1 = new JSONArray(fdayPlanMores);
                                                for (int m = 0; m < arr1.length(); m++) {
                                                    JSONObject jsonObject2 = arr1.getJSONObject(m);
                                                    if (jsonObject2.getString("tel_number").equals(tel)) {
                                                        fZdZylcs=null2str(jsonObject2.getString("fZdZylcs"));
                                                        lc=null2str(jsonObject2.getString("nextLc"));
                                                    }
                                                }

                                            } catch (Exception ex) {
                                            }
                                            if (nv.length() > 25) {

                                                usd.insSg(db2,tel+sms,responseBody.toString(),fZdZylcs,lc);
                                                intent = new Intent();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("tel", tel);
                                                bundle.putString("sms", sms);
                                                bundle.putString("msg", responseBody.toString());
                                                intent.putExtras(bundle);
                                                intent.setClass(login.this, SgxxActivity.class);
                                                startActivity(intent);
                                                //finish();
                                            } else {
                                                dialog("当前未安排盯控！");
                                            }
                                        }

                                        @Override
                                        public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable e, JSONObject errorResponse) {
                                            super.onFailure(statusCode, headers, e, errorResponse);
                                            closeProgress();
                                            dialog("登录失败！");
                                        }
                                    });
                                }

                            }
                            else {
                                dialog("信息不能为空！");
                            }
                        } else {
                            dialog("网络链接异常，请检查网络！");
                        }
                    }
                    else{
                        String sgxx= usd.getsgxx(db2,tel+sms);
                        intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("tel", tel);
                        bundle.putString("sms", sms);
                        bundle.putString("msg", sgxx.toString());
                        intent.putExtras(bundle);
                        intent.setClass(login.this, SgxxActivity.class);
                        startActivity(intent);
                    }



                }
                else{
                    if (Idlxx == 0) {
                        if (networkInfo != null) {
                            if (!(upswd.getText().toString()).equals("")) {
                                if (Idlxx == 0) {
                                    tel = upswd.getText().toString();
                                    RequestParams params = new RequestParams();// 绑定参数
                                    params.put("tel_number", tel);
                                    params.put("sendyzm", sms);
                                    showProgress(login.this);
                                    URLManage.showInfos("findDayPlans.json", params, new JsonHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject responseBody) {
                                            // TODO Auto-generated method stub
                                            super.onSuccess(statusCode, headers, responseBody);
                                            closeProgress();
                                            try {
                                                String _value = responseBody.getString("FDayPlanVOList");
                                                nv = _value;
                                                JSONArray arr = new JSONArray(_value);
                                                JSONObject jsonObject1 = arr.getJSONObject(0);
                                                String fdayPlanMores = jsonObject1.getString("fdayPlanMoreVO1s");
                                                JSONArray arr1 = new JSONArray(fdayPlanMores);
                                                for (int m = 0; m < arr1.length(); m++) {
                                                    JSONObject jsonObject2 = arr1.getJSONObject(m);
                                                    if (jsonObject2.getString("tel_number").equals(tel)) {
                                                        fZdZylcs=null2str(jsonObject2.getString("fZdZylcs"));
                                                        lc=null2str(jsonObject2.getString("nextLc"));
                                                    }
                                                }
                                            } catch (Exception ex) {
                                            }
                                            if (nv.length() > 25) {
                                                usd.insSg(db2,tel+sms,responseBody.toString(),fZdZylcs,lc);

                                                intent = new Intent();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("tel", tel);
                                                bundle.putString("sms", sms);
                                                bundle.putString("msg", responseBody.toString());
                                                intent.putExtras(bundle);
                                                intent.setClass(login.this, SgxxActivity.class);
                                                startActivity(intent);
                                                //finish();
                                            } else {
                                                dialog("当前未安排盯控！");
                                            }
                                        }

                                        @Override
                                        public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable e, JSONObject errorResponse) {
                                            super.onFailure(statusCode, headers, e, errorResponse);
                                            closeProgress();
                                            dialog("登录失败！");
                                        }
                                    });
                                }

                            }
                            else {
                                dialog("信息不能为空！");
                            }
                        } else {
                            dialog("网络链接异常，请检查网络！");
                        }
                    }
                    else{
                            String sgxx= usd.getsgxx(db2,tel+sms);
                            intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putString("tel", tel);
                            bundle.putString("sms", sms);
                            bundle.putString("msg", sgxx.toString());
                            intent.putExtras(bundle);
                            intent.setClass(login.this, SgxxActivity.class);
                            startActivity(intent);
                    }
                }
            }
        });
    }
    /**
     * 设置日期 利用StringBuffer追加
     */

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
    protected void dialog111() {
        AlertDialog.Builder builder = new Builder(login.this);
        builder.setMessage("确定要退出吗?");
        builder.setTitle("提示");
        builder.setPositiveButton("确认",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        login.this.finish();
                    }
                });
        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
    /**
     * 退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
           // dialog111();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public  void dialog(String text) {
        AlertDialog.Builder builder = new Builder(login.this);
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
    public  void dialogexit(String text) {
        AlertDialog.Builder builder = new Builder(login.this);
        builder.setMessage(text);
        builder.setTitle("提示");
        builder.setPositiveButton("确认",
                new android.content.DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        System.exit(0);
                    }
                });
        builder.create().show();

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
    public String null2str(String obj){
        if(obj.equals("null")){
            obj="";
        }
        if(obj==null){
            obj="";
        }
        return obj;
    }
    public static void updialog(final Activity lg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(lg);
        builder.setMessage("检查更新吗?");
        builder.setTitle("提示");
        builder.setPositiveButton("确认",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        UpdateManager manager = new UpdateManager(lg);
                        manager.checkUpdate();
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
}
