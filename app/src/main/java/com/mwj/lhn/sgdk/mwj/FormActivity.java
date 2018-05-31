package com.mwj.lhn.sgdk.mwj;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mwj.lhn.sgdk.LocatActivity;
import com.mwj.lhn.sgdk.R;
import com.mwj.lhn.sgdk.pub.Basic;
import com.mwj.lhn.sgdk.pub.URLManage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.mwj.lhn.sgdk.pub.Basic.jsonlogin;

public class FormActivity extends AppCompatActivity implements
        View.OnClickListener {
    private String msg,tel,pid,qdsj,lksj,sdkdd;
    private String sgqks,fxwt;
    private Button sw1,sw2;
    private Button bt1,bt2,bt3;
    private TextView sgqk,tv_time,czwt,ddkdd;
    private static final int msgKey1 = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        Bundle bundle = this.getIntent().getExtras();
        msg = bundle.getString("msg");
        tel = bundle.getString("tel");

        new TimeThread().start();
        init();
    }
    public class TimeThread extends  Thread{
        @Override
        public void run() {
            super.run();
            do{
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = msgKey1;
                    mHandler.sendMessage(msg);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }while (true);
        }
    }
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case msgKey1:
                    long time = System.currentTimeMillis();
                    Date date = new Date(time);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
                    tv_time.setText("当前时间："+format.format(date));
                    break;
                default:
                    break;
            }
        }
    };
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
                    if(!jsonObject2.getString("jcdd").equals("null")){
                        if(null2str(jsonObject2.getString("jcdd")).length()>3) {
                            sdkdd = null2str(jsonObject2.getString("jcdd"));
                        }
                    }
                    pid=null2str(jsonObject2.getString("id"));
                    qdsj=null2str(jsonObject2.getString("jcsj"));
                    lksj=null2str(jsonObject2.getString("lgsj"));
                    fxwt=null2str(jsonObject2.getString("fxwt"));
                    //dgsj=jsonObject2.getString();
                    // lksj=jsonObject2.getString();
                }
            }

        } catch (Exception e) {
        }
        sgqk=(TextView)findViewById(R.id.sgqk);sgqk.setText(sgqks);
        sw1=(Button)findViewById(R.id.switch1);
        sw2=(Button)findViewById(R.id.switch2);
        bt1=(Button)findViewById(R.id.button);
        bt2=(Button)findViewById(R.id.button2);
        bt3=(Button)findViewById(R.id.button3);
        czwt=(TextView)findViewById(R.id.editText111);
        czwt.setText(fxwt);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);
        sw1.setOnClickListener(this);
        sw2.setOnClickListener(this);
        ddkdd.setText(sdkdd);

        if(qdsj.length()>4){
            if(lksj.length()>4){
                sw1.setEnabled(false);
                sw2.setEnabled(false);
                ddkdd.setEnabled(false);

            }
            else{
                sw1.setEnabled(false);
                sw2.setEnabled(true);
                ddkdd.setEnabled(false);
            }
        }
        else{
            ddkdd.setEnabled(true);
            sw1.setEnabled(true);
            sw2.setEnabled(false);
        }
    }
    public void onClick(View v) {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("dd-HH:mm");
        String dtime=format.format(date);
        if (v.getId() == R.id.button) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("msg", msg);
            bundle.putString("pid", pid);
            intent.putExtras(bundle);
            intent.setClass(FormActivity.this,com.mwj.lhn.sgdk.dzqm.photeActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.switch1) {
            sw1.setEnabled(false);
            sw2.setEnabled(true);
            RequestParams params = new RequestParams(); // 绑定参数
            params.put("id", pid);
            params.put("jcsj",dtime);
            params.put("jcdd",ddkdd.getText().toString());//到岗
            URLManage.showInfos("updateDayPlanMore.json",params, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject responseBody) {
                    // TODO Auto-generated method stub
                    super.onSuccess(statusCode, headers, responseBody);
                    ddkdd.setEnabled(false);
                    getjg(responseBody);
                }
                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable e, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, e, errorResponse);
                    Toast.makeText(FormActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (v.getId() == R.id.switch2) {  //离岗
            {
                sw1.setEnabled(false);
                sw2.setEnabled(false);
                RequestParams params = new RequestParams(); // 绑定参数
                params.put("id", pid);
                params.put("lgsj",dtime);
                URLManage.showInfos("updateDayPlanMore.json",params, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject responseBody) {
                        // TODO Auto-generated method stub
                        super.onSuccess(statusCode, headers, responseBody);
                        getjg(responseBody);
                    }
                    @Override
                    public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable e, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, e, errorResponse);
                        Toast.makeText(FormActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        if (v.getId() == R.id.button2) {
            String rqwt = czwt.getText().toString();

            Toast.makeText(FormActivity.this, rqwt+rqwt.length(), Toast.LENGTH_SHORT).show();


            if(rqwt.length()>4)
            {

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
                        Toast.makeText(FormActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
        if (v.getId() == R.id.button3) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("msg", msg);
            bundle.putString("pid", pid);
            intent.putExtras(bundle);
            intent.setClass(FormActivity.this,PhotoListActivity.class);
            startActivity(intent);
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
                Toast.makeText(FormActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(FormActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
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

}
