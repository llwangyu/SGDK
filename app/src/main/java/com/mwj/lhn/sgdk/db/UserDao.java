package com.mwj.lhn.sgdk.db;

/**
 * Created by LHN on 2018/3/26.
 */
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mwj.lhn.sgdk.mwj.DDLKActivity;
import com.mwj.lhn.sgdk.mwj.DDSCActivity;
import com.mwj.lhn.sgdk.pub.Basic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.mwj.lhn.sgdk.pub.Basic.jsonlogin;

public class UserDao {

    public int getrk(SQLiteDatabase db1,String tel) {
        int count=0;
        Cursor cursor = db1.query("sgxx", new String[]{"count(tel)"}, "tel=?", new String[]{tel}, null, null, null, null);
        //利用游标遍历所有数据对象
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        return count;
    }
    public int getimgc(SQLiteDatabase db1,String tel) {
        int count=0;
        Cursor cursor = db1.query("sgxx_img", new String[]{"count(tel)"}, "tel=? and flag='未上传'", new String[]{tel}, null, null, null, null);
        //利用游标遍历所有数据对象
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        return count;
    }
    public void insSg(SQLiteDatabase db,String tel,String rmsg,String qblc,String nlc){
        ContentValues cv = new ContentValues();
        cv.put("tel",tel);//
        cv.put("sgxx",rmsg); //
        cv.put("qblc",qblc); //添加密码
        cv.put("xylc",nlc); //添加密码
        db.insert("sgxx",null,cv);//执行插入操作
    }
    public void insSgimg(SQLiteDatabase db,String tel,ContentValues cv ){
        cv.put("tel",tel);//
        db.insert("sgxx_img",null,cv);//执行插入操作
    }

    public String getsgxx(SQLiteDatabase db1,String tel) {
        String msg="";
        Cursor cursor = db1.query("sgxx", new String[]{"*"}, "tel=?", new String[]{tel}, null, null, null, null);
        //利用游标遍历所有数据对象
        if(cursor.moveToFirst()) {
            msg= cursor.getString(cursor.getColumnIndex("sgxx"));
        }
        return msg;
    }
    public String getlc(SQLiteDatabase db1,String tel) {
        String  msg1="";
        Cursor cursor = db1.query("sgxx", new String[]{"*"}, "tel=?", new String[]{tel}, null, null, null, null);
        //利用游标遍历所有数据对象
        if(cursor.moveToFirst()) {
            msg1= cursor.getString(cursor.getColumnIndex("xylc"));
        }
        return msg1;

    }
    public String getdgsj(SQLiteDatabase db1,String tel) {
        String  msg1="";
        Cursor cursor = db1.query("sgxx", new String[]{"*"}, "tel=?", new String[]{tel}, null, null, null, null);
        //利用游标遍历所有数据对象
        if(cursor.moveToFirst()) {
            msg1= cursor.getString(cursor.getColumnIndex("dgsj"));
        }
        return msg1;

    }
    public String getlgsj(SQLiteDatabase db1,String tel) {
        String  msg1="";
        Cursor cursor = db1.query("sgxx", new String[]{"*"}, "tel=?", new String[]{tel}, null, null, null, null);
        //利用游标遍历所有数据对象
        if(cursor.moveToFirst()) {
            msg1= cursor.getString(cursor.getColumnIndex("lgsj"));
        }
        return msg1;

    }
    public void updglg(SQLiteDatabase db1,String tel) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String cjdate=df.format(new Date());
        String dgsj = "";
        String lgsj="";
        Cursor cursor = db1.query("sgxx", new String[]{"*"}, "tel=?", new String[]{tel}, null, null, null, null);
        //利用游标遍历所有数据对象
        if(cursor.moveToFirst()) {
            dgsj= cursor.getString(cursor.getColumnIndex("dgsj"));
        }
        if(dgsj!=null){
            ContentValues values2 = new ContentValues();
            values2.put("lgsj", cjdate);
            db1.update("sgxx", values2, "tel=?", new String[]{tel});
        }
        else {
            ContentValues values2 = new ContentValues();
            values2.put("dgsj", cjdate);
            db1.update("sgxx", values2, "tel=?", new String[]{tel});
        }
    }
    public void uplc(SQLiteDatabase db1,String tel,String lcid) {
        int ilcid=Integer.parseInt(lcid)+1;
        String  msg1="";
        Cursor cursor = db1.query("sgxx", new String[]{"*"}, "tel=?", new String[]{tel}, null, null, null, null);
        //利用游标遍历所有数据对象
        if(cursor.moveToFirst()) {
            msg1= cursor.getString(cursor.getColumnIndex("qblc"));
        }
        if(!msg1.equals("")) {
            try {
                JSONArray arr1 = new JSONArray(msg1);
                List<Integer> nums = new ArrayList<Integer>();
                for (int m = 0; m < arr1.length(); m++) {
                    JSONObject jsonObject2 = arr1.getJSONObject(m);
                    nums.add(jsonObject2.getInt("id"));
                    if (jsonObject2.getInt("id")==ilcid) {
                        ContentValues values2 = new ContentValues();
                        values2.put("xylc", jsonObject2.toString());
                        db1.update("sgxx", values2, "tel=?", new String[]{tel});
                    }
                }
                int Max = Collections.max(nums);
                if(ilcid>Max){
                    ContentValues values2 = new ContentValues();
                    values2.put("xylc", "");
                    db1.update("sgxx", values2, "tel=?", new String[]{tel});
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
