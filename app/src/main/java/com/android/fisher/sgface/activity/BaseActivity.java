package com.android.fisher.sgface.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.fisher.sgface.util.StringUtils;
import com.android.fisher.sgface.util.ToastUtil;


public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    private ProgressDialog progressDialog;

    /**
     * activity跳转工具类，负责Activity的基本跳转时间
     * @param
     * @param cls
     */
    public void JumpToActivity(Class<?> cls) {
        Intent intent = null;
        intent = new Intent(this, cls);// 实例化Intent信使
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);// 设置跳转标志为如此Activity存在则把其从任务堆栈中取出放到最上方
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 在跳转之前情况当前任务堆栈中此Activity的顶部任务
        this.startActivity(intent);// 开始跳转
    }

    public void JumpToActivity(Class<?> cls, Bundle bundle) {
        Intent intent = null;
        intent = new Intent(this, cls);// 实例化Intent信使
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);// 设置跳转标志为如此Activity存在则把其从任务堆栈中取出放到最上方
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 在跳转之前情况当前任务堆栈中此Activity的顶部任务
        if (bundle != null)
            intent.putExtras(bundle);
        this.startActivity(intent);// 开始跳转
    }

    public void showToast(String text) {
        ToastUtil.showToast(getApplicationContext(), text);
    }

    protected void showProgressBar(String str) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setTitle(str);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }
    protected void hideProgressBar() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }

    // 打印Log方法
    public void LogE(String msg) {
        if (!StringUtils.isEmpty(msg)) {
            Log.e(TAG, msg);
        }
    }
    public void showUpdataDialog(String title,String info) {
        AlertDialog.Builder builer = new AlertDialog.Builder(this);
        builer.setTitle(title);//"会议签到"
        builer.setMessage(info);//"您的会议已签到，系统规定不能重复签到，如有疑问请联系管理员。"
        builer.setPositiveButton("确定", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        /*builer.setNegativeButton("取消", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });*/
        AlertDialog dialog = builer.create();
        dialog.show();
    }

    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("是否放弃未完成编辑？");
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
        builder.create().show();
    }

    /*
   获取当前版本号：
    */
    public int getVersionCode() {
        try{
            PackageManager packageManager = getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
            return packInfo.versionCode;
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }
    /*
   获取当前版本名称：
    */
    public String getVersionName()  {
        try{
            PackageManager packageManager = getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
            return packInfo.versionName;
        }catch(Exception e){
            e.printStackTrace();
            return "未知的版本名称";
        }
    }
}

