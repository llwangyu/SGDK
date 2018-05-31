package com.android.fisher.sgface.util;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.UUID;

public final class ToolUtil {


	// 日志Tag
	private static String TAG = "android.com.sggl";
	// 日志打印开关
	private static boolean print = true;

	public static void JumpActivity(Context context, Class<?> cls){
		Intent intent = null;
		intent = new Intent(context, cls);// 实例化Intent信使
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);// 设置跳转标志为如此Activity存在则把其从任务堆栈中取出放到最上方
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 在跳转之前情况当前任务堆栈中此Activity的顶部任务
		context.startActivity(intent);// 开始跳转
	}

	public static void print(String msg) {
		if (print) {
			Log.d(TAG, msg);
		}
	}

	public static void print(CharSequence msg) {
		if (msg != null) {
			print(msg.toString());
		}
	}

	public static void print(String tag, String msg) {
		print(msg);
		if (print) {
			Log.i(tag, msg);
		}
	}

	/**
	 * 发送Toast
	 *
	 * @param mContext
	 * @param text
	 */
	public static void sendToast(Context mContext, String text) {
		Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
	}

	/**
	 * 发送Toast
	 *
	 * @param /mContext
	 * @param /text
	 */
	public static void sendToast(Context context,int StringResId) {
		sendToast(context, context
				.getString(StringResId));
	}

	/**
	 * 发送Toast
	 *
	 * @param mContext
	 * @param text
	 */
	public static void sendToast(Context mContext, CharSequence text) {
		Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
	}

	public static void copy(String content, Context context) {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(content.trim());

	}
	public static void hiddenInputMethod(Activity activity,
										 final EditText editView) {
		if (android.os.Build.VERSION.SDK_INT <= 10) {
			editView.setInputType(InputType.TYPE_NULL);
		} else {
			activity.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			try {
				Class<EditText> cls = EditText.class;
				Method setShowSoftInputOnFocus;
				setShowSoftInputOnFocus = cls.getMethod(
						"setShowSoftInputOnFocus", boolean.class);
				setShowSoftInputOnFocus.setAccessible(true);
				setShowSoftInputOnFocus.invoke(editView, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
//	生成32为的uuid并去掉-
	public static String getUUID(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
//	转换字符串空值
	public static String trimNull(String input){
		if(input==null||input.trim().equals("")||input.trim().equals("undefined")){
			return "";
		}else{
			return input;
		}
	}
//	字符串转int
	public static int str2Int(String input){
		if(input==null||input.trim().equals("")){
			return 0;
		}else{
			int rs = 0;
			try{
				rs = Integer.parseInt(input);
			}catch(Exception e){
				e.printStackTrace();
			}
			return rs;
		}
	}

}
