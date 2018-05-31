package com.mwj.lhn.sgdk.pub;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;



import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mwj.lhn.sgdk.R;
import com.mwj.lhn.sgdk.mwj.DDLKActivity;
import com.mwj.lhn.sgdk.mwj.FormActivity;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.mwj.lhn.sgdk.pub.Basic.jsonlogin;
import static com.mwj.lhn.sgdk.pub.Basic.updateurl;


/**
 * Created by LHN on 2017/11/29.
 */

public class UpdateManager {
    private static final int DOWNLOAD = 1;
	/* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;
	/* 保存解析的XML信息 */
    HashMap<String, String> mHashMap;
    private String mSavePath;
    private JSONObject myjson;
    private String upurl,upname;
    private int progress;
	/* 是否取消更新 */
    private static final int MESSAGETYPE_01 = 0x0001;
    private boolean cancelUpdate = false;
    private static final String TAG ="22222" ;
    public static ProgressDialog progressDialog = null;
    public static boolean STOP = true;
    private Context mContext;
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case DOWNLOAD:
                    mProgress.setProgress(progress);
                    break;
                case DOWNLOAD_FINISH:
                    installApk();
                    break;
                default:
                    break;
            }
        };
    };

	public UpdateManager(Context context)
    {
        this.mContext = context;
    }

    /**
     */
    public void checkUpdate()
    {
        if (isUpdate())
        {
            showNoticeDialog();
        }
    }

    private boolean isUpdate()
    {
        final boolean[] uflag = {false};
        // 获取当前软件版本
        final int versionCode = getVersionCode(mContext);
        Log.i("-本地版本--", ""+versionCode);
        String path =updateurl;
        // 执行get方法
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(path, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                         String msg=new String(bytes);
                try {
                    JSONObject jsonObject = jsonlogin(msg);
                    myjson=jsonObject;
                    upurl=jsonObject.getString("url");
                    upname=jsonObject.getString("name");
                    Log.i("--网络版本-------", ""+Integer.parseInt(jsonObject.getString("version")));
                     if(Integer.parseInt(jsonObject.getString("version"))>versionCode)
                     {
                         showNoticeDialog();
                     }
                     else{
                         shownoup();
                     }
                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
         return uflag[0];
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
    /**
     *
     * @param context
     * @return
     */
    private int getVersionCode(Context context)
    {
        int versionCode = 0;

        try
        {
            versionCode = context.getPackageManager().getPackageInfo("com.mwj.lhn.sgdk", 0).versionCode;

        } catch (NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return versionCode;
    }


    private void showNoticeDialog()
    {
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle(R.string.soft_update_title);
        builder.setMessage(R.string.soft_update_info);
        // 更新
        builder.setPositiveButton(R.string.soft_update_updatebtn, new OnClickListener()
        {

            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                showDownloadDialog();
            }
        });
        // 稍后更新
        builder.setNegativeButton(R.string.soft_update_later, new OnClickListener()
        {

            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }
    private void shownoup()
    {
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle("版本检测");
        builder.setMessage("无新版本更新！");
        // 更新
        builder.setPositiveButton("确认", new OnClickListener()
        {

            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }
    /**
     * 显示软件下载对话�?
     */
    private void showDownloadDialog()
    {
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle(R.string.soft_updating);
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.softupdate_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        builder.setView(v);
        builder.setNegativeButton(R.string.soft_update_cancel, new OnClickListener()
        {

            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                cancelUpdate = true;
            }
        });
        mDownloadDialog = builder.create();
        mDownloadDialog.show();
        downloadApk();
    }

    /**
     * 下载apk文件
     */
    private void downloadApk()
    {
        new downloadApkThread().start();
    }

    /**
     * 下载文件线程
     *
     * @author coolszy
     *@date 2012-4-26
     *@blog http://blog.92coding.com
     */
    private class downloadApkThread extends Thread
    {

        public void run()
        {
            try
            {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                {
                    // 获得存储卡的路径
                    String sdpath = Environment.getExternalStorageDirectory() + "/";
                    mSavePath = sdpath + "download";
                    URL url = new URL(upurl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();

                    File file = new File(mSavePath);
                    if (!file.exists())
                    {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, upname);
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    // 缓存
                    byte buf[] = new byte[1024];
                    do
                    {
                        int numread = is.read(buf);
                        count += numread;
                        progress = (int) (((float) count / length) * 100);
                        mHandler.sendEmptyMessage(DOWNLOAD);
                        if (numread <= 0)
                        {
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            mDownloadDialog.dismiss();
        }
    };

    /**
     */
    private void installApk()
    {
        File apkfile = new File(mSavePath, upname);
        if (!apkfile.exists())
        {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(mContext, "com.mwj.lhn.sgdk.fileprovider", apkfile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(apkfile);
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        mContext.startActivity(intent);

    }
}
