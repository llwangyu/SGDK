package com.mwj.lhn.sgdk.mwj;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mwj.lhn.sgdk.R;
import com.mwj.lhn.sgdk.db.DatabaseHelper;
import com.mwj.lhn.sgdk.db.UserDao;
import com.mwj.lhn.sgdk.dzqm.LQRPhotoSelectUtils;
import com.mwj.lhn.sgdk.dzqm.SignView;
import com.mwj.lhn.sgdk.dzqm.photeActivity;
import com.mwj.lhn.sgdk.pub.Basic;
import com.mwj.lhn.sgdk.pub.URLManage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import kr.co.namee.permissiongen.PermissionGen;

import static com.mwj.lhn.sgdk.R.color.colorAccent;
import static com.mwj.lhn.sgdk.pub.Basic.forjson;
import static com.mwj.lhn.sgdk.pub.Basic.jsonlogin;

public class SgxxActivity extends AppCompatActivity implements
        View.OnClickListener {
    private String msg,tel,cmsg,qmwj,ssgzt,remark1,sgqks,sms,kid,kname="";
    private String name,gtel,gunit,dgsj,lksj,pid,fxwt,lc;
    private TextView sgdw,sgdd,sglc,sgnr,sgsj,sgfzr,sgfzrdh,tname,ttel,tdgsj,tlksj,tsgzt,tbm;
    private Button sgbl,sqxx,fxwz,bczl,ddsc,spps = null;
    private Bitmap mSignBitmap;
    private String signPath,nmsg,dgqm,lgqm,dgqm1,lgqm1,lcname,lcseq,lcsfbx,lcid;
    private Intent intent;
    private static final int MESSAGETYPE_01 = 0x0001;
    private static final String TAG ="22222" ;
    public static ProgressDialog progressDialog = null;
    public static boolean STOP = true;
    private boolean dgflag=true,lgflag=true;
    private UserDao usd;
    private TextView dlxx;
    private String dlxxs,fxwtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        CustomTitle.getCustomTitle(this, "施工信息");
        setContentView(R.layout.activity_main2);

        usd=new UserDao();
        Bundle bundle = this.getIntent().getExtras();
        msg = bundle.getString("msg");
        tel = bundle.getString("tel");
        sms= bundle.getString("sms");

        ddsc=(Button)findViewById(R.id.ddsc) ;
        sqxx=(Button)findViewById(R.id.sqxx) ;
        fxwz=(Button)findViewById(R.id.fxwz) ;
        spps=(Button)findViewById(R.id.spps) ;
        bczl=(Button)findViewById(R.id.bczl) ;
        ddsc.setOnClickListener(this);
        fxwz.setOnClickListener(this);
        bczl.setOnClickListener(this);
        sgbl=(Button)findViewById(R.id.btbl) ;
        sqxx.setOnClickListener(this);
        spps.setOnClickListener(this);
        sgbl.setOnClickListener(this);
        init(msg);
    }
    @Override
    protected void onResume() {
        super.onResume();
        DatabaseHelper dbHelper1 = new DatabaseHelper(SgxxActivity.this, "MY.db");
        //取得一个只读的数据库对象
        SQLiteDatabase db2 = dbHelper1.getReadableDatabase();
        String sgxx= usd.getsgxx(db2,tel+sms);

        int imgc=usd.getimgc(db2,tel+sms);
        if(imgc>0){
            ddsc.setTextColor(Color.RED);
            ddsc.setText(imgc+"个待上传！");
        }
        else{
            ddsc.setText("离线影像");
        }
        db2.close();
        msg = sgxx.toString();
        init(msg);
        nmsg=msg;
    }

    private void getreflash(){


//        dgflag=true;lgflag=true;
//        RequestParams params = new RequestParams();// 绑定参数
//        long time = System.currentTimeMillis();
//        Date date = new Date(time);
//        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
//        String dtime=format.format(date);
//        params.put("tel_number", tel);
//        params.put("sendyzm",sms);
//        showProgress(SgxxActivity.this);
//        URLManage.showInfos("findDayPlans.json",params, new JsonHttpResponseHandler(){
//            @Override
//            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject  responseBody) {
//                // TODO Auto-generated method stub
//                super.onSuccess(statusCode, headers, responseBody);
//                closeProgress();
//                try {
//                    msg = responseBody.toString();
//                    init(msg);
//                    nmsg=msg;
//                } catch (Exception ex) {
//                }
//
//            }
//            @Override
//            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable e, JSONObject errorResponse) {
//                super.onFailure(statusCode, headers, e, errorResponse);
//                closeProgress();
//            }
//        });
    }
    private void init(String nmsg) {
        tsgzt=(TextView)findViewById(R.id.sgzt);
        dlxx=(TextView)findViewById(R.id.dlxx);
        name="";
        gtel="";
        kid="";
        gunit="";
        dgsj="";
        lksj="";
        cmsg=nmsg;
        String sgdws="施工单位：";
        String sgdds="施工地点：";
        String sglcs="里程：";
        String sgnrs="施工内容：";
        String sgsjs="施工时间：";
        String sgfzrs="项目负责人：";
        String fzrdhs="负责人电话：";
        tsgzt.setText("");
        try {
            JSONObject jsonObject = jsonlogin(nmsg);
            String _value = jsonObject.getString("FDayPlanVOList");
            JSONArray arr = new JSONArray(_value);
           // for(int m=0;m<arr.length();m++)
            {
                JSONObject jsonObject1 = arr.getJSONObject(0);
                kid=jsonObject1.getString("id");
                sgqks = "施工概况：" + jsonObject1.getString("planDate") + "日" + jsonObject1.getString("doTime") +
                        jsonObject1.getString("location") + jsonObject1.getString("length") + jsonObject1.getString("doUnit")
                        + jsonObject1.getString("type");
                sgdws = "施工单位：" + jsonObject1.getString("doUnit");
                sgdds = "施工地点：" + null2str(jsonObject1.getString("location"));
                sglcs = "里程：" + null2str(jsonObject1.getString("length"));
                sgnrs = "施工内容：" + jsonObject1.getString("type");
                String Ssgsj = jsonObject1.getString("planDate");
                if (Ssgsj.length() > 5) {
                    Ssgsj = Ssgsj.substring(0, 4) + "-" + Ssgsj.substring(4, 6) + "-" + Ssgsj.substring(6, 8);
                }
                sgsjs = "施工时间：" + Ssgsj + " " + jsonObject1.getString("doTime");
                sgfzrs = "项目负责人：" + jsonObject1.getString("principal");
                fzrdhs = "负责人电话：" + jsonObject1.getString("telNumber");
                String fdayPlanMores = jsonObject1.getString("fdayPlanMoreVO1s");

                JSONArray arr1 = new JSONArray(fdayPlanMores);
                for (int m = 0; m < arr1.length(); m++) {
                    JSONObject jsonObject2 = arr1.getJSONObject(m);
                    if (jsonObject2.getString("tel_number").equals(tel)) {
                        kname= null2str(jsonObject2.getString("princpal"));
                        dgqm1 = null2str(jsonObject2.getString("dgqmimg"));
                        lgqm1 = null2str(jsonObject2.getString("lgqmimg"));
                        pid = null2str(jsonObject2.getString("id"));
                        fxwtn = null2str(jsonObject2.getString("fDayPlanProblems"));
                        dlxxs = null2str(jsonObject2.getString("princpal")) + "  " + null2str(jsonObject2.getString("tel_number")) + " " + null2str(jsonObject2.getString("unit"));

                    }
                }
                for (int m = 0; m < arr1.length(); m++) {
                    JSONObject jsonObject2 = arr1.getJSONObject(m);
                    if (m == 0) {
                        name = null2str(jsonObject2.getString("princpal")) + "  " + null2str(jsonObject2.getString("tel_number")) + "  单位：" +
                                null2str(jsonObject2.getString("unit")) ;
                    }
                    if (m == 1) {
                        gtel = null2str(jsonObject2.getString("princpal")) + "  " + null2str(jsonObject2.getString("tel_number")) + "  单位：" +
                                null2str(jsonObject2.getString("unit")) ;
                    }
                    if (m == 2) {
                        gunit = null2str(jsonObject2.getString("princpal")) + "  " + null2str(jsonObject2.getString("tel_number")) + "  单位：" +
                                null2str(jsonObject2.getString("unit")) ;
                    }
                }
            }
        } catch (Exception e) {
        }
        DatabaseHelper dbHelper1 = new DatabaseHelper(SgxxActivity.this, "MY.db");
        //取得一个只读的数据库对象
        SQLiteDatabase db2 = dbHelper1.getReadableDatabase();
        lc= usd.getlc(db2,tel+sms);
        dgsj=usd.getdgsj(db2,tel+sms);
        lksj=usd.getlgsj(db2,tel+sms);
        db2.close();
        if(!lc.equals("")) {
            JSONObject jsonlc = jsonlogin(lc);
            try {
                lcname = jsonlc.getString("name");
                lcseq = jsonlc.getString("seq");
                lcsfbx = jsonlc.getString("sfbx");
                lcid = jsonlc.getString("id");
                tsgzt.setText(lcseq+"."+lcname);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            tsgzt.setText("已结束！");
            sgbl.setVisibility(View.INVISIBLE);
        }
        sgdw=(TextView)findViewById(R.id.sgdw);sgdw.setText(sgdws);
        sgdd=(TextView)findViewById(R.id.sgdd);sgdd.setText(sgdds);
        sglc=(TextView)findViewById(R.id.sglc);sglc.setText(sglcs);
        sgnr=(TextView)findViewById(R.id.sgnr);sgnr.setText(sgnrs);
        sgsj=(TextView)findViewById(R.id.sgsj);sgsj.setText(sgsjs);
        sgfzr=(TextView)findViewById(R.id.sgfzr);sgfzr.setText(sgfzrs);
        sgfzrdh=(TextView)findViewById(R.id.fzrdh);sgfzrdh.setText(fzrdhs);

        tname=(TextView)findViewById(R.id.name);tname.setText(name);
        ttel=(TextView)findViewById(R.id.tel);ttel.setText(gtel);
        tbm=(TextView)findViewById(R.id.dw);tbm.setText(gunit);
        tdgsj=(TextView)findViewById(R.id.dgsj);tdgsj.setText("到达时间："+dgsj);
        tlksj=(TextView)findViewById(R.id.lksj);tlksj.setText("离开时间："+lksj);
        dlxx.setText(dlxxs);
    }

    @Override
    public void onClick(View v) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        Date date=new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (v.getId() == R.id.bczl) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("msg", nmsg);
            bundle.putString("pid", pid);
            bundle.putString("tel", tel);
            intent.putExtras(bundle);
            intent.setClass(SgxxActivity.this,com.mwj.lhn.sgdk.dzqm.photeActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.spps) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("msg", nmsg);
            bundle.putString("kid", kid);
            bundle.putString("kname", kname);

            bundle.putString("pid", pid);
            bundle.putString("tel", tel);
            intent.putExtras(bundle);
            intent.setClass(SgxxActivity.this,com.mwj.lhn.sgdk.spps.PspActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.ddsc) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("msg", nmsg);
            bundle.putString("pid", pid);
            bundle.putString("tel", tel);
            bundle.putString("sms", sms);
            intent.putExtras(bundle);
            intent.setClass(SgxxActivity.this,DDSCActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.fxwz) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("msg", nmsg);
            bundle.putString("tel", tel);
            bundle.putString("pid", pid);
            intent.putExtras(bundle);
            intent.setClass(SgxxActivity.this,WtlbActivity.class);
            startActivity(intent);
        }

        if(v.getId() == R.id.sqxx){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("msg", nmsg);
            bundle.putString("pid", pid);
            bundle.putString("tel", tel);
            bundle.putString("sms", sms);
            intent.putExtras(bundle);
            intent.setClass(SgxxActivity.this,PhotoListActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.btbl) {
            if(tsgzt.getText()!="") {
                if (lcsfbx.equals("N")) {
                    intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("lcseq", lcseq);
                    bundle.putString("pid", pid);
                    bundle.putString("tel", tel+sms);
                    bundle.putString("msg", nmsg);
                    bundle.putString("lcid", lcid);
                    bundle.putString("lcname", lcname);
                    bundle.putString("sgqks", sgqks);
                    intent.putExtras(bundle);
                    intent.setClass(SgxxActivity.this, YbhActivity.class);
                    startActivity(intent);
                }
                if (lcsfbx.equals("Y")) {
                    intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("lcseq", lcseq);
                    bundle.putString("pid", pid);
                    bundle.putString("msg", nmsg);
                    bundle.putString("tel", tel);
                    bundle.putString("sms", sms);
                    bundle.putString("lcid", lcid);
                    bundle.putString("lcname", lcname);
                    bundle.putString("tel", tel);
                    intent.putExtras(bundle);
                    intent.setClass(SgxxActivity.this, DDLKActivity.class);
                    startActivity(intent);
                }






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
    public String null2str(String obj){
        if(obj.equals("null")){
            obj="";
        }
        if(obj==null){
            obj="";
        }
        return obj;
    }


    /**
     * @return
     */

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

}
