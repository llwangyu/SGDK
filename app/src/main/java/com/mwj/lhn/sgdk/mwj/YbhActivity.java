package com.mwj.lhn.sgdk.mwj;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.mwj.lhn.sgdk.R;
import com.mwj.lhn.sgdk.Utils;
import com.mwj.lhn.sgdk.db.DatabaseHelper;
import com.mwj.lhn.sgdk.db.UserDao;
import com.mwj.lhn.sgdk.dzqm.LQRPhotoSelectUtils;
import com.mwj.lhn.sgdk.dzqm.photeActivity;
import com.mwj.lhn.sgdk.pub.Basic;
import com.mwj.lhn.sgdk.pub.URLManage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;


import static com.mwj.lhn.sgdk.pub.Basic.getFileSize;
import static com.mwj.lhn.sgdk.pub.Basic.jsonlogin;

public class YbhActivity extends AppCompatActivity {
    private Handler mHandler = new Handler();
    private Button mBtnTakePhoto, btupload,nophoteqr,wzlqr;
    private Button mBtnSelectPhoto;
    private LQRPhotoSelectUtils mLqrPhotoSelectUtils;
    private ImageView mIvPic;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private TextView sgxx,dqwz,sggs;
    private String muri, pid, filename,remark1,qdwz,tel="";
    private static final int MESSAGETYPE_01 = 0x0001;
    private static final String TAG = "22222";
    public static ProgressDialog progressDialog = null;
    public static boolean STOP = true;
    public String photetype,msg,lcname,lcid,sgqks;
    public int sgtype=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomTitle.getCustomTitle(this, "影像信息采集");

        setContentView(R.layout.activity_ybh);
        nophoteqr= (Button) findViewById(R.id.nophoteqr);
        wzlqr= (Button) findViewById(R.id.wzlqr);
        mBtnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);
        mBtnSelectPhoto = (Button) findViewById(R.id.btnSelectPhoto);
        Bundle bundle = this.getIntent().getExtras();
        pid = bundle.getString("pid");
        msg = bundle.getString("msg");
        lcname = bundle.getString("lcname");
        lcid= bundle.getString("lcid");
        tel= bundle.getString("tel");
        sgqks= bundle.getString("sgqks");
        mIvPic = (ImageView) findViewById(R.id.ivPic);
        dqwz=(TextView) findViewById(R.id.dqwz);
//


//
        sgxx=(TextView) findViewById(R.id.sgxx);
        sgxx.setText(lcname);
        Button dwbt=findViewById(R.id.dwbt);
        dwbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocation();
                PermissionGen.with(YbhActivity.this)
                        .permissions(
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        ).request();
            }
        });
        wzlqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper dbHelper1 = new DatabaseHelper(YbhActivity.this, "MY.db");
                //取得一个只读的数据库对象
                SQLiteDatabase db2 = dbHelper1.getReadableDatabase();
                UserDao usd=new UserDao();
                usd.uplc(db2,tel,lcid);
                db2.close();
                finish();
            }
        });

        sggs=(TextView) findViewById(R.id.sggs);
        nophoteqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog111();
            }
        });
        sggs.setText(sgqks);
        init();
        initListener();
        initLocation();
        btupload = (Button) findViewById(R.id.btnuploadPhoto);
        btupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(muri!=null){

                    try {
                        AsyncHttpClient client = new AsyncHttpClient(true,80,443);
                        client.setTimeout(30000);
                        String uri = muri;
                        RequestParams params = new RequestParams();
                        final File bfile=new File(uri);
                        long size = getFileSize(bfile);
                        if(size<2900000) {

                            params.put("zyid",lcid);
                            params.put("zysfbc","N");
                            params.put("file", bfile);
                            params.put("moreid", pid);
                            params.put("sdurl", uri);
                            params.put("title", lcname);
                            params.put("imageurl", filename);

                            showProgress(YbhActivity.this);
                            client.post(Basic.myurl_host+"myUpload", params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                                    try {
                                        String msg=new String(bytes,"utf-8").toString();

                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(YbhActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                                    DatabaseHelper dbHelper1 = new DatabaseHelper(YbhActivity.this, "MY.db");
                                    //取得一个只读的数据库对象
                                    SQLiteDatabase db2 = dbHelper1.getReadableDatabase();
                                    UserDao usd=new UserDao();
                                    usd.uplc(db2,tel,lcid);
                                    db2.close();
                                    closeProgress();
                                    finish();
                                }

                                @Override
                                public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                                    Toast.makeText(YbhActivity.this, "上传失败", Toast.LENGTH_LONG).show();
                                    closeProgress();

                                }
                            });
                        }
                        else{
                            Toast.makeText(YbhActivity.this, "上传失败图片太大", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(YbhActivity.this, "请拍照后再上传", Toast.LENGTH_LONG).show();

                }
            }
        });

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

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            qdwz="";
            if (null != location) {

                StringBuffer sb = new StringBuffer();
                if(location.getErrorCode() == 0){
                    qdwz=location.getPoiName()+" "+Utils.formatUTC(location.getTime(), "MM-dd HH:mm") + "\n";
                } else {
                    qdwz="定位失败";
                }
            } else {
                qdwz="定位失败";
            }
            dqwz.setText(qdwz);
        }
    };


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
        mLqrPhotoSelectUtils = new LQRPhotoSelectUtils(this, new LQRPhotoSelectUtils.PhotoSelectListener() {
            @Override
            public void onFinish(File outputFile, Uri outputUri) {
                filetobmap(outputFile);
                muri = outputFile.getAbsolutePath();
                filename = outputFile.getName();
                Glide.with(YbhActivity.this).load(outputUri).into(mIvPic);
            }
        }, false);  }
    private void initListener() {
        mBtnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionGen.with(YbhActivity.this)
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
                PermissionGen.needPermission(YbhActivity.this,
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
                intent.setData(Uri.parse("package:" + YbhActivity.this.getPackageName()));
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
    protected void dialog111() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(YbhActivity.this);
        builder.setMessage("确定将离线保存资料，在网络畅通时手动上传?");
        builder.setTitle("提示");
        builder.setPositiveButton("确认",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                            try {
                                String uri = muri;
                                if(!uri.equals("")) {
                                    ContentValues cv = new ContentValues();
                                    cv.put("zyid",lcid);
                                    cv.put("zysfbc","N");
                                    cv.put("moreid", pid);
                                    cv.put("sdurl", uri);
                                    cv.put("title", lcname);
                                    cv.put("imageurl", filename);
                                    cv.put("flag", "未上传");
                                    DatabaseHelper dbHelper1 = new DatabaseHelper(YbhActivity.this, "MY.db");
                                    //取得一个只读的数据库对象
                                    SQLiteDatabase db2 = dbHelper1.getReadableDatabase();
                                    UserDao usd=new UserDao();
                                    usd.insSgimg(db2,tel,cv);
                                    usd.uplc(db2,tel,lcid);
                                    db2.close();
                                    finish();
                                }
                                else {
                                    Toast.makeText(YbhActivity.this, "请拍照后再上传", Toast.LENGTH_LONG).show();
                                }
                            }
                         catch (Exception e) {
                            e.printStackTrace();
                        }
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != locationClient) {
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }
    private void startLocation(){
        locationClient.setLocationOption(locationOption);
        locationClient.startLocation();
    }
    private void stopLocation(){
        locationClient.stopLocation();
    }

    private void destroyLocation(){
        if (null != locationClient) {
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
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
            newbm.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap.recycle();
        newbm.recycle();
    }
    private Bitmap addTimeFlag(Bitmap src){
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        Canvas mCanvas = new Canvas(newBitmap);
        mCanvas.drawBitmap(src, 0, 0, null);
        Paint textPaint = new Paint();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        String time = sdf.format(new Date(System.currentTimeMillis()));
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(50);
        mCanvas.drawText(qdwz,(float)(w*1)/7,(float)(h*14)/15, textPaint);
        mCanvas.save(Canvas.ALL_SAVE_FLAG);
        mCanvas.restore();
        src.recycle();
        return newBitmap ;
    }
}