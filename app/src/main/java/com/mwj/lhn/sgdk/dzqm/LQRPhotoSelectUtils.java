package com.mwj.lhn.sgdk.dzqm;

/**
 * Created by lhn on 2017/11/14.
 */

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LQRPhotoSelectUtils {

    public static final int REQ_TAKE_PHOTO = 10001;
    public static final int REQ_SELECT_PHOTO = 10002;
    public static final int REQ_ZOOM_PHOTO = 10003;
    private Activity mActivity;
    //拍照或剪切后图片的存放位置(参考file_provider_paths.xml中的路径)
    private String imgPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + String.valueOf(System.currentTimeMillis()) + ".jpg";
    //FileProvider的主机名：一般是包名+".fileprovider"，严格上是build.gradle中defaultConfig{}中applicationId对应的值+".fileprovider"
    private String AUTHORITIES = "packageName" + ".fileprovider";
    private boolean mShouldCrop = false;
    private Uri mOutputUri = null;
    private File mInputFile;
    private File mOutputFile = null;

    //剪裁图片宽高比
    private int mAspectX = 1;
    private int mAspectY = 1;
    //剪裁图片大小
    private int mOutputX = 800;
    private int mOutputY = 480;
    PhotoSelectListener mListener;

    /**
     * 可指定是否在拍照或从图库选取照片后进行裁剪
     * <p>
     * 默认裁剪比例1:1，宽度为800，高度为480
     *
     * @param activity   上下文
     * @param listener   选取图片监听
     * @param shouldCrop 是否裁剪
     */
    public LQRPhotoSelectUtils(Activity activity, PhotoSelectListener listener, boolean shouldCrop) {
        mActivity = activity;
        mListener = listener;
        mShouldCrop = shouldCrop;
        AUTHORITIES = activity.getPackageName() + ".fileprovider";
        imgPath = generateImgePath();

    }

    /**
     * 可以拍照或从图库选取照片后裁剪的比例及宽高
     *
     * @param activity 上下文
     * @param listener 选取图片监听
     * @param aspectX  图片裁剪时的宽度比例
     * @param aspectY  图片裁剪时的高度比例
     * @param outputX  图片裁剪后的宽度
     * @param outputY  图片裁剪后的高度
     */
    public LQRPhotoSelectUtils(Activity activity, PhotoSelectListener listener, int aspectX, int aspectY, int outputX, int outputY) {
        this(activity, listener, true);

        mAspectX = aspectX;
        mAspectY = aspectY;
        mOutputX = outputX;
        mOutputY = outputY;
    }


    public void setAuthorities(String authorities) {
        this.AUTHORITIES = authorities;
    }


    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }


    public void takePhoto() {
        File imgFile = new File(imgPath);
        if (!imgFile.getParentFile().exists()) {
            imgFile.getParentFile().mkdirs();
        }
        Uri imgUri = null;
        if (Build.VERSION.SDK_INT < 24) {
            imgUri = Uri.fromFile(imgFile);
        } else {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, imgFile.getAbsolutePath());
            imgUri = mActivity.getApplication().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        mActivity.startActivityForResult(intent, REQ_TAKE_PHOTO);
    }

    public void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        mActivity.startActivityForResult(intent, REQ_SELECT_PHOTO);
    }

    private void zoomPhoto(File inputFile, File outputFile) {
        File parentFile = outputFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setDataAndType(getImageContentUri(mActivity, inputFile), "image/*");
        } else {
            intent.setDataAndType(Uri.fromFile(inputFile), "image/*");
        }
        intent.putExtra("crop", "true");
        //设置剪裁图片宽高比
        intent.putExtra("mAspectX", mAspectX);
        intent.putExtra("mAspectY", mAspectY);
        //设置剪裁图片大小
        intent.putExtra("mOutputX", mOutputX);
        intent.putExtra("mOutputY", mOutputY);
        // 是否返回uri
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        mActivity.startActivityForResult(intent, REQ_ZOOM_PHOTO);
    }

    public void attachToActivityForResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case LQRPhotoSelectUtils.REQ_TAKE_PHOTO://拍照
                    mInputFile = new File(imgPath);
                    if (mShouldCrop) {//裁剪
                        mOutputFile = new File(generateImgePath());
                        mOutputUri = Uri.fromFile(mOutputFile);
                        zoomPhoto(mInputFile, mOutputFile);
                    } else {//不裁剪
                      //  filetobmap(mInputFile);
                        mOutputUri = Uri.fromFile(mInputFile);
                        if (mListener != null) {
                            mListener.onFinish(mInputFile, mOutputUri);
                        }
                    }
                    break;
                case LQRPhotoSelectUtils.REQ_SELECT_PHOTO://图库
                    if (data != null) {
                        Uri sourceUri = data.getData();
                        String[] proj = {MediaStore.Images.Media.DATA};
                        Cursor cursor = mActivity.managedQuery(sourceUri, proj, null, null, null);
                        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        String imgPath = cursor.getString(columnIndex);
                        mInputFile = new File(imgPath);
                        if (mShouldCrop) {
                            mOutputFile = new File(generateImgePath());
                            mOutputUri = Uri.fromFile(mOutputFile);
                            zoomPhoto(mInputFile, mOutputFile);
                        } else {//不裁剪
                          //  filetobmap(mInputFile);
                            mOutputUri = Uri.fromFile(mInputFile);
                            if (mListener != null) {
                                mListener.onFinish(mInputFile, mOutputUri);
                            }
                        }
                    }
                    break;
                case LQRPhotoSelectUtils.REQ_ZOOM_PHOTO://裁剪
                    if (data != null) {
                        if (mOutputUri != null) {
                            File tmpFile = new File(imgPath);
                            if (tmpFile.exists())
                                tmpFile.delete();
                            if (mListener != null) {
                                mListener.onFinish(mOutputFile, mOutputUri);
                            }
                        }
                    }
                    break;
            }
        }
    }


    /**
     * 安卓7.0裁剪根据文件路径获取uri
     */
    private Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * 产生图片的路径，带文件夹和文件名，文件名为当前毫秒数
     */
    private String generateImgePath() {

        return getExternalStoragePath() + File.separator +(int)(Math.random()*100)+ String.valueOf(System.currentTimeMillis()) + ".jpg";
    }
    /**
     * 获取SD下的应用目录
     */
    private String getExternalStoragePath() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(File.separator);
        String ROOT_DIR = "Android/data/" + mActivity.getPackageName();
        sb.append(ROOT_DIR);
        sb.append(File.separator);
        return sb.toString();
    }

    public interface PhotoSelectListener {
        void onFinish(File outputFile, Uri outputUri);
    }
    public interface PhotoTakeListener {
        void onFinish(File outputFile, Uri outputUri);
    }

}
