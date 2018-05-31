package com.mwj.lhn.sgdk.pub;




import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mwj.lhn.sgdk.mwj.DDLKActivity;
import com.mwj.lhn.sgdk.mwj.FormActivity;
import com.mwj.lhn.sgdk.mwj.SgxxActivity;
import com.mwj.lhn.sgdk.mwj.YbhActivity;
import com.mwj.lhn.sgdk.mwj.login;

import cz.msebera.android.httpclient.Header;

public class Basic extends Activity {
	private static final int MESSAGETYPE_01 = 0x0001;
	private static final String TAG ="22222" ;
	public static ProgressDialog progressDialog = null;
	public static boolean STOP = true;
	public static ProgressDialog mpDialog;
	public static String updateurl="http://61.178.243.175:7001/mApp/version.jsp";
	public static String myurl_host="http://211.98.121.169:57099/SgjhApi/api/";
//  public static String movieurl_host="http://192.168.1.108:8080/SgjhApi/api/";
	public static String weburl="http://211.98.121.169:57099/SgVideo.jsp";
//	public static String myurl_host="https://frontier.lanzh.95306.cn/gateway/hydzsw/SgjhApi/api/";

	public String null2str(Object obj){
		return obj!=null ?obj.toString():"0";
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
		progressDialog = ProgressDialog.show(lg, "请稍候", "正在与服务器同步！！");
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

	public static boolean reflash=false;
	public void nettrue() {
		ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			Toast.makeText(getApplicationContext(), "网络状态畅通,正在与服务器同步！",
					Toast.LENGTH_LONG).show();

		} else {
			Toast.makeText(getApplicationContext(), "当前无网络可用,正在本地上传",
					Toast.LENGTH_LONG).show();

		}
	}
	public static long getFileSize(File file) throws Exception {
		if (file == null) {
			return 0;
		}
		long size = 0;
		if (file.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(file);
			size = fis.available();
		}
		return size;
	}
	public static JSONObject getloginname(String tel, Activity lg) {

		   JSONObject name ;
			showProgress(lg);
			name = aclogin(tel);
			closeProgress();
           System.out.println(name);
		return name;

	}
	public static InputStream getversion() {
		AsyncHttpClient client = new AsyncHttpClient();
		String url = "http://61.178.243.175:7001/mApp/version.xml";

		HttpGet httpGet = new HttpGet(url);
		HttpResponse response;
		InputStream stream = null;
		try {
			response = new DefaultHttpClient().execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				stream = entity.getContent();

			}

		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}
		return stream;
	}
	public static String getversion1() {
        final String[] inStream = {""};
        AsyncHttpClient client = new AsyncHttpClient();
        String path ="http://61.178.243.175:7001/mApp/version.jsp";
        // 执行get方法

        client.post(path, new AsyncHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
                try {
                    inStream[0] =new String(responseBody,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                try {
                    inStream[0] =new String(bytes,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        return inStream[0];

    }
	public static JSONObject jsonlogin(String result) {


		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;

	}
	public static JSONObject forjson(String name) {


		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(name);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;

	}


	public static JSONObject  aclogin(String tel ){
		final JSONObject[] msg = {new JSONObject()};
	   RequestParams params = new RequestParams();// 绑定参数
		long time = System.currentTimeMillis();
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String dtime=format.format(date);
	  params.put("tel_number", tel);
	  params.put("plan_date",dtime);
//到岗
	    URLManage.showInfos("findDayPlans.json",params, new JsonHttpResponseHandler(){
		  @Override
		  public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject  responseBody) {
			  // TODO Auto-generated method stub
			  super.onSuccess(statusCode, headers, responseBody);
			  msg[0] =responseBody;
		  }
		  @Override
		  public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable e, JSONObject errorResponse) {
			  super.onFailure(statusCode, headers, e, errorResponse);
			  msg[0] =errorResponse;
		  }
	  });
	  return msg[0];
  }




	public static String streamToString(InputStream is) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			baos.close();
			is.close();
			byte[] byteArray = baos.toByteArray();
			return new String(byteArray);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return null;
		}
	}
	public static String InputStreamtoString(InputStream is) throws Exception {

		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = in.readLine()) != null) {
			buffer.append(line);
		}
		return buffer.toString();
	}

	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"),// 防止模拟器上的乱码
					512 * 1024);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			Log.e("8888", e.getLocalizedMessage(),
					e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}




	public static void dialog(Activity lg) {
		AlertDialog.Builder builder = new Builder(lg);
		builder.setMessage("确定要退出吗?");
		builder.setTitle("提示");
		builder.setPositiveButton("确认",
				new android.content.DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						android.os.Process.killProcess(android.os.Process
								.myPid());
						System.exit(0);
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

	public static String[] splitStr(String srcStr, String spChar,
									int maxNumSplit) {
		if (srcStr == null) {// 若是空串，则返回null
			return null;
		}
		if (spChar == null) {// 如果未指定分隔符号，则返回源串
			return new String[] { srcStr };
		} else {
			final int SIZE = 10;
			int index = 0;
			String[] result = new String[SIZE];
			int delimLen = spChar.length();
			int nextIndex = 0;
			int foundIndex = -1;
			int resultArraySize = result.length;

			while ((foundIndex = srcStr.indexOf(spChar, nextIndex)) != -1) {
				if (index == resultArraySize) {// 预分配空间不够，则增加空间
					String[] temp = result;
					result = new String[result.length + SIZE];
					System.arraycopy(temp, 0, result, 0, temp.length);
					resultArraySize = result.length;
				}
				result[index++] = srcStr.substring(nextIndex, foundIndex);
				nextIndex = foundIndex + delimLen;
				if (maxNumSplit > 0 && index >= maxNumSplit) {
					break;
				}
			}

			if (result.length >= index) {
				String[] temp = result;
				result = new String[index + 1];
				System.arraycopy(temp, 0, result, 0, index);
			}

			result[index++] = srcStr.substring(nextIndex);

			return result;
		}
	}

	public static String unzip(byte[] values) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(values);
		ZipInputStream zis = new ZipInputStream(bis);
		zis.getNextEntry();
		ObjectInputStream ois = new ObjectInputStream(zis);
		Object obj = ois.readObject();
		ois.close();
		zis.close();
		bis.close();
		return new String(hexstr2bytes(obj.toString()));
	}

	public static byte[] hexstr2bytes(String hexstr) {
		if (hexstr != null) {
			int len = hexstr.length() / 2;
			byte[] result = new byte[len];
			char[] array = hexstr.toCharArray();
			for (int i = 0; i < len; i++) {
				int pos = i * 2;
				result[i] = (byte) (toByte(array[pos]) << 4 | toByte(array[pos + 1]));
			}
			return result;
		}
		return null;
	}

	public static byte toByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}


}