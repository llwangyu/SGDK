package com.mwj.lhn.sgdk.pub;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by 13893 on 2017/12/6.
 */

public class MwjDialog {
    public static String OneDialog(Context lg, final String[] strs){
        final String[] msg = {""};
        AlertDialog.Builder builder = new AlertDialog.Builder(lg);// 自定义对话框
        builder.setSingleChoiceItems(strs, 0, new DialogInterface.OnClickListener() {// 2默认的选中

            @Override
            public void onClick(DialogInterface dialog, int which) {// which是被选中的位置
                msg[0] =(strs[which]);
                dialog.dismiss();//随便点击一个item消失对话框，不用点击确认取消
            }
        });
        builder.show();
        return msg[0];
    }
}
