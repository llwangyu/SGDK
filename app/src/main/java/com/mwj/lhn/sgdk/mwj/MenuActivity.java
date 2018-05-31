package com.mwj.lhn.sgdk.mwj;



import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.mwj.lhn.sgdk.R;

@TargetApi(Build.VERSION_CODES.ECLAIR)
@SuppressLint("NewApi")
public class MenuActivity extends Activity {
	private GridView grid;
	private DisplayMetrics localDisplayMetrics;
	private View view;
	private TextView mText1;

	private String value = "";

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		view = this.getLayoutInflater().inflate(R.layout.menumain, null);
		showData();

	}



	public void showData() {
		setContentView(view);
		localDisplayMetrics = getResources().getDisplayMetrics();
		grid = (GridView) view.findViewById(R.id.my_grid);
		ListAdapter adapter = new GridAdapter(this);
		grid.setAdapter(adapter);
		grid.setOnItemClickListener(mOnClickListener);
		mText1 = (TextView) findViewById(R.id.syr);	
		mText1.setText("欢迎使用"+value);
	}


	@SuppressLint("NewApi")
	private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
		@TargetApi(Build.VERSION_CODES.ECLAIR)
		@SuppressLint("NewApi")
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			
			if (position == 0) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				intent.setClass(MenuActivity.this, login.class);
				startActivity(intent);
			}
			else if (position == 1) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				intent.setClass(MenuActivity.this, com.android.fisher.sgface.activity.WelcomeActivity.class);
				startActivity(intent);
			}
			else if (position == 2) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				intent.setClass(MenuActivity.this, WebviewActivity.class);
				startActivity(intent);
			}

			
		}
	};

	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}
		public final int getCount() {
			return 9;
		}
		public final Object getItem(int paramInt) {
			return null;
		}
		public final long getItemId(int paramInt) {
			return paramInt;
		}
		public View getView(int paramInt, View paramView,
				ViewGroup paramViewGroup) {
			
			paramView = inflater.inflate(R.layout.activity_label_item, null);
			TextView text = (TextView) paramView
					.findViewById(R.id.activity_name);
			switch (paramInt) {
			case 0: {

					text.setText("现场控制子系统");


				Drawable draw = getResources().getDrawable(
						R.drawable.wbgw1);
				draw.setBounds(0, 0, draw.getIntrinsicWidth(),
						draw.getIntrinsicHeight());
				text.setCompoundDrawables(null, draw, null, null);
				break;
			}

			case 1: {

					text.setText("到岗签名子系统");

				Drawable draw = getResources().getDrawable(
						R.drawable.nbzl1);
				draw.setBounds(0, 0, draw.getIntrinsicWidth(),
						draw.getIntrinsicHeight());
				text.setCompoundDrawables(null, draw, null, null);
				break;
			}

			case 2: {

					text.setText("施工现场视频");

				Drawable draw = getResources().getDrawable(
						R.drawable.dzyj1);
				draw.setBounds(0, 0, draw.getIntrinsicWidth(),
						draw.getIntrinsicHeight());
				text.setCompoundDrawables(null, draw, null, null);
				break;
			}


			}

			paramView
					.setMinimumHeight((int) (36.0F * localDisplayMetrics.density));
//			 paramView.setMinimumHeight((int)(96.0F *
//			 localDisplayMetrics.density));
			paramView
					.setMinimumWidth(((-12 + localDisplayMetrics.widthPixels) / 3));

			return paramView;
		}
	}

	protected void dialog() {
		Builder builder = new Builder(MenuActivity.this);
		builder.setMessage("确定要退出?");
		builder.setTitle("提示");
		builder.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						MenuActivity.this.finish();
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
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { 
	    	dialog();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

}