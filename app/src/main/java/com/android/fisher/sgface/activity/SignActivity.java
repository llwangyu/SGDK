package com.android.fisher.sgface.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.mwj.lhn.sgdk.R;
import com.android.fisher.sgface.tool.PrefName;
import com.android.fisher.sgface.util.FileUtil;
import com.android.fisher.sgface.util.PreferencesUtils;
import com.android.fisher.sgface.util.ToolUtil;
import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class SignActivity extends BaseActivity implements TakePhoto.TakeResultListener,InvokeListener {
    private Button takephoto_sign_bt;
    private Button upload_sign_bt;
    private ImageView sign_iv;
    private EditText depart_name;
    private EditText emp_name;
    private EditText statusms_sign;
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign1);
        initView();
    }

    private void initView(){
        final String username = ToolUtil.trimNull(PreferencesUtils.getString(SignActivity.this,"username"));
        final String userpwd = ToolUtil.trimNull(PreferencesUtils.getString(SignActivity.this,"userpwd"));
        String depart_nameStr = ToolUtil.trimNull(PreferencesUtils.getString(SignActivity.this,"_depart_name"));
        String emp_nameStr = ToolUtil.trimNull(PreferencesUtils.getString(SignActivity.this,"_emp_name"));
        String statusms_signStr = ToolUtil.trimNull(PreferencesUtils.getString(SignActivity.this,"_statusms_sign"));
        depart_name = (EditText)findViewById(R.id.depart_name);
        depart_name.setText(depart_nameStr);
        emp_name = (EditText)findViewById(R.id.emp_name);
        emp_name.setText(emp_nameStr);
        statusms_sign = (EditText)findViewById(R.id.statusms_sign);
        statusms_sign.setText(statusms_signStr);
        sign_iv = (ImageView)findViewById(R.id.sign_iv);
        upload_sign_bt = (Button)findViewById(R.id.upload_sign_bt);
        takephoto_sign_bt = (Button)findViewById(R.id.takephoto_sign_bt);
        final String empid = PreferencesUtils.getString(SignActivity.this,"_empid");
        takephoto_sign_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file=new File(Environment.getExternalStorageDirectory(), "/sgface/"+System.currentTimeMillis() + ".jpg");
                if (!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                //file.delete();
                imageUri = Uri.fromFile(file);
                getTakePhoto().onPickFromCapture(imageUri);
            }
        });
        upload_sign_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar("正在上传您的照片，请稍候...");
                try{
                    //final File file = new File(new URI(imageUri.toString()));
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Disposition", "form-data;filename=enctype");
                    //if (file==null||!file.exists()||empid==null||empid.equals("")) {
                    if (empid==null||empid.equals("")) {
                        hideProgressBar();
                        showUpdataDialog("上传照片","未采集照片，请拍照后上传!");
                        return;
                    }else{
                        File file = FileUtil.getFileFromIV(sign_iv);
                        String url = PrefName.DEFAULT_SERVER_URL + PrefName.UPLOAD_SIGN_SERVER_URL;
                        String filename = file.getName();
                        OkHttpUtils.post()
                                .url(url)
                                .addParams("id",empid)
                                .addParams("remark1",file.getPath())
                                .headers(headers)
                                .addFile("file", filename, file)
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                    }
                                    @Override
                                    public void onResponse(String response, int id) {
                                        doLogin(username,userpwd);
                                        hideProgressBar();
                                    }
                                });
                    }
                }catch(Exception e){
                    showToast("上传文件错误！");
                    LogE(e.toString());
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     *  获取TakePhoto实例
     * @return
     */
    public TakePhoto getTakePhoto(){
        if (takePhoto==null){
            takePhoto= (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this,this));
            /*CompressConfig compressConfig=new CompressConfig.Builder().setMaxSize(60).setMaxPixel(60).create();
            takePhoto.onEnableCompress(compressConfig,false);*/
            //takePhoto.onPickFromGallery();
        }
        return takePhoto;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type=PermissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
        PermissionManager.handlePermissionsResult(this,type,invokeParam,this);
    }

    @Override
    public void takeSuccess(TResult result) {
        try{
            Glide.with(this).load(imageUri).override(800,800).into(sign_iv);
        }catch(Exception e){
            e.printStackTrace();
            LogE(e.toString());
        }
    }

    @Override
    public void takeFail(TResult result, String msg) {

    }

    @Override
    public void takeCancel() {

    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type=PermissionManager.checkPermission(TContextWrap.of(this),invokeParam.getMethod());
        if(PermissionManager.TPermissionType.WAIT.equals(type)){
            this.invokeParam=invokeParam;
        }
        return type;
    }


    private void doLogin(final String edittext_username,final String edittext_userpwd){
        Map<String,String> params = new HashMap<String,String>();
        params.put("username",edittext_username);
        params.put("userpwd",edittext_userpwd);
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
                            File sdFile = new File(remark1);
                            if(remark1==null||remark1.length()<3||(!sdFile.exists())){
                                PreferencesUtils.putString(SignActivity.this,"username",edittext_username);
                                PreferencesUtils.putString(SignActivity.this,"_meetinguserid",_meetinguserid);
                                PreferencesUtils.putString(SignActivity.this,"_empid",empid);
                                tmpJsonElement = jsonObject.get("depart_name");
                                String depart_name = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());//会议名称
                                PreferencesUtils.putString(SignActivity.this,"_depart_name",depart_name);
                                tmpJsonElement = jsonObject.get("emp_name");
                                String emp_name = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());//会议名称
                                PreferencesUtils.putString(SignActivity.this,"_emp_name",emp_name);
                                tmpJsonElement = jsonObject.get("statusms");
                                String statusms_sign = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());//会议名称
                                PreferencesUtils.putString(SignActivity.this,"_statusms_sign",statusms_sign);
                                JumpToActivity(SignActivity.class);
                            }else {
                                PreferencesUtils.putString(SignActivity.this,"username",edittext_username);
                                PreferencesUtils.putString(SignActivity.this,"remark1",remark1);
                                PreferencesUtils.putString(SignActivity.this,"_meetinguserid",_meetinguserid);
                                PreferencesUtils.putString(SignActivity.this,"_empid",empid);
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
                                PreferencesUtils.putString(SignActivity.this,"_meetingname",meetingname);
                                PreferencesUtils.putString(SignActivity.this,"_meetingdate",meetingdate);
                                PreferencesUtils.putString(SignActivity.this,"_meetingaddress",meetingaddress);
                                PreferencesUtils.putString(SignActivity.this,"_meetingcontent",meetingcontent);
                                PreferencesUtils.putString(SignActivity.this,"_statusms",statusms);
                                PreferencesUtils.putString(SignActivity.this,"_status",status);
                                PreferencesUtils.putString(SignActivity.this,"_ppl",ppl);
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
}