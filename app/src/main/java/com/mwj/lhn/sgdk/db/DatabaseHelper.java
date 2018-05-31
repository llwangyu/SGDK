package com.mwj.lhn.sgdk.db;

/**
 * Created by LHN on 2018/3/7.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bumptech.glide.DrawableRequestBuilder;

public class DatabaseHelper extends SQLiteOpenHelper{
    private static final int VERSION = 1;
    private static final String SWORD="SWORD";
    //三个不同参数的构造函数
    //带全部参数的构造函数，此构造函数必不可少
    public DatabaseHelper(Context context, String name, CursorFactory factory,
                          int version) {
        super(context, name, factory, version);

    }
    //带两个参数的构造函数，调用的其实是带三个参数的构造函数
    public DatabaseHelper(Context context,String name){
        this(context,name,VERSION);
    }
    //带三个参数的构造函数，调用的是带所有参数的构造函数
    public DatabaseHelper(Context context,String name,int version){
        this(context, name,null,version);
    }
    //创建数据库
    public void onCreate(SQLiteDatabase db) {
        Log.i(SWORD,"create a Database");
        //创建数据库sql语句
        String sql = "create table sgxx(_id integer primary key autoincrement,tel varchar(20),sgxx text,qblc text,xylc text,dgsj varchar(20),lgsj varchar(20))";
        String sqlwj = "create table sgxx_img(_id integer primary key autoincrement,tel varchar(20),zyid varchar(20),zysfbc varchar(20),moreid varchar(200),sdurl varchar(200),title varchar(200),imageurl varchar(200),flag varchar(20))";
        //执行创建数据库操作
        db.execSQL(sql);
        db.execSQL(sqlwj);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //创建成功，日志输出提示
        Log.i(SWORD,"update a Database");
    }

}
