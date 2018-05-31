package com.mwj.lhn.sgdk.mwj;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mwj.lhn.sgdk.R;
import com.mwj.lhn.sgdk.db.DatabaseHelper;
import com.mwj.lhn.sgdk.db.UserDao;
import com.mwj.lhn.sgdk.pub.Basic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DDSCActivity extends AppCompatActivity {
    private String msg,tel,sms;
    private ListView listView = null;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private SimpleAdapter simpleAdapter = null;
    private static final int MESSAGETYPE_01 = 0x0001;
    private static final String TAG ="22222" ;
    public static ProgressDialog progressDialog = null;
    public static boolean STOP = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ddsc);
        Bundle bundle = this.getIntent().getExtras();
        msg = bundle.getString("msg");
        tel = bundle.getString("tel");
        sms = bundle.getString("sms");
        listView = (ListView) findViewById(R.id.listview_sc);

        simpleAdapter = new SimpleAdapter(this, getdata(), R.layout.desc_list,
                new String[] { "itemTitle","itemTextsc" }, new int[] { R.id.itemTitlesc,R.id.itemTextsc });
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                String text1 = (String) ((TextView)arg1.findViewById(R.id.itemTextsc)).getText();
                nextdo(text1,arg2);
            }
        });
}


    private List<Map<String, Object>> getdata() {
        DatabaseHelper dbHelper1 = new DatabaseHelper(DDSCActivity.this, "MY.db");
        SQLiteDatabase db = dbHelper1.getReadableDatabase();
        Cursor cursor = db.query("sgxx_img", new String[]{"zyid,title,sdurl,flag"}, "tel=? and flag='未上传'", new String[]{tel+sms}, null, null, null, null);
        list.clear();
        while (cursor.moveToNext()) {
            String zyid = cursor.getString(0);
            String title = cursor.getString(1);
            String sdurl = cursor.getString(2);
            String flag = cursor.getString(3);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("itemTitle", zyid+"-"+title+"  "+flag); // 获取name
            map.put("itemTextsc", sdurl);
            list.add(map);

        }

        cursor.close();
        db.close();
        return list;
    }
    public void nextdo(final String title, final int arg2) {
        ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        AlertDialog.Builder builder = new AlertDialog.Builder(DDSCActivity.this);
        builder.setTitle("提示");
        builder.setPositiveButton("确定上传离线图片？",
                new android.content.DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        try {
                            if (networkInfo != null) {
                                gotoinfo(title,arg2);
                            }
                            else{
                                dialog("网络链接异常，请检查网络！");
                            }
                        } catch (Exception e) {
                            System.out.println("time out");
                            e.printStackTrace();
                        } finally {
                        }

                    }
                });
        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();

    }
    public void gotoinfo(final String sdurl, final int arg2) {
        DatabaseHelper dbHelper1 = new DatabaseHelper(DDSCActivity.this, "MY.db");
        SQLiteDatabase db2 = dbHelper1.getReadableDatabase();

        String zyid,zysfbc,moreid,sdurl1,title,imageurl="";
        Cursor cursor = db2.query("sgxx_img", new String[]{"*"}, "sdurl=?", new String[]{sdurl}, null, null, null, null);
        //利用游标遍历所有数据对象
        if(cursor.moveToFirst()) {
            zyid= cursor.getString(cursor.getColumnIndex("zyid"));
            zysfbc= cursor.getString(cursor.getColumnIndex("zysfbc"));
            moreid= cursor.getString(cursor.getColumnIndex("moreid"));
            sdurl1= cursor.getString(cursor.getColumnIndex("sdurl"));
            System.out.println(sdurl1);
            title= cursor.getString(cursor.getColumnIndex("title"));
            imageurl= cursor.getString(cursor.getColumnIndex("imageurl"));
            {
                try {
                    AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
                    client.setTimeout(30000);
                    String uri = sdurl1;
                    RequestParams params = new RequestParams();
                    final File bfile = new File(uri);
                    params.put("sdurl", uri);
                    params.put("file", bfile);
                    params.put("imageurl", imageurl);
                    params.put("moreid", moreid);
                    params.put("qmwj", imageurl);
                    params.put("zyid", zyid);
                    params.put("title", title);
                    params.put("zysfbc", zysfbc);

                    showProgress(DDSCActivity.this);
                    client.post(Basic.myurl_host + "myUpload", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                            closeProgress();
                            Toast.makeText(DDSCActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                            ContentValues cvalues2 = new ContentValues();
                            cvalues2.put("flag", "已上传");
                            DatabaseHelper dbHelper2 = new DatabaseHelper(DDSCActivity.this, "MY.db");
                            SQLiteDatabase db3 = dbHelper2.getReadableDatabase();
                            db3.update("sgxx_img", cvalues2, "sdurl=?", new String[]{sdurl});
                            db3.close();
                             list.remove(arg2);
                            simpleAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                            closeProgress();
                            Toast.makeText(DDSCActivity.this, "上传失败", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        db2.close();
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
    public  void dialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DDSCActivity.this);
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
