package com.mwj.lhn.sgdk.mwj;

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
import android.graphics.Rect;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.mwj.lhn.sgdk.R;
import com.mwj.lhn.sgdk.Utils;
import com.mwj.lhn.sgdk.db.DatabaseHelper;
import com.mwj.lhn.sgdk.db.UserDao;
import com.mwj.lhn.sgdk.dzqm.SignView;
import com.mwj.lhn.sgdk.pub.Basic;
import com.mwj.lhn.sgdk.pub.URLManage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;


import static com.mwj.lhn.sgdk.pub.Basic.jsonlogin;
import static com.mwj.lhn.sgdk.pub.URLManage.HOST;

public class DDLKActivity extends AppCompatActivity implements
         View.OnClickListener {
    private String msg,tel,cmsg,qmwj,ssgzt,remark1,lmsg,lcid,sms;
    private String ssgxx1,pid,sdqwz;
    private TextView sgdw,sgdd,sglc,sgnr,sgsj,sgfzr,sgfzrdh,tname,ttel,tdgsj,tlksj,tsgzt;
    private Button qbutton,tbutton,hbutton,commit,clear,sgbl,myretrun,nophotebt = null;
    private Bitmap mSignBitmap;
    private String sgqks,signPath,imgflag,imgtitle;
    private Intent intent;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private static final int MESSAGETYPE_01 = 0x0001;
    private static final int MESSAGETYPE_02 = 0x0002;
    private static final String TAG ="22222" ;
    private String lcname,lcsfbx;
    private int lcseq;
    public static ProgressDialog progressDialog = null;
    public static boolean STOP = true;
    private SignView mView;
    MapView mMapView;
    private AMap mAMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   CustomTitle.getCustomTitle(this, "签名确认");
        setContentView(R.layout.activity_ddlk);
        mMapView = (MapView) findViewById(R.id.mwjmap11);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(10000);
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        mAMap = mMapView.getMap();
        mAMap.moveCamera(CameraUpdateFactory.zoomBy(7));
        mAMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        mAMap.setMyLocationEnabled(true);
        myretrun= (Button) findViewById(R.id.my_return);
        myretrun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        Bundle bundle = this.getIntent().getExtras();
        msg = bundle.getString("msg");
        sms = bundle.getString("sms");
        tel = bundle.getString("tel");
        lcseq = Integer.parseInt(bundle.getString("lcseq"));
        lcname = bundle.getString("lcname");
        lcid= bundle.getString("lcid");

        init(msg);
        initLocation();
    }

    private void init(String nmsg) {
        tsgzt=(TextView)findViewById(R.id.dqwz);
        mView = (SignView) findViewById(R.id.signView00);
        commit = (Button) findViewById(R.id.my_commit);
        clear = (Button) findViewById(R.id.my_clear);
        TextView qdinfo=(TextView)findViewById(R.id.qdinfo);
        nophotebt= (Button) findViewById(R.id.my_nophcommit);
        commit.setText(lcname);
        qdinfo.setText("请确认签名后在签认！");
        imgtitle=lcname;

        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog111();
            }
        });
        nophotebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog222();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mView.clear();
            }
        });
            try {
                JSONObject jsonObject = jsonlogin(msg);
                String _value = jsonObject.getString("FDayPlanVOList");
                JSONArray arr = new JSONArray(_value);
                JSONObject jsonObject1 = arr.getJSONObject(0);
                String fdayPlanMores=jsonObject1.getString("fdayPlanMoreVO1s");
                JSONArray arr1 = new JSONArray(fdayPlanMores);
                for(int m=0;m<arr1.length();m++){
                    JSONObject jsonObject2 = arr1.getJSONObject(m);
                    if(jsonObject2.getString("tel_number").equals(tel)){
                        pid=null2str(jsonObject2.getString("id"));
                    }
                }
            } catch (Exception e) {
            }





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
        tsgzt.setText(sdqwz);
    }

    /**
     * 默认的定位参数
     * @since 2.8.0
     * @author hongming.wang
     *
     */
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
            sdqwz="";
            if (null != location) {

                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if(location.getErrorCode() == 0){
                    sdqwz="当前位置: "+location.getPoiName()+ "--" + Utils.formatUTC(location.getTime(), "MM-dd HH:mm") + "\n";

                } else {
                    //定位失败
                    sdqwz="定位失败";

                }


                //解析定位结果，
            } else {
                sdqwz="定位失败";
            }
            tsgzt.setText(sdqwz);
        }
    };


    /**
     * 获取GPS状态的字符串
     * @param statusCode GPS状态码
     * @return
     */
    private String getGPSStatusString(int statusCode){
        String str = "";
        switch (statusCode){
            case AMapLocationQualityReport.GPS_STATUS_OK:
                str = "GPS状态正常";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER:
                str = "手机中没有GPS Provider，无法进行GPS定位";
                break;
            case AMapLocationQualityReport.GPS_STATUS_OFF:
                str = "GPS关闭，建议开启GPS，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_MODE_SAVING:
                str = "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION:
                str = "没有GPS定位权限，建议开启gps定位权限";
                break;
        }
        return str;
    }



    public void onClick(View v) {

    }
    private static Handler handler = new Handler() {

        public void handleMessage(Message message) {
            switch (message.what) {
                case MESSAGETYPE_01:
                    // 刷新UI，显示数据，并关闭进度条
                    progressDialog.dismiss(); // 关闭进度条
                    break;
                case MESSAGETYPE_02:
                    // 刷新UI，显示数据，并关闭进度条
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
    public void saveSign(Bitmap bit,boolean flag) {
        if(bit!=null){
            mSignBitmap = bit;
            signPath = createFile(flag);
        }
        else{
        }
    }

    /**
     * @return
     */
    private String createFile(boolean flag) {
        ByteArrayOutputStream baos = null;
        String _path = null;
        try {
            String sign_dir = Environment.getExternalStorageDirectory().getPath() + "/";
            qmwj=(int)(Math.random()*100)+ String.valueOf(System.currentTimeMillis()) + ".jpg";
            _path = sign_dir + qmwj;
            baos = new ByteArrayOutputStream();

            mSignBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] photoBytes = baos.toByteArray();
            if (photoBytes != null) {
                new FileOutputStream(new File(_path)).write(photoBytes);
                File mInputFile = new File(_path);
                filetobmap(mInputFile);
                if (flag==false) {
                    ContentValues cv = new ContentValues();
                    cv.put("zyid",lcid);
                    cv.put("zysfbc","N");
                    cv.put("moreid", pid);
                    cv.put("sdurl", _path);
                    cv.put("title", lcname);
                    cv.put("imageurl", qmwj);
                    cv.put("flag", "未上传");
                    DatabaseHelper dbHelper1 = new DatabaseHelper(DDLKActivity.this, "MY.db");
                    //取得一个只读的数据库对象
                    SQLiteDatabase db2 = dbHelper1.getReadableDatabase();
                    UserDao usd=new UserDao();
                    usd.insSgimg(db2,tel+sms,cv);
                    usd.uplc(db2,tel+sms,lcid);
                    usd.updglg(db2,tel+sms);
                    db2.close();
                    finish();
                }
                else {
                    try {
                        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
                        client.setTimeout(30000);
                        String uri = _path;
                        showProgress(DDLKActivity.this);
                        RequestParams params = new RequestParams();
                        long time = System.currentTimeMillis();
                        Date date = new Date(time);
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                        String dtime = format.format(date);


                        final File bfile = new File(uri);
                        params.put("sdurl", uri);
                        params.put("file", bfile);
                        params.put("imageurl", qmwj);
                        params.put("moreid", pid);
                        params.put("qmwj", qmwj);
                        params.put("zyid", lcid);
                        params.put("title", lcname);
                        params.put("zysfbc", "N");


                        client.post(Basic.myurl_host + "myUpload", params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                                //  String msg=new String(bytes,"ISO-8859-1");
                                Toast.makeText(DDLKActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                                DatabaseHelper dbHelper1 = new DatabaseHelper(DDLKActivity.this, "MY.db");
                                SQLiteDatabase db2 = dbHelper1.getReadableDatabase();
                                UserDao usd = new UserDao();
                                usd.uplc(db2, tel+sms, lcid);
                                usd.updglg(db2,tel+sms);
                                db2.close();
                                closeProgress();
                                finish();
                            }

                            @Override
                            public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                                Toast.makeText(DDLKActivity.this, "上传失败", Toast.LENGTH_LONG).show();
                                closeProgress();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            else{
                Toast.makeText(DDLKActivity.this, "请拍照后再上传", Toast.LENGTH_LONG).show();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return _path;
    }
    private void savejp(boolean flag) {
        View view = getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        Bitmap bitmap = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Log.i("TAG", "" + statusBarHeight);

        // 获取屏幕长和高
        int width = getWindowManager().getDefaultDisplay().getWidth();
        LinearLayout ly1=(LinearLayout)findViewById(R.id.llay11);
        LinearLayout ly2=(LinearLayout)findViewById(R.id.llay22);
        int height =ly1.getHeight()+ly2.getHeight()+10;
        Bitmap b = Bitmap.createBitmap(bitmap, 0, statusBarHeight, width, height);
        saveSign(b,flag);
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mSignBitmap != null) {
            mSignBitmap.recycle();
        }
    }
    public void upphote(String uid, String fname,String photetype) {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String dtime=format.format(date);
        RequestParams params = new RequestParams();// 绑定参数
        String filename = fname;
        params.put("moreid", uid);
        params.put("imageurl", filename);
        params.put("title", imgtitle);

        AsyncHttpClient client = new AsyncHttpClient(true,80,443);

        client.get(HOST +"insertDayPlanImgs.json", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject responseBody) {
                // TODO Auto-generated method stub
                super.onSuccess(statusCode, headers, responseBody);
                try {
                    String dbResult1=responseBody.getString("dbResult");
                    JSONObject jsonObject = jsonlogin(dbResult1);
                    String result = jsonObject.getString("result");

                    if(result.equals("1")){
                        finish();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "登记失败，请稍候再试", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable e, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, e, errorResponse);
                     closeProgress();
            }
        });
    }
    protected void dialog111() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DDLKActivity.this);
        builder.setMessage("签名后不可更改，请确认?");
        builder.setTitle("提示");
        builder.setPositiveButton("确认",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        savejp(true);
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
    protected void dialog222() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DDLKActivity.this);
        builder.setMessage("签名后不可更改，确认离线保存，待网络通常后手动上传?");
        builder.setTitle("提示");
        builder.setPositiveButton("确认",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        savejp(false);
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
    private Bitmap addTimeFlag(Bitmap src){
        // 获取原始图片与水印图片的宽与高
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(newBitmap);
        // 往位图中开始画入src原始图片
        mCanvas.drawBitmap(src, 0, 0, null);
        //添加文字
        Paint textPaint = new Paint();
        textPaint.setColor(Color.RED) ;
        textPaint.setTextSize(30);
        String dwqz=" "+sdqwz;
        mCanvas.drawText(dwqz, (float)(w*1)/7, (float)(h*14)/15, textPaint);
        mCanvas.save(Canvas.ALL_SAVE_FLAG);
        mCanvas.restore();
        return newBitmap ;
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
            newbm.compress(Bitmap.CompressFormat.JPEG, 85, out);
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
    private void startLocation(){
        //根据控件的选择，重新设置定位参数
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void stopLocation(){
        // 停止定位
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void destroyLocation(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            mMapView.onDestroy();
            locationClient = null;
            locationOption = null;

        }
        if (mSignBitmap != null) {
            mSignBitmap.recycle();
        }
    }
}

