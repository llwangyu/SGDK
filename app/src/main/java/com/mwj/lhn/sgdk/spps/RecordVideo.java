package com.mwj.lhn.sgdk.spps;


import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import com.mwj.lhn.sgdk.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.VideoEncoder;
import android.media.MediaRecorder.VideoSource;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


/**
 * Created by xiaomengli on 16/4/27.
 */
public class RecordVideo extends LinearLayout implements OnErrorListener {
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private ProgressBar mProgressBar;
    private Activity mActivity;
    private MediaRecorder mMediaRecorder;
    private Camera mCamera;
    private Timer mTimer;// 计时器
    private OnRecordFinishListener mOnRecordFinishListener;// 录制完成回调接口

    private int mWidth;// 视频分辨率宽度
    private int mHeight;// 视频分辨率高度
    private boolean isOpenCamera;// 是否一开始就打开摄像头
    private int mRecordMaxTime;// 一次拍摄最长时间
    private int mTimeCount;// 时间计数
    private File mRecordFile = null;// 文件

    public RecordVideo(Context context) {
        this(context, null);
    }

    public RecordVideo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("NewApi")
    public RecordVideo(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // 初始化各项组件

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecordVideo1, defStyle, 0);
        mWidth = a.getInteger(R.styleable.RecordVideo1_witdh, 640);// 默认320
        mHeight = a.getInteger(R.styleable.RecordVideo1_hieght, 480);// 默认240

        isOpenCamera = a.getBoolean(R.styleable.RecordVideo1_open_camera, true);// 默认打开
        mRecordMaxTime = a.getInteger(R.styleable.RecordVideo1_timeLenght, 11);// 默认为10

        LayoutInflater.from(context).inflate(R.layout.record_video, this);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setMax(mRecordMaxTime);// 设置进度条最大量

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new CustomCallBack());
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        a.recycle();
    }

    private class CustomCallBack implements Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (!isOpenCamera)
                return;
            try {
                initCamera();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (!isOpenCamera)
                return;
            freeCameraResource();
        }

    }

    /**
     * 初始化摄像头
     * @throws IOException
     */
    @SuppressLint("NewApi")
    private void initCamera() throws IOException {
        if (mCamera != null) {
            freeCameraResource();
        }
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
            freeCameraResource();
        }
        if (mCamera == null)
            return;

        // setCameraParams();
        mCamera.setDisplayOrientation(90);
        mCamera.setPreviewDisplay(mSurfaceHolder);
        mCamera.startPreview();
        mCamera.unlock();
    }

    /**
     * 设置摄像头为竖屏
     */
    /*private void setCameraParams() {
        if (mCamera != null) {
            Parameters params = mCamera.getParameters();
            params.set("orientation", "portrait");
            mCamera.setParameters(params);
        }
    }*/

    /**
     * 释放摄像头资源
     */
    private void freeCameraResource() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
            mCamera = null;
        }
    }

    private void createRecordDir() {

        File sampleDir = new File(  Environment.getExternalStorageDirectory().getPath() + "/sgsp/");
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }
        File vecordDir = sampleDir;
        // 创建文件
        try {
            mRecordFile = File.createTempFile("record", ".mp4", vecordDir); //mp4格式
            Log.i("-------", mRecordFile.getAbsolutePath());
        } catch (IOException e) {
        }
    }

    /**
     * 初始化
     * @throws IOException
     */
    private void initRecord() throws IOException {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset();
        if (mCamera != null)
            mMediaRecorder.setCamera(mCamera);
        // 输出旋转90度，保持竖屏录制
        mMediaRecorder.setOrientationHint(90);

        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
    /* Fixed video Size: 640 * 480*/
        mMediaRecorder.setVideoSize(640, 480);
    /* Encoding bit rate: 1 * 1024 * 1024*/
        mMediaRecorder.setVideoEncodingBitRate(3 * 1024 * 1024);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        mMediaRecorder.setOutputFile(mRecordFile.getAbsolutePath());
        mMediaRecorder.prepare();
        try {
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始录制视频
     * @param onRecordFinishListener
     * 达到指定时间之后回调接口
     */
    public void record(final OnRecordFinishListener onRecordFinishListener) {
        this.mOnRecordFinishListener = onRecordFinishListener;
        createRecordDir();
        try {
            if (!isOpenCamera)// 如果未打开摄像头，则打开
                initCamera();
            initRecord();
            mTimeCount = 0;// 时间计数器重新赋值
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mTimeCount++;
                    mProgressBar.setProgress(mTimeCount);
                    if (mTimeCount == (mRecordMaxTime-1)) {
                        stop();
                        if (mOnRecordFinishListener != null)
                            mOnRecordFinishListener.onRecordFinish();
                    }
                }
            }, 0, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止拍摄
     */
    public void stop() {

        releaseRecord();
        freeCameraResource();
        stopRecord();
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        mProgressBar.setProgress(0);
        if (mTimer != null)
            mTimer.cancel();
        if (mMediaRecorder != null) {
            // 设置后不会崩
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mMediaRecorder.setPreviewDisplay(null);
        }
    }

    /**
     * 释放资源
     */
    private void releaseRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMediaRecorder = null;
    }

    public int getTimeCount() {
        return mTimeCount;
    }

    /**
     * @return the mVecordFile
     */
    public File getmRecordFile() {
        return mRecordFile;
    }

    /**
     * 录制完成回调接口
     */
    public interface OnRecordFinishListener {
        public void onRecordFinish();
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null)
                mr.reset();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
