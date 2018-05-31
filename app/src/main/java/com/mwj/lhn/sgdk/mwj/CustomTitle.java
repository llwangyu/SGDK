package com.mwj.lhn.sgdk.mwj;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.mwj.lhn.sgdk.R;
import com.mwj.lhn.sgdk.pub.UpdateManager;

/**
 * Created by LHN on 2017/11/30.
 */

public class CustomTitle {
    private static Activity mActivity;

    public static void getCustomTitle(Activity activity, String title) {
        mActivity = activity;
        mActivity.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        mActivity.setContentView(R.layout.custom_title);
        mActivity.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.custom_title);

        TextView textView = (TextView) activity.findViewById(R.id.head_center_text);
        textView.setText(title);
        Button titleBackBtn = (Button) activity.findViewById(R.id.TitleBackBtn);
        titleBackBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBack();
               // mActivity.finish();
            }
        });
        Button titleinfoBtn = (Button) activity.findViewById(R.id.TitleInfoBtn);
        titleinfoBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog(mActivity);
            }
        });
    }
    public static void dialog(Activity lg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(lg);
        builder.setMessage("检查更新吗?");
        builder.setTitle("提示");
        builder.setPositiveButton("确认",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        UpdateManager manager = new UpdateManager(mActivity);
                        manager.checkUpdate();
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
    public static void onBack(){
        new Thread(){
            public void run() {
                try{
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                }
                catch (Exception e) {
                    Log.e("Exception when onBack", e.toString());
                }
            }
        }.start();
    }
}
