package com.android.fisher.sgface.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import com.android.fisher.sgface.tool.PrefName;
import com.android.fisher.sgface.util.Base64Util;
import com.android.fisher.sgface.util.FileUtil;
import com.android.fisher.sgface.util.PreferencesUtils;
import com.android.fisher.sgface.util.ToolUtil;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
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
import com.mwj.lhn.sgdk.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements TakePhoto.TakeResultListener,InvokeListener {
    private Button takephoto_face_bt;
    private Button sign_face_bt;
    //private TextView content_face_info;
    private ImageView sign_face_iv;
    private EditText meetingname;
    private EditText meetingdate;
    private EditText meetingaddress;
    private EditText meetingcontent;
    private EditText statusms;
    private EditText ppl;
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;
    private Uri imageUri;
    private double bsd = -1;
    private String remark3 = "";
    private static String TAG = "fisher_tag";
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        initView();
    }
    private void initView(){
        String meetingnameStr = ToolUtil.trimNull(PreferencesUtils.getString(MainActivity.this,"_meetingname"));
        String meetingdateStr = ToolUtil.trimNull(PreferencesUtils.getString(MainActivity.this,"_meetingdate"));
        String meetingaddressStr = ToolUtil.trimNull(PreferencesUtils.getString(MainActivity.this,"_meetingaddress"));
        String meetingcontentStr = ToolUtil.trimNull(PreferencesUtils.getString(MainActivity.this,"_meetingcontent"));
        String statusmsStr = ToolUtil.trimNull(PreferencesUtils.getString(MainActivity.this,"_statusms"));
        String statusStr = ToolUtil.trimNull(PreferencesUtils.getString(MainActivity.this,"_status"));
        String pplStr = ToolUtil.trimNull(PreferencesUtils.getString(MainActivity.this,"_ppl"));
        meetingname = (EditText)findViewById(R.id.meetingname);
        meetingname.setText(meetingnameStr);
        meetingdate = (EditText)findViewById(R.id.meetingdate);
        meetingdate.setText(meetingdateStr);
        meetingaddress = (EditText)findViewById(R.id.meetingaddress);
        meetingaddress.setText(meetingaddressStr);
        meetingcontent = (EditText)findViewById(R.id.meetingcontent);
        meetingcontent.setText(meetingcontentStr);
        statusms = (EditText)findViewById(R.id.statusms);
        statusms.setText(statusmsStr);
        ppl = (EditText)findViewById(R.id.ppl);
        ppl.setText(pplStr);
        takephoto_face_bt = (Button)findViewById(R.id.takephoto_face_bt);
        sign_face_bt = (Button)findViewById(R.id.sign_face_bt);
        sign_face_iv = (ImageView)findViewById(R.id.sign_face_iv);
        changeETColor(statusStr);
        takephoto_face_bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String statusStr_final = ToolUtil.trimNull(PreferencesUtils.getString(MainActivity.this,"_status"));;
                if(statusStr_final.equals("1")){
                    showUpdataDialog("会议签到","您的会议已签到，系统规定不能重复签到，如有疑问请联系管理员。");
                }else{
                    bsd=-1;
                    File file=new File(Environment.getExternalStorageDirectory(), "/sgface/"+System.currentTimeMillis() + ".jpg");
                    if (!file.getParentFile().exists()){
                        file.getParentFile().mkdirs();
                    }
                    imageUri = Uri.fromFile(file);
                    getTakePhoto().onPickFromCapture(imageUri);
                }
            }
        });
        sign_face_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String statusStr_final = ToolUtil.trimNull(PreferencesUtils.getString(MainActivity.this,"_status"));;
                if(statusStr_final.equals("1")){
                    showUpdataDialog("会议签到","您的会议已签到，系统规定不能重复签到，如有疑问请联系管理员。");
                }else{
                    try{
                        //String remark1 = PreferencesUtils.getString(MainActivity.this,"remark1");
                        //File file1 = new File(remark1);
                        File file2 = FileUtil.getFileFromIV(sign_face_iv);
                        if(file2!=null&&file2.exists()){
                            showProgressBar("正在进行会议签到，请稍候...");
                            //byte[] imgData1 = FileUtil.readFileByBytes(file1);
                            //String imgStr1 = Base64Util.encode(imgData1);
                            //String imgParam1 = URLEncoder.encode(imgStr1, "UTF-8");
                            String imgParam1  = PreferencesUtils.getString(MainActivity.this,"imgdata");
                            byte[] imgData2 = FileUtil.readFileByBytes(file2);
                            String imgStr2 = Base64Util.encode(imgData2);
                            String imgParam2 = URLEncoder.encode(imgStr2, "UTF-8");
                            String url = PrefName.BAIDU_FACE_URL;
                            String accessToken = PreferencesUtils.getString(MainActivity.this,"BAIDU_TOKEN");
                            url = url + "?access_token=" + accessToken;
                            OkHttpClient client = new OkHttpClient
                                    .Builder()
                                    .connectTimeout(180, TimeUnit.SECONDS)
                                    .readTimeout(180,TimeUnit.SECONDS)
                                    .build();
                            RequestBody requestBodyPost = new FormBody
                                    .Builder()
                                    .addEncoded("images",imgParam1 + "," + imgParam2)
                                    .build();
                            Request requestPost = new Request
                                    .Builder()
                                    .url(url)
                                    .addHeader("Content-Type","application/x-www-form-urlencoded")
                                    .addHeader("Connection","Keep-Alive")
                                    .post(requestBodyPost)
                                    .build();
                            client.newCall(requestPost).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    hideProgressBar();
                                    showUpdataDialog("会议签到","会议签到人脸对比失败，请联系系统管理员处理！");
                                }
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String rs = response.body().string();
                                    JsonObject jsonObject = new JsonParser().parse(rs).getAsJsonObject();
                                    int result_num = jsonObject.get("result_num").getAsInt();
                                    if(result_num>0){
                                        JsonArray results = jsonObject.get("result").getAsJsonArray();
                                        JsonObject result0 = results.get(0).getAsJsonObject();
                                        bsd = result0.get("score").getAsDouble();
                                    }else{
                                        bsd = 0;
                                    }
                                    mLocationClient = new LocationClient(getApplicationContext());
                                    mLocationClient.registerLocationListener(myListener);
                                    LocationClientOption option = new LocationClientOption();
                                    option.setIsNeedLocationDescribe(true);
                                    option.setIsNeedAddress(true);
                                    mLocationClient.setLocOption(option);
                                    mLocationClient.start();

                                    //
                                    hideProgressBar();
                                }
                            });
                        }else{
                            showUpdataDialog("会议签到","本机照片或服务器照片不存在，请拍照后进行签到！");
                        }
                    }catch(Exception e){
                        e.printStackTrace();

                    }

                }
            }
        });
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

    /**
     *  获取TakePhoto实例
     * @return
     */
    public TakePhoto getTakePhoto(){
        if (takePhoto==null){
            takePhoto= (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this,this));
        }
        return takePhoto;
    }

    @Override
    public void takeSuccess(TResult result) {
        try{
            Glide.with(this).load(imageUri).override(800,800).into(sign_face_iv);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void takeFail(TResult result, String msg) {

    }

    @Override
    public void takeCancel() {
        hideProgressBar();
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type=PermissionManager.checkPermission(TContextWrap.of(this),invokeParam.getMethod());
        if(PermissionManager.TPermissionType.WAIT.equals(type)){
            this.invokeParam=invokeParam;
        }
        return type;
    }
    //改变颜色
    private void changeETColor(String status){
        if(status.equals("1")){
            statusms.setTextColor(Color.parseColor("#007500"));
        }else{
            statusms.setTextColor(Color.RED);
        }
    }

    //提交最终的签到结果
    private void submitLast(){
        try{
            String url = PrefName.DEFAULT_SERVER_URL + PrefName.FINAL_SIGN_SERVER_URL;
            String _meetinguserid = PreferencesUtils.getString(MainActivity.this,"_meetinguserid");
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Disposition", "form-data;filename=enctype");
            Map<String,String> params = new HashMap<String,String>();
            params.put("id",_meetinguserid);
            params.put("ppl",""+bsd);
            params.put("remark3",remark3);
            File file = FileUtil.getFileFromIV(sign_face_iv);
            String filename = file.getName();
            OkHttpUtils.post()
                    .url(url)
                    .params(params)
                    .headers(headers)
                    .addFile("file", filename, file)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                        }
                        @Override
                        public void onResponse(String response, int id) {
                            JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                            JsonElement tmpJsonElement = null;
                            tmpJsonElement = jsonObject.get("success");
                            boolean success = (tmpJsonElement==null||tmpJsonElement.isJsonNull())?false:tmpJsonElement.getAsBoolean();
                            if(success){
                                tmpJsonElement = jsonObject.get("status");
                                String _status = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());//签到状态 1是已成功   其他是未成功
                                tmpJsonElement = jsonObject.get("statusms");
                                String _statusms = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());//签到状态 1是已成功   其他是未成功
                                tmpJsonElement = jsonObject.get("ppl");
                                String _ppl = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());//相似度
                                statusms.setText(_statusms);
                                ppl.setText(_ppl);
                                PreferencesUtils.putString(MainActivity.this,"_status",_status);
                                changeETColor(_status);
                                if(!_status.equals("1")){
                                    showUpdataDialog("会议签到","签到失败，请本人签到！");
                                }
                            }else{
                                showUpdataDialog("会议签到","签到失败，请联系管理员！");
                            }
                        }
                    });
        }catch(Exception e){
            showToast("上传文件错误！");
            LogE(e.toString());
            e.printStackTrace();
        }
        /*
        String url = PrefName.DEFAULT_SERVER_URL + PrefName.FINAL_SIGN_SERVER_URL;
        String _meetinguserid = PreferencesUtils.getString(MainActivity.this,"_meetinguserid");
        Map<String,String> params = new HashMap<String,String>();
        params.put("id",_meetinguserid);
        params.put("ppl",""+bsd);
        params.put("remark3",remark3);
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
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        JsonElement tmpJsonElement = null;
                        tmpJsonElement = jsonObject.get("success");
                        boolean success = (tmpJsonElement==null||tmpJsonElement.isJsonNull())?false:tmpJsonElement.getAsBoolean();
                        if(success){
                            tmpJsonElement = jsonObject.get("status");
                            String _status = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());//签到状态 1是已成功   其他是未成功
                            tmpJsonElement = jsonObject.get("statusms");
                            String _statusms = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());//签到状态 1是已成功   其他是未成功
                            tmpJsonElement = jsonObject.get("ppl");
                            String _ppl = ToolUtil.trimNull((tmpJsonElement==null||tmpJsonElement.isJsonNull())?"":tmpJsonElement.getAsString());//相似度
                            statusms.setText(_statusms);
                            ppl.setText(_ppl);
                            PreferencesUtils.putString(MainActivity.this,"_status",_status);
                            changeETColor(_status);
                            if(!_status.equals("1")){
                                showUpdataDialog("会议签到","签到失败，请本人签到！");
                            }
                        }else{
                            showUpdataDialog("会议签到","签到失败，请联系管理员！");
                        }
                    }

                });*/
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            String locationDescribe = bdLocation.getLocationDescribe();    //获取位置描述信息
            String addr = bdLocation.getAddrStr();    //获取详细地址信息
            /*String country = bdLocation.getCountry();    //获取国家
            String province = bdLocation.getProvince();    //获取省份
            String city = bdLocation.getCity();    //获取城市
            String district = bdLocation.getDistrict();    //获取区县
            String street = bdLocation.getStreet();    //获取街道信息*/
            remark3 = addr+locationDescribe;
            submitLast();
        }
    }

}