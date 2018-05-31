package com.android.fisher.sgface.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mwj.lhn.sgdk.BuildConfig;
import com.mwj.lhn.sgdk.R;
import com.android.fisher.sgface.entity.FZdMupdate;
import com.android.fisher.sgface.tool.PrefName;
import com.android.fisher.sgface.util.GsonUtil;
import com.android.fisher.sgface.util.PreferencesUtils;
import com.android.fisher.sgface.util.ToolUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class LoginActivity extends BaseActivity {
    private Button login_button ;
    private TextView sign_tv;
    private TextView login_txtForgotPwd;
    private EditText login_telnum_et;
    private EditText login_smsyzm_et;
    private static final int FILE_SELECT_CODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);
        intiView();
    }

    private void intiView(){
        String username = ToolUtil.trimNull(PreferencesUtils.getString(LoginActivity.this,"username"));
        login_button = (Button)findViewById(R.id.login_btnLogin);
        login_telnum_et = (EditText)findViewById(R.id.login_telnum_et);
        login_telnum_et.setText(username);
        login_smsyzm_et = (EditText)findViewById(R.id.login_smsyzm_et);
        login_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String edit_telnum = login_telnum_et.getText().toString();
                final String edit_yzm = login_smsyzm_et.getText().toString();
                doLogin(edit_telnum,edit_yzm);
            }
        });
        sign_tv = (TextView)findViewById(R.id.sign_tv);
        sign_tv.setClickable(true);
        sign_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("该功能未开放！");
            }
        });
        login_txtForgotPwd = (TextView)findViewById(R.id.login_txtForgotPwd);
        String verName = getVersionName();
        login_txtForgotPwd.setText(verName);
        login_txtForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("版本名称"+getVersionName()+"；版本号："+getVersionCode());
                //checkVersion();
            }
        });
        checkVersion();
    }

    private void doLogin(final String edittext_username,final String edittext_userpwd){
        Map<String,String> params = new HashMap<String,String>();
        params.put("username",edittext_username);
        params.put("userpwd",edittext_userpwd);
        params.put("getImgFlag","1");
        String url = PrefName.DEFAULT_SERVER_URL + PrefName.LOGIN_SERVER_URL;
        showProgressBar("正在登陆系统，请稍候...");
        OkHttpUtils
                .get()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        JsonElement tmpJsonElement = null;
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        tmpJsonElement = jsonObject.get("success");
                        boolean success = (tmpJsonElement==null||tmpJsonElement.isJsonNull())?false:tmpJsonElement.getAsBoolean();
                        if(success){
                            tmpJsonElement = jsonObject.get("empid");
                            String empid = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());
                            tmpJsonElement = jsonObject.get("id");
                            String _meetinguserid = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());
                            tmpJsonElement = jsonObject.get("remark1");
                            String remark1 = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());
                            //File sdFile = new File(remark1);
                            if(remark1==null||remark1.length()<3){
                                PreferencesUtils.putString(LoginActivity.this,"username",edittext_username);
                                PreferencesUtils.putString(LoginActivity.this,"userpwd",edittext_userpwd);
                                PreferencesUtils.putString(LoginActivity.this,"_meetinguserid",_meetinguserid);
                                PreferencesUtils.putString(LoginActivity.this,"_empid",empid);
                                tmpJsonElement = jsonObject.get("depart_name");
                                String depart_name = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());//会议名称
                                PreferencesUtils.putString(LoginActivity.this,"_depart_name",depart_name);
                                tmpJsonElement = jsonObject.get("emp_name");
                                String emp_name = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());//会议名称
                                PreferencesUtils.putString(LoginActivity.this,"_emp_name",emp_name);
                                tmpJsonElement = jsonObject.get("statusms");
                                String statusms_sign = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());//会议名称
                                PreferencesUtils.putString(LoginActivity.this,"_statusms_sign",statusms_sign);
                                JumpToActivity(SignActivity.class);
                            }else {
                                PreferencesUtils.putString(LoginActivity.this,"username",edittext_username);
                                PreferencesUtils.putString(LoginActivity.this,"userpwd",edittext_userpwd);
                                //PreferencesUtils.putString(LoginActivity.this,"remark1",remark1);
                                PreferencesUtils.putString(LoginActivity.this,"_meetinguserid",_meetinguserid);
                                PreferencesUtils.putString(LoginActivity.this,"_empid",empid);
                                tmpJsonElement = jsonObject.get("meetingname");
                                String meetingname = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());//会议名称
                                tmpJsonElement = jsonObject.get("meetingdate");
                                String meetingdate = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());//会议时间
                                tmpJsonElement = jsonObject.get("meetingaddress");
                                String meetingaddress = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());//会议地点
                                tmpJsonElement = jsonObject.get("meetingcontent");
                                String meetingcontent = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());//会议内容
                                tmpJsonElement = jsonObject.get("statusms");
                                String statusms = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());//签到状态 1是已成功   其他是未成功
                                tmpJsonElement = jsonObject.get("status");
                                String status = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());//签到状态 1是已成功   其他是未成功
                                tmpJsonElement = jsonObject.get("ppl");
                                String ppl = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());//相似度
                                PreferencesUtils.putString(LoginActivity.this,"_meetingname",meetingname);
                                PreferencesUtils.putString(LoginActivity.this,"_meetingdate",meetingdate);
                                PreferencesUtils.putString(LoginActivity.this,"_meetingaddress",meetingaddress);
                                PreferencesUtils.putString(LoginActivity.this,"_meetingcontent",meetingcontent);
                                PreferencesUtils.putString(LoginActivity.this,"_statusms",statusms);
                                PreferencesUtils.putString(LoginActivity.this,"_status",status);
                                PreferencesUtils.putString(LoginActivity.this,"_ppl",ppl);
                                //开始处理
                                tmpJsonElement = jsonObject.get("imgdata");
                                String imgdata = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());//相似度
                                PreferencesUtils.putString(LoginActivity.this,"imgdata",imgdata);
                                JumpToActivity(MainActivity.class);
                            }
                        }else{
                            tmpJsonElement = jsonObject.get("statusms");
                            String statusms = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());//签到状态 1是已成功   其他是未成功
                            showToast(statusms);
                        }
                        hideProgressBar();
                    }

                });
    }

    private void checkVersion(){
        String url = PrefName.DEFAULT_SERVER_URL + PrefName.VER_SERVER_URL;
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        //FZdMupdate fZdMupdate = new FZdMupdate("id",1804191,"versionName","http://www.ltslkcw.com/slkc/resources/apk/lsc.apk","description");
                        response = response.toLowerCase();
                        FZdMupdate fZdMupdate = GsonUtil.fromJsonView(response,"update",FZdMupdate.class);
                        int locationVersion = getVersionCode();
                        if(fZdMupdate.getVersioncode()>locationVersion){//如果远程版本更新
                            showUpdataDialog(fZdMupdate);
                        }
                    }
                });
    }

    /*
    弹出是否更新的对话框：
     */
    private void showUpdataDialog(final FZdMupdate fZdMupdate){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("版本升级");
        builder.setMessage(fZdMupdate.getVersionname()+"--->"+fZdMupdate.getDescription());
        builder.setPositiveButton("确定升级",new AlertDialog.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showProgressBar("正在更新程序，请稍候...");
                OkHttpUtils//
                        .get()//
                        .tag(this)
                        .url(fZdMupdate.getUrl())//
                        .build()//
                        .execute(new FileCallBack(Environment.getExternalStorageDirectory() + "/" + PrefName.FILE_DIR_NAME, PrefName.APK_NAME)//
                        {
                            @Override
                            public void onError(Call call, Exception e, int id) {//下载失败
                                showToast("下载更新文件失败");
                            }
                            @Override
                            public void onResponse(File response, int id) {//下载完成
                                //下载完成则安装
                                File apkFile = new File(Environment.getExternalStorageDirectory() + "/" + PrefName.FILE_DIR_NAME + "/" + PrefName.APK_NAME);
                                installApk(LoginActivity.this,apkFile);
                                hideProgressBar();
                            }
                        });
            }
        });
        builder.setNegativeButton("以后再说",new AlertDialog.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void installApk(Context context, File file) {
        if (file.exists()) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT < 23) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                context.startActivity(intent);
            } else {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                //Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
                Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            }
            if (context.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                context.startActivity(intent);
            }
        }
    }
}