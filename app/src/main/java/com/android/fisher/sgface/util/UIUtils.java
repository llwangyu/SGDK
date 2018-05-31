package com.android.fisher.sgface.util;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.mwj.lhn.sgdk.R;
import com.android.fisher.sgface.base.MyApplication;


/**
 * Created by Fisher on 2017-11-20.
 */
public class UIUtils {
    public static Context getContext() {
        return MyApplication.getContext();
    }

    public static Thread getMainThread() {
        return MyApplication.getMainThread();
    }

    public static long getMainThreadId() {
        return MyApplication.getMainThreadId();
    }

    /**
     * dip转换px
     */
    public static int dip2px(int dip) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * pxz转换dip
     */
    public static int px2dip(int px) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * 获取主线程的handler
     */
    public static Handler getHandler() {
        return MyApplication.getMainHandler();
    }

    /**
     * 延时在主线程执行runnable
     */
    public static boolean postDelayed(Runnable runnable, long delayMillis) {
        return getHandler().postDelayed(runnable, delayMillis);
    }

    /**
     * 在主线程执行runnable
     */
    public static boolean post(Runnable runnable) {
        return getHandler().post(runnable);
    }

    /**
     * 从主线程looper里面移除runnable
     */
    public static void removeCallbacks(Runnable runnable) {
        getHandler().removeCallbacks(runnable);
    }

    public static View inflate(int resId) {
        return LayoutInflater.from(getContext()).inflate(resId, null);
    }

    /**
     * 获取资源
     */
    public static Resources getResources() {
        return getContext().getResources();
    }

    /**
     * 获取文字
     */
    public static String getString(int resId) {
        return getResources().getString(resId);
    }

    /**
     * 获取文字数组
     */
    public static String[] getStringArray(int resId) {
        return getResources().getStringArray(resId);
    }

    /**
     * 获取dimen
     */
    public static int getDimens(int resId) {
        return getResources().getDimensionPixelSize(resId);
    }

    /**
     * 获取drawable
     */
    @SuppressWarnings("deprecation")
    public static Drawable getDrawable(int resId) {
        return getResources().getDrawable(resId);
    }

    /**
     * 获取颜色
     */
    public static int getColor(int resId) {
        return getResources().getColor(resId);
    }

    /**
     * 获取颜色选择器
     */
    public static ColorStateList getColorStateList(int resId) {
        return getResources().getColorStateList(resId);
    }


    /**
     * 判断当前的线程是不是在主线程
     *
     * @return
     */
    public static boolean isRunInMainThread() {
        return android.os.Process.myTid() == getMainThreadId();
    }

    public static void runInMainThread(Runnable runnable) {
        if (isRunInMainThread()) {
            runnable.run();
        } else {
            post(runnable);
        }
    }

    /**
     * 对toast的简易封装。线程安全，可以在非UI线程调用。
     */
    public static void showToastSafe(final int resId) {
        showToastSafe(getString(resId));
    }

    /**
     * 对toast的简易封装。线程安全，可以在非UI线程调用。
     */
    public static void showToastSafe(final String str) {
        if (isRunInMainThread()) {
            showToast(str);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    showToast(str);
                }
            });
        }
    }

    private static Toast mToast;

    private static void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(UIUtils.getContext(), text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    /**
     * 得到屏幕的高度
     *
     * @param activity
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getSreenHeight(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getHeight();
    }

    /**
     * 得到屏幕的宽度
     *
     * @param activity
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getSreenWidth(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getWidth();
    }

    /**
     * 得到一个控件相对于屏幕左侧的位置
     *
     * @param view
     * @return
     */
    public static int getLeftOnScreen(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return location[0];

    }

    /**
     * 得到一个控件相对于屏幕左侧的位置
     *
     * @param view
     * @return
     */
    public static int getRightOnScreen(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return location[0];

    }

    /**
     * 得到一个控件相对于屏幕顶部的位置
     *
     * @param view
     * @return
     */
    public static int getTopOnScreen(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return location[1];

    }

    /**
     * 获得状态栏的高度
     *
     * @return
     */
    public static int getStatusHeight() {

        int statusHeight = -1;
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 设置沉浸式状态栏，以一个高度为0的View为基础(在每个Activity的setContentView之后添加效果为佳)
     *
     * @param activity 当前的Activity
     * @param view     高度为0的view
     * @param type     根布局的类型，线性布局为1，相对布局为2
     */
    public static void setStatusColor(Activity activity, View view, int type, int i) {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                activity.getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                activity.getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                if (1 == type) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UIUtils.getStatusHeight());
                    view.setLayoutParams(params);
                } else if (2 == type) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, UIUtils.getStatusHeight());
                    view.setLayoutParams(params);

                }
                view.setBackgroundColor(getColor(R.color.colorPrimary));
            } else {
                view.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            view.setVisibility(View.GONE);
            e.printStackTrace();
            Log.e("MainActivity", "沉浸式状态栏设置出错");
        }
    }

    /**
     * 实现文本复制功能
     * 注意：导包的时候
     * API 11之前： android.text.ClipboardManager
     * API 11之后： android.content.ClipboardManager
     *
     * @param content
     */
    public static void copy(String content) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }

    /**
     * 实现粘贴功能
     *
     * @return
     */
    public static String paste() {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        return cmb.getText().toString().trim();
    }

    public static void setHeightByWidth(View view, float ratio) {
        if (ratio == 0) {
            throw new IllegalArgumentException("比例不能为零!");
        } else {
            view.getLayoutParams().height = (int) (view.getLayoutParams().width / ratio);
        }
    }
}