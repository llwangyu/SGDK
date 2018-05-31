package com.android.fisher.sgface.util;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by Administrator on 2017-03-27.
 */

public class ImageFile {
    private File file;
    private Bitmap thumbBitmap;
    private int type=1;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Bitmap getThumbBitmap() {
        return thumbBitmap;
    }

    public void setThumbBitmap(Bitmap thumbBitmap) {
        this.thumbBitmap = thumbBitmap;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
