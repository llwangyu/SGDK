package com.mwj.lhn.sgdk.mwj;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mwj.lhn.sgdk.R;
import com.mwj.lhn.sgdk.pub.URLManage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

import static com.mwj.lhn.sgdk.pub.Basic.jsonlogin;

public class WtlrActivity extends AppCompatActivity implements
        View.OnClickListener{
    private String pid="";
    private TextView jcdd,fxwt,zgqx,zgyq;
    private EditText zrdw,xd,ys,xz;
    private Button bt4,dwb,fxb,ysb,xzb,rqxz;
    ArrayList<Integer>MultiChoiceID = new ArrayList<Integer>();
    private static final int MESSAGETYPE_01 = 0x0001;
    private static final String TAG ="22222" ;
    public static ProgressDialog progressDialog = null;
    public static boolean STOP = true;
    private String[] items1,items2,items3,items4;
    private boolean[] bitem4;
    private ListView lv;
    int mYear, mMonth, mDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wtlr);
        Bundle bundle = this.getIntent().getExtras();
        pid = bundle.getString("pid");
        getreflash();
    }
    private void getreflash(){
        RequestParams params = new RequestParams();// 绑定参数
        params.put("moreid", pid);
        showProgress(WtlrActivity.this);
        URLManage.showInfos("findPrlblemZd.json",params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject responseBody) {
                // TODO Auto-generated method stub
                super.onSuccess(statusCode, headers, responseBody);
                closeProgress();
                try {
                   String msg = responseBody.toString();
                    if(msg.length()>20){
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
    private void init(String msg) {

        try {
            JSONObject jsonObject = jsonlogin(msg);
            String zd1= jsonObject.getString("zd1");
            String zd2= jsonObject.getString("zd2");
            String zd3= jsonObject.getString("zd3");
            String zd4= jsonObject.getString("zd4");
            JSONArray zd1a = new JSONArray(zd1);
            JSONArray zd2a = new JSONArray(zd2);
            JSONArray zd3a = new JSONArray(zd3);
            items1 = new String[zd1a.length()];
            for (int i = 0; i < zd1a.length(); i++) {
                JSONObject jsonObject2 = zd1a.getJSONObject(i);
                items1[i] = jsonObject2.getString("name");
            }
            items2 = new String[zd2a.length()];
            for (int i = 0; i < zd2a.length(); i++) {
                JSONObject jsonObject2 = zd2a.getJSONObject(i);
                items2[i] = jsonObject2.getString("name");
            }
            items3 = new String[zd3a.length()];
            for (int i = 0; i < zd3a.length(); i++) {
                JSONObject jsonObject2 = zd3a.getJSONObject(i);
                items3[i] = jsonObject2.getString("name");
            }
            zd4=zd4.replace("[","");
            zd4=zd4.replace("]","");
            zd4=zd4.replace("\"","");
            items4 = zd4.split(",");

            bitem4=new boolean[items4.length];
            for (int i = 0; i < items4.length; i++) {
                bitem4[i] = false;
            }

        } catch (Exception e) {
        }
        Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        jcdd=findViewById(R.id.jcddn);
        fxwt=findViewById(R.id.fxwtn);
        zgqx=findViewById(R.id.zgqxn);
        zgyq=findViewById(R.id.zgyqn);
        zrdw=findViewById(R.id.spinner1);
        xd=findViewById(R.id.spinner2);
        ys=findViewById(R.id.spinner3);
        xz=findViewById(R.id.spinner4);
        rqxz=findViewById(R.id.button7);
        bt4=findViewById(R.id.button4);bt4.setOnClickListener(this);
        dwb=findViewById(R.id.dwb);dwb.setOnClickListener(this);
        fxb=findViewById(R.id.fxb);fxb.setOnClickListener(this);
        ysb=findViewById(R.id.ysb);ysb.setOnClickListener(this);
        xzb=findViewById(R.id.xzb);xzb.setOnClickListener(this);
        rqxz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 调用时间选择器
                new DatePickerDialog(WtlrActivity.this, onDateSetListener, mYear, mMonth, mDay).show();
            }
        });


    }
    public void onClick(View v) {
        if (v.getId() == R.id.ysb) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);// 自定义对话框
            builder.setSingleChoiceItems(items1, 0, new DialogInterface.OnClickListener() {// 2默认的选中

                @Override
                public void onClick(DialogInterface dialog, int which) {// which是被选中的位置
                    ys.setText(items1[which]);
                    dialog.dismiss();//随便点击一个item消失对话框，不用点击确认取消
                }
            });
            builder.show();
        }

        if (v.getId() == R.id.fxb) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);// 自定义对话框
            builder.setSingleChoiceItems(items2, 0, new DialogInterface.OnClickListener() {// 2默认的选中

                @Override
                public void onClick(DialogInterface dialog, int which) {// which是被选中的位置
                    xd.setText(items2[which]);
                    dialog.dismiss();//随便点击一个item消失对话框，不用点击确认取消
                }
            });
            builder.show();
        }
        if (v.getId() == R.id.xzb) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);// 自定义对话框
            builder.setSingleChoiceItems(items3, 0, new DialogInterface.OnClickListener() {// 2默认的选中
                @Override
                public void onClick(DialogInterface dialog, int which) {// which是被选中的位置
                    xz.setText(items3[which]);
                    dialog.dismiss();//随便点击一个item消失对话框，不用点击确认取消
                }
            });
            builder.show();
        }
        if(v.getId()==R.id.dwb){
            AlertDialog.Builder builder=new AlertDialog.Builder(WtlrActivity.this);
            builder.setTitle("责任单位");
            builder.setMultiChoiceItems(items4, bitem4, new DialogInterface.OnMultiChoiceClickListener() {
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    bitem4[which] = isChecked;  //保存客户选择的属性是否被勾选
                }
            });
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String item="";
                    for(int i=0;i<items4.length;i++){
                        if(bitem4[i]){             //如果被勾线则保存数据
                            item+=items4[i]+",";
                        }
                    }
                    if (item.length() > 0) {
                        item = item.substring(0, item.length() - 1);
                    }
                        zrdw.setText(item);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
        if(v.getId()==R.id.button4){
            String _fxwt=fxwt.getText().toString();
            if(!_fxwt.equals("")) {
                RequestParams params = new RequestParams();// 绑定参数
                params.put("jcdd", jcdd.getText().toString());
                params.put("moreid", pid);
                params.put("fxwt", fxwt.getText().toString());
                params.put("zgqx", zgqx.getText().toString());
                params.put("zrdw", zrdw.getText().toString());
                params.put("yq", zgyq.getText().toString());
                params.put("xz", xz.getText().toString());
                params.put("fxys", ys.getText().toString());
                params.put("fxxd", xd.getText().toString());
                showProgress(WtlrActivity.this);
                URLManage.showInfos("insertDayPlanProblem.json", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject responseBody) {
                        // TODO Auto-generated method stub
                        super.onSuccess(statusCode, headers, responseBody);
                        closeProgress();
                        try {
                            String dbResult1 = responseBody.getString("dbResult");
                            JSONObject jsonObject = jsonlogin(dbResult1);
                            String result = jsonObject.getString("result");
                            if (result.equals("1")) {
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "登记失败，请稍候再试", Toast.LENGTH_SHORT).show();
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
            else{
                Toast.makeText(getApplicationContext(), "请输入内容后提交", Toast.LENGTH_SHORT).show();

            }
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
    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {


        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            String days;
            if (mMonth + 1 < 10) {
                if (mDay < 10) {
                    days = new StringBuffer().append(mYear).append("年").append("0").
                            append(mMonth + 1).append("月").append("0").append(mDay).append("日").toString();
                } else {
                    days = new StringBuffer().append(mYear).append("年").append("0").
                            append(mMonth + 1).append("月").append(mDay).append("日").toString();
                }

            } else {
                if (mDay < 10) {
                    days = new StringBuffer().append(mYear).append("年").
                            append(mMonth + 1).append("月").append("0").append(mDay).append("日").toString();
                } else {
                    days = new StringBuffer().append(mYear).append("年").
                            append(mMonth + 1).append("月").append(mDay).append("日").toString();
                }

            }
            zgqx.setText(days);
        }
    };
}
