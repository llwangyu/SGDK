package com.mwj.lhn.sgdk.dzqm;

/**
 * Created by LHN on 2017/11/15.
 */

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.mwj.lhn.sgdk.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mwj.lhn.sgdk.Utils;
import com.mwj.lhn.sgdk.mwj.CustomTitle;
import com.mwj.lhn.sgdk.mwj.YbhActivity;
import com.mwj.lhn.sgdk.mwj.login;
import com.mwj.lhn.sgdk.pub.Basic;
import com.mwj.lhn.sgdk.pub.URLManage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

import static com.mwj.lhn.sgdk.pub.Basic.jsonlogin;


public class photeActivity extends AppCompatActivity implements
        View.OnClickListener{

    private Button mBtnTakePhoto, btupload;
    private Button mBtnSelectPhoto;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    private LQRPhotoSelectUtils mLqrPhotoSelectUtils;
    private ImageView mIvPic;
    private TextView bclx;
    private Button bcbt;
    private String muri, pid, filename,tel,sdqwz;
    private static final int MESSAGETYPE_01 = 0x0001;
    private static final String TAG = "22222";
    public static ProgressDialog progressDialog = null;
    public static boolean STOP = true;
    public String photetype,imgflag,msg;
    private String[] items1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomTitle.getCustomTitle(this, "影像资料");

        setContentView(R.layout.activity_phote);
        mBtnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);
        mBtnSelectPhoto = (Button) findViewById(R.id.btnSelectPhoto);
        Bundle bundle = this.getIntent().getExtras();
        pid = bundle.getString("pid");
        msg = bundle.getString("msg");
        tel = bundle.getString("tel");
        mIvPic = (ImageView) findViewById(R.id.ivPic);
        bclx=(TextView) findViewById(R.id.bclx);

        init();
        initListener();
        initLocation();

    }
    private void initLocation(){
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
        startLocation();
    }
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            sdqwz="";
            if (null != location) {

                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if(location.getErrorCode() == 0){
                    sdqwz=location.getPoiName()+ "--" + Utils.formatUTC(location.getTime(), "dd HH:mm") + "\n";

                } else {
                    //定位失败
                    sdqwz="定位失败";

                }


                //解析定位结果，
            } else {
                sdqwz="定位失败";
            }
        }
    };
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(10000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }
    private void startLocation(){
        //根据控件的选择，重新设置定位参数
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }
    public void onClick(View v) {
        if (v.getId() == R.id.bcbt) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("关键步骤");
            builder.setSingleChoiceItems(items1, 0, new DialogInterface.OnClickListener() {// 2默认的选中
                @Override
                public void onClick(DialogInterface dialog, int which) {// which是被选中的位置
                    bclx.setText(items1[which]);
                    dialog.dismiss();//随便点击一个item消失对话框，不用点击确认取消
                }
            });
            builder.show();
        }
    }
    public void upphote(String uid, String fname,String photetype) {
        RequestParams params = new RequestParams();// 绑定参数
        String filename =  fname;
        params.put("moreid", uid);
        params.put("imageurl", filename);
        params.put("title", photetype);
        params.put("flag", imgflag);
        URLManage.showInfos("insertDayPlanImgs.json", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject responseBody) {
                // TODO Auto-generated method stub
                super.onSuccess(statusCode, headers, responseBody);
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable e, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, e, errorResponse);
                //     closeProgress();
            }
        });
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
        progressDialog = ProgressDialog.show(lg, "请稍候", "正在传输数据！！");
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

    public String null2str(String obj) {
        if (obj.equals("null")) {
            obj = "";
        }
        if (obj == null) {
            obj = "";
        }
        return obj;
    }

    private void init() {
        try {
            JSONObject jsonObject = jsonlogin(msg);
            String _value = jsonObject.getString("FDayPlanVOList");
            JSONArray arr = new JSONArray(_value);
            {
                JSONObject jsonObject1 = arr.getJSONObject(0);
                String fdayPlanMores = jsonObject1.getString("fdayPlanMoreVO1s");
                JSONArray arr1 = new JSONArray(fdayPlanMores);
                for (int m = 0; m < arr1.length(); m++) {
                    JSONObject jsonObject2 = arr1.getJSONObject(m);
                    if (jsonObject2.getString("tel_number").equals(tel)) {
                        String gjzd= jsonObject2.getString("fZdZylcs");
                        JSONArray gja = new JSONArray(gjzd);
                        items1 = new String[gja.length()];
                        for (int i = 0; i < gja.length(); i++) {
                            JSONObject jsonObjectzd = gja.getJSONObject(i);
                           // if(jsonObjectzd.getString("sfbx").equals("N")) {
                                items1[i] = jsonObjectzd.getString("id") + "." + jsonObjectzd.getString("name");
                           // }
                        }

                    }
                }
            }

        } catch (Exception e) {
        }
        bcbt=(Button)findViewById(R.id.bcbt);
        bcbt.setOnClickListener(this);

        btupload = (Button) findViewById(R.id.btnuploadPhoto);
        btupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bclxs=bclx.getText().toString();
                if(bclxs.length()>3) {
                    if (muri != null) {
                        try {
                            AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
                            client.setTimeout(30000);
                            // String path = IMGSERVLET;
                            String uri = muri;
                            RequestParams params = new RequestParams();
                            final File bfile = new File(uri);
                            params.put("file", bfile);
                            params.put("sdurl", uri);
                            params.put("moreid", pid);
                            params.put("imageurl", filename);
                            params.put("title", bclxs.substring(bclxs.indexOf(".")));
                            params.put("zyid",bclxs.substring(0,bclxs.indexOf(".")));
                            params.put("zysfbc","Y");
                            showProgress(photeActivity.this);
                            client.post(Basic.myurl_host + "myUpload", params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                                    Toast.makeText(photeActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                                    closeProgress();
                                    mIvPic.setImageBitmap(null);
                                    try {
                                        String msg = new String(bytes, "UTF-8");
                                        System.out.println("---"+msg);
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    //  upphote(pid, filename, photetype);

                                }

                                @Override
                                public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                                    Toast.makeText(photeActivity.this, "上传失败", Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        Toast.makeText(photeActivity.this, "请拍照后再上传", Toast.LENGTH_LONG).show();

                    }
                }
                else
                {
                    Toast.makeText(photeActivity.this, "请先选择上报类型", Toast.LENGTH_LONG).show();

                }
            }
        });
        mLqrPhotoSelectUtils = new LQRPhotoSelectUtils(this, new LQRPhotoSelectUtils.PhotoSelectListener() {
            @Override
            public void onFinish(File outputFile, Uri outputUri) {
                filetobmap(outputFile);
                muri = outputFile.getAbsolutePath();
                filename = outputFile.getName();
                Glide.with(photeActivity.this).load(outputUri).into(mIvPic);
            }
        }, false);  }

    private void initListener() {
        mBtnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionGen.with(photeActivity.this)
                        .addRequestCode(LQRPhotoSelectUtils.REQ_TAKE_PHOTO)
                        .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                        ).request();

            }
        });
        mBtnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionGen.needPermission(photeActivity.this,
                        LQRPhotoSelectUtils.REQ_SELECT_PHOTO,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}
                );
            }
        });
    }

    @PermissionSuccess(requestCode = LQRPhotoSelectUtils.REQ_TAKE_PHOTO)
    private void takePhoto() {
        mLqrPhotoSelectUtils.takePhoto();
    }

    @PermissionSuccess(requestCode = LQRPhotoSelectUtils.REQ_SELECT_PHOTO)
    private void selectPhoto() {
        mLqrPhotoSelectUtils.selectPhoto();
    }

    @PermissionFail(requestCode = LQRPhotoSelectUtils.REQ_TAKE_PHOTO)
    private void showTip1() {
        //        Toast.makeText(getApplicationContext(), "不给我权限是吧，那就别玩了", Toast.LENGTH_SHORT).show();
        showDialog();
    }

    @PermissionFail(requestCode = LQRPhotoSelectUtils.REQ_SELECT_PHOTO)
    private void showTip2() {
        //        Toast.makeText(getApplicationContext(), "不给我权限是吧，那就别玩了", Toast.LENGTH_SHORT).show();
        showDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLqrPhotoSelectUtils.attachToActivityForResult(requestCode, resultCode, data);
    }
    public void  filetobmap(File outputFile) {
        Bitmap bitmap= BitmapFactory.decodeFile(outputFile.getAbsolutePath());
        Bitmap newbm = addTimeFlag(bitmap);
        File f = new File(outputFile.getAbsolutePath());
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            newbm.compress(Bitmap.CompressFormat.JPEG, 75, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        bitmap.recycle();
        newbm.recycle();
    }
    private Bitmap addTimeFlag(Bitmap src){
        // 获取原始图片与水印图片的宽与高
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        Canvas mCanvas = new Canvas(newBitmap);
        // 往位图中开始画入src原始图片
        mCanvas.drawBitmap(src, 0, 0, null);
        //添加文字
        Paint textPaint = new Paint();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        String time = sdf.format(new Date(System.currentTimeMillis()));
        textPaint.setColor(Color.RED) ;
        textPaint.setTextSize(70);

        mCanvas.drawText("补充资料"+sdqwz, (float)(w*1)/7, (float)(h*14)/15, textPaint);
        mCanvas.save(Canvas.ALL_SAVE_FLAG);
        mCanvas.restore();
        src.recycle();
        return newBitmap ;
    }

    public void showDialog() {
        //创建对话框创建器
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置对话框显示小图标
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        //设置标题
        builder.setTitle("权限申请");
        //设置正文
        builder.setMessage("在设置-应用-虎嗅-权限 中开启相机、存储权限，才能正常使用拍照或图片选择功能");

        //添加确定按钮点击事件
        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {//点击完确定后，触发这个事件

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //这里用来跳到手机设置页，方便用户开启权限
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + photeActivity.this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //添加取消按钮点击事件
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        //使用构建器创建出对话框对象
        AlertDialog dialog = builder.create();
        dialog.show();//显示对话框
    }
    public static void releaseImageViewResouce(ImageView imageView) {
        if (imageView == null) return;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }
    private void stopLocation(){
        // 停止定位
        locationClient.stopLocation();
    }
    private void destroyLocation(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;

        }

    }
}

