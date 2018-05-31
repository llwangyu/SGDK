package com.android.fisher.sgface.util;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.android.fisher.sgface.base.MyApplication;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.Calendar;

public class FileUtil  {
    public static final String CHAT_PHOTO_DIR = "/formats/";

    public static void ceateDirs(Context context) {
        File file = new File(context.getCacheDir().getPath() + CHAT_PHOTO_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static String storeImageToFile(Context context, String msgId, Bitmap bitmap) {

        if (bitmap == null) {
            return null;
        }
//		ceateDirs(context);
        File file = null;
        RandomAccessFile accessFile = null;
        // 得到秒数，Date类型的getTime()返回毫秒数
        file = new File(getLocationFile(), msgId + ".jpg");
        if (file.exists()) file.delete();
        ByteArrayOutputStream steam = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, steam);
        byte[] buffer = steam.toByteArray();
        try {
            accessFile = new RandomAccessFile(file, "rw");
            accessFile.write(buffer);
        } catch (Exception e) {
            return null;
        }

        try {
            steam.close();
            accessFile.close();
        } catch (IOException e) {
        }
        return file.getPath();
    }

    private synchronized boolean saveChatPhoto(Context context, String dir,
                                               String filename, InputStream inputStream) {
        Boolean saved = false;
        if (null == inputStream)
            return false;
        File file = new File(context.getFilesDir() + dir);
        if (file.exists()) {
        } else {
            file.mkdirs();
        }
        file = new File(context.getFilesDir() + dir, filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();
                inputStream.close();
                fos.close();
                saved = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            // file.delete();
        }
        return saved;
    }

    public static String imageSave(Context context, Bitmap bitmap) {

        if (bitmap == null) {
            return null;
        }
        ceateDirs(context);
        final File file = new File(CHAT_PHOTO_DIR, "share_bg.jpg");// 得到秒数，Date类型的getTime()返回毫秒数
        RandomAccessFile accessFile = null;
        ByteArrayOutputStream steam = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, steam);
        final byte[] buffer = steam.toByteArray();

        try {
            accessFile = new RandomAccessFile(file, "rw");
            accessFile.write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        try {
            steam.close();
            accessFile.close();
        } catch (IOException e) {
        }
        bitmap.recycle();
        return file.getPath();
    }

    public static void saveBitmap(Bitmap bm, String picName, Context context) {
        Log.e("", "保存图片");
        try {
            if (!isFileExist("", context)) {
                ceateDirs(context);
            }
            File f = new File(context.getCacheDir().getPath() + CHAT_PHOTO_DIR,
                    picName + ".JPEG");
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.e("", "已经保存");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File createSDDir(String dirName, Context context)
            throws IOException {
        File dir = new File(context.getCacheDir().getPath() + CHAT_PHOTO_DIR
                + dirName);

        return dir;
    }

    public static boolean isFileExist(String fileName, Context context) {
        File file = new File(context.getCacheDir().getPath() + CHAT_PHOTO_DIR
                + fileName);
        file.isFile();
        return file.exists();
    }

    public static void delFile(String fileName) {
        File file = new File(CHAT_PHOTO_DIR + fileName);
        if (file.isFile()) {
            file.delete();
        }
        file.exists();
    }

    public static void deleteDir(Context context) {
        File dir = new File(context.getCacheDir().getPath() + CHAT_PHOTO_DIR);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDir(context); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    public static boolean fileIsExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {

            return false;
        }
        return true;
    }

    public static String getCacheFilePath() {
        File dir11 = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            File sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
            dir11 = new File(sdDir.getPath() + "/messagewav");
            if (!dir11.exists()) {
                dir11.mkdirs();
            }
        }
        return dir11.getPath();
    }

    private static final String FILE_CACHE_DIR = "download";

    public static File getDownloadFile(String url) {
        String[] nameStrs = url.split("/");
        String name = nameStrs[nameStrs.length - 1];
        File cacheDir = FileUtil.getFileSaveDir();
        cacheDir.mkdirs();
        File appFile = new File(cacheDir, name);
        return appFile;
    }


    private static final String IMG_CACHE_DIR = "img";

    public static File getTempPhotoFile() {
        Calendar calendar = Calendar.getInstance();
        File cacheDir = new File(getCacheFilePath() + "/" + IMG_CACHE_DIR);
        cacheDir.mkdirs();
        File imgFile = new File(cacheDir, calendar.getTimeInMillis() + ".jpg");
        return imgFile;
    }

    public static void copyFile(File srcFile, File dstFile) throws IOException {
        InputStream is = new FileInputStream(srcFile);
        saveFile(is, dstFile);
        is.close();
    }

    private final static int BUFFER_SIZE = 1024;

    public static void saveFile(InputStream inputStream, File dstFile)
            throws IOException {
        if (dstFile.exists()) {
            dstFile.delete();
        } else {
            File dir = dstFile.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        dstFile.createNewFile();

        OutputStream os = new FileOutputStream(dstFile);
        byte[] buffer = new byte[BUFFER_SIZE];
        int count = -1;
        while ((count = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
            os.write(buffer, 0, count);
        }

        os.flush();
        os.close();
    }

    public static File getFile(Activity activity, Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor actualimagecursor = activity.managedQuery(uri, proj, null, null,
                null);
        int actual_image_column_index = actualimagecursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String filePath = actualimagecursor
                .getString(actual_image_column_index);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            actualimagecursor.close();
        if (filePath == null) {
            return null;
        }
        return new File(filePath);
    }

    //this method only for kitkat
    @SuppressLint("NewApi")
    public static String uriToPath(Uri uri) {
        // Will return "image:x*"
        String wholeID = DocumentsContract
                .getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = MyApplication.getContext().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel,
                new String[]{id}, null);

        String filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        cursor.close();
        return filePath;

    }

    public static String saveBitmapToFile(File file, Bitmap bitmap) {
        RandomAccessFile accessFile = null;
        ByteArrayOutputStream steam = new ByteArrayOutputStream();

        if (bitmap == null) {
            return null;
        }
        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
            if (file.getName().contains("jpg")) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, steam);
            } else {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, steam);
            }
            final byte[] buffer = steam.toByteArray();
            accessFile = new RandomAccessFile(file, "rw");
            accessFile.write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                steam.close();
                accessFile.close();
            } catch (IOException e) {
            }
        }
        bitmap = null;
        return file.getPath();
    }

    public static String convertSize(long size) {
        DecimalFormat df = new DecimalFormat("###.#");
        float f;
        if (size < 1024 * 1024) {
            f = (float) ((float) size / (float) 1024);
            return (df.format(Float.valueOf(f).doubleValue()) + "KB");
        } else {
            f = (float) ((float) size / (float) (1024 * 1024));
            return (df.format(Float.valueOf(f).doubleValue()) + "MB");
        }
    }

    public static File getPicSaveDir() {
        File dir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            File sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
            dir = new File(sdDir.getPath() + "/officehelper");
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        return dir;
    }

    public static File getPicSaveFile() {
        File dir = getPicSaveDir();
        if (dir == null) {
            return null;
        }
        String name = Calendar.getInstance().getTimeInMillis() + ".jpg";
        return new File(dir, name);
    }

    public static void refreshImageFile(Context context, File imageFile) {
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + imageFile.getPath())));

    }

    public static void openFile(File file, String type) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, type);
        MyApplication.getContext().startActivity(intent);
    }

    public static void deleteDir(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
//            if (file.isFile())
//                file.delete(); // 删除所有文件
            deletFile(file);
        }
    }

    public static File getLocationFile() {
//        Calendar calendar = Calendar.getInstance();
        File cacheDir = new File(getCacheFilePath() + "/image2");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    public static String sendMsgBitmapToFile(File file, Bitmap bitmap) {
        RandomAccessFile accessFile = null;
        ByteArrayOutputStream steam = new ByteArrayOutputStream();

        if (bitmap == null) {
            return null;
        }
        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
            if (file.getName().contains("jpg")) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, steam);
            } else {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, steam);
            }
            final byte[] buffer = steam.toByteArray();
            accessFile = new RandomAccessFile(file, "rw");
            accessFile.write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                steam.close();
                accessFile.close();
            } catch (IOException e) {
            }
        }
        bitmap = null;
        return file.getPath();
    }


    public static File getDownloadPath() {
        File cacheDir = new File(getCacheFilePath() + "/" + FILE_CACHE_DIR);
        if (!cacheDir.exists()) {
            return null;
        }
        return cacheDir;
    }

    public static File getFileSaveDir() {
        File dir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            File sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
            dir = new File(sdDir.getPath() + "/waferDownload");
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        return dir;
    }

    public static boolean isFile(String url, int size) {
        File downloadFile = FileUtil.getFileSaveDir();
        if (downloadFile != null) {
            for (File dir : downloadFile.listFiles()) {
                if (dir.getName().equals(url
                        .substring(url.lastIndexOf("/") + 1))) {
                    try {
                        FileInputStream fis = new FileInputStream(dir.getPath());
                        int length=fis.available();
                        if (size == length) {
                            return true;
                        }else{
                            deletFile(dir);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
        }
        return false;
    }

    private static void deletFile(File file){
        if (file.isFile())
            file.delete();
    }

    private static final String APK_CACHE_DIR = "apk";
    private static final String APK_UPDATE_DIR = "apkUpdate";

    public static File getAppDownloadFile(String url) {
        String[] nameStrs = url.split("/");
        String name = nameStrs[nameStrs.length - 1];
        File cacheDir = new File(getCacheFilePath() + "/" + APK_CACHE_DIR);
        cacheDir.mkdirs();
        File appFile = new File(cacheDir, name);
        return appFile;
    }

    public static File getAppUpdateFile(String url) {
        String[] nameStrs = url.split("/");
        String name = nameStrs[nameStrs.length - 1];
        File cacheDir = new File(getCacheFilePath() + "/" + APK_UPDATE_DIR);
        cacheDir.mkdirs();
        File appFile = new File(cacheDir, name);
        return appFile;
    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * @param videoPath 视频的路径
     * @param width 指定输出视频缩略图的宽度
     * @param height 指定输出视频缩略图的高度度
     * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                           int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        System.out.println("w"+bitmap.getWidth());
        System.out.println("h"+bitmap.getHeight());
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 读取文件内容，作为字符串返回
     */
    public static String readFileAsString(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        }

        if (file.length() > 1024 * 1024 * 1024) {
            throw new IOException("File is too large");
        }

        StringBuilder sb = new StringBuilder((int) (file.length()));
        // 创建字节输入流
        FileInputStream fis = new FileInputStream(filePath);
        // 创建一个长度为10240的Buffer
        byte[] bbuf = new byte[10240];
        // 用于保存实际读取的字节数
        int hasRead = 0;
        while ( (hasRead = fis.read(bbuf)) > 0 ) {
            sb.append(new String(bbuf, 0, hasRead));
        }
        fis.close();
        return sb.toString();
    }

    /**
     * 根据文件路径读取byte[] 数组
     */
    public static byte[] readFileByBytes(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
            BufferedInputStream in = null;

            try {
                in = new BufferedInputStream(new FileInputStream(file));
                short bufSize = 1024;
                byte[] buffer = new byte[bufSize];
                int len1;
                while (-1 != (len1 = in.read(buffer, 0, bufSize))) {
                    bos.write(buffer, 0, len1);
                }

                byte[] var7 = bos.toByteArray();
                return var7;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException var14) {
                    var14.printStackTrace();
                }

                bos.close();
            }
        }
    }

    /**
     * 根据文件路径读取byte[] 数组
     */
    public static byte[] readFileByBytes(File file) throws IOException {
        //File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException();
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
            BufferedInputStream in = null;

            try {
                in = new BufferedInputStream(new FileInputStream(file));
                short bufSize = 1024;
                byte[] buffer = new byte[bufSize];
                int len1;
                while (-1 != (len1 = in.read(buffer, 0, bufSize))) {
                    bos.write(buffer, 0, len1);
                }

                byte[] var7 = bos.toByteArray();
                return var7;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException var14) {
                    var14.printStackTrace();
                }

                bos.close();
            }
        }
    }

    //bitmap写入文件
    public static Uri saveMyBitmap(Bitmap bm,int quality,Uri inUri) throws IOException,URISyntaxException {
        File f = new File(new URI(inUri.toString()));
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, quality, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Uri.fromFile(f);
    }
//从imageview中提取file
    public static File getFileFromIV(ImageView iv){
        try{
            iv.setDrawingCacheEnabled(true);
            Bitmap ivBitmap = Bitmap.createBitmap(iv.getDrawingCache());
            iv.setDrawingCacheEnabled(false);
            File file=new File(Environment.getExternalStorageDirectory(), "/sgface/"+System.currentTimeMillis() + ".jpg");
            OutputStream os = new FileOutputStream(file);
            ivBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            return file;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
