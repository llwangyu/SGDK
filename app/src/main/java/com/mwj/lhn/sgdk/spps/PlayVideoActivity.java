package com.mwj.lhn.sgdk.spps;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.icu.lang.UProperty;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mwj.lhn.sgdk.R;
import com.mwj.lhn.sgdk.db.DatabaseHelper;
import com.mwj.lhn.sgdk.db.UserDao;
import com.mwj.lhn.sgdk.mwj.DDLKActivity;
import com.mwj.lhn.sgdk.mwj.PhotoListActivity;
import com.mwj.lhn.sgdk.pub.Basic;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.mwj.lhn.sgdk.pub.Basic.closeProgress;
import static com.mwj.lhn.sgdk.pub.Basic.jsonlogin;
import static com.mwj.lhn.sgdk.pub.Basic.showProgress;
import static com.mwj.lhn.sgdk.pub.URLManage.HOST;

public class PlayVideoActivity extends Activity {

    private MyVideoView videoView;
    private String path,kid,kname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        videoView = (MyVideoView) findViewById(R.id.video);
        Button playBtn = (Button) findViewById(R.id.play_video);
        Button savBtn = (Button) findViewById(R.id.save_video);
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        kid = intent.getStringExtra("kid");
        kname = intent.getStringExtra("kname");
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });
        savBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog111();
            }
        });
    }
    private void play() {
        Uri uri = Uri.parse(path);
        videoView.setVideoURI(uri);
        MediaController mMediaController = new MediaController(this);
        videoView.setMediaController(mMediaController);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });
    }
    protected void dialog111() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PlayVideoActivity.this);
        builder.setMessage("确认上传视频文件?");
        builder.setTitle("提示");
        builder.setPositiveButton("确认",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        uploadmovie();
                    }
                });
        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void uploadmovie() {
        try {
            AsyncHttpClient client = new AsyncHttpClient(false, 80, 443);
            client.setTimeout(30000);
            String uri = path;
            showProgress(PlayVideoActivity.this);
            RequestParams params = new RequestParams();
            long time = System.currentTimeMillis();
            Date date = new Date(time);
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            String dtime = format.format(date);

            final File bfile = new File(uri);
            params.put("sdurl", uri);
            params.put("file", bfile);
            String upurl=Basic.myurl_host + "uploadVideo";
            System.out.println(upurl);
            client.post(upurl, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {

                    Toast.makeText(PlayVideoActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                    finish();
                    try {
                        String msg = new String(bytes,"UTF-8");
                        JSONObject jsonObject = jsonlogin(msg);
                        String result = jsonObject.getString("result");
                        String rsEntity=jsonObject.getString("rsEntity");
                        if(result.equals("1")){

                            RequestParams params = new RequestParams();// 绑定参数
                           params.put("planid", kid);
                            params.put("lrr", kname);
                            params.put("videourl", rsEntity);
                            AsyncHttpClient client1 = new AsyncHttpClient(false,80,443);
                            client1.get(HOST +"insertDayPlanVideos.json", params, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject responseBody) {
                                    // TODO Auto-generated method stub
                                    super.onSuccess(statusCode, headers, responseBody);
                                    String msg = responseBody.toString();
                                    System.out.println(kid+"---"+msg);
                                }
                                @Override
                                public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable e, JSONObject errorResponse) {
                                    super.onFailure(statusCode, headers, e, errorResponse);
                                    String msg = errorResponse.toString();
                                    System.out.println(kid+"---"+msg);

                                }
                            });

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "上传失败，请稍候再试", Toast.LENGTH_SHORT).show();
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                    closeProgress();
                    finish();
                }

                @Override
                public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                    Toast.makeText(PlayVideoActivity.this, "上传失败", Toast.LENGTH_LONG).show();
                    try {
                        String msg = new String(bytes, "UTF-8");
                        System.out.println("---"+msg);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    closeProgress();
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
