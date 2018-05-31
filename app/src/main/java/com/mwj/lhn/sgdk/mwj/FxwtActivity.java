package com.mwj.lhn.sgdk.mwj;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mwj.lhn.sgdk.R;
import com.mwj.lhn.sgdk.pub.URLManage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

import static com.mwj.lhn.sgdk.pub.Basic.jsonlogin;

public class FxwtActivity extends AppCompatActivity implements
        View.OnClickListener {
    private String msg,tel,pid,qdsj,lksj,sdkdd;
    private String sgqks="",fxwt="";
    private Button bt2;
    private TextView sgqk,czwt;
    private static final int msgKey1 = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomTitle.getCustomTitle(this, "发现问题");
        setContentView(R.layout.activity_fxwt);
        Bundle bundle = this.getIntent().getExtras();
        msg = bundle.getString("msg");
        tel = bundle.getString("tel");
        ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            init();
        }
        else{
            dialog("网络链接异常，请检查网络！");
        }

    }

    private void init() {

        try {
            JSONObject jsonObject = jsonlogin(msg);
            String _value = jsonObject.getString("FDayPlanVOList");
            JSONArray arr = new JSONArray(_value);
            JSONObject jsonObject1 = arr.getJSONObject(0);
            sdkdd= jsonObject1.getString("location");
            sgqks = "施工概况："+jsonObject1.getString("planDate")+"日"+jsonObject1.getString("doTime")+
                    jsonObject1.getString("location")+jsonObject1.getString("length")+jsonObject1.getString("doUnit")
                    +jsonObject1.getString("type");

            String fdayPlanMores=jsonObject1.getString("fdayPlanMoreVO1s");
            JSONArray arr1 = new JSONArray(fdayPlanMores);
            for(int m=0;m<arr1.length();m++){
                JSONObject jsonObject2 = arr1.getJSONObject(m);
                if(jsonObject2.getString("tel_number").equals(tel)){
                    pid=null2str(jsonObject2.getString("id"));
                    fxwt=null2str(jsonObject2.getString("fxwt"));
                }
            }

        } catch (Exception e) {
        }
        sgqk=(TextView)findViewById(R.id.sgqk);sgqk.setText(sgqks);
        bt2=(Button)findViewById(R.id.button2);
        czwt=(TextView)findViewById(R.id.editText111);
        czwt.setText(fxwt);
        bt2.setOnClickListener(this);



    }
    public void onClick(View v) {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("dd-HH:mm");
        String dtime=format.format(date);
        if (v.getId() == R.id.button2) {
            String rqwt = czwt.getText().toString();
            if (sgqks.equals("")) {
                Toast.makeText(FxwtActivity.this, "无施工信息", Toast.LENGTH_SHORT).show();

            } else {
                if (rqwt.length() > 1) {
                    RequestParams params = new RequestParams(); // 绑定参数
                    params.put("id", pid);
                    params.put("fxwt", rqwt);
                    URLManage.showInfos("updateDayPlanMore.json", params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject responseBody) {
                            // TODO Auto-generated method stub
                            super.onSuccess(statusCode, headers, responseBody);
                            getjg(responseBody);
                        }

                        @Override
                        public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable e, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, e, errorResponse);
                            Toast.makeText(FxwtActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    Toast.makeText(FxwtActivity.this, "请输入问题", Toast.LENGTH_SHORT).show();

                }

            }
        }
    }
    private void getjg(JSONObject responseBody){
        String res= null;
        try {
            res = responseBody.getString("dbResult");
            JSONObject jsonObject = new JSONObject();
            jsonObject = new JSONObject(res);
            String result=jsonObject.getString("result");
            if(result.equals("1")){
                Toast.makeText(FxwtActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                Toast.makeText(FxwtActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
    public  void dialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FxwtActivity.this);
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
