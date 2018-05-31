package com.android.fisher.sgface.util;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class BigImageFileUtil {

	/*
	 * 调整图片大小，适合大图片
	 */
	public static File resizeImageFile(File photoFile, int width, int heigth) {
		if (photoFile == null) {
			return null;
		}
		File file = FileUtil.getTempPhotoFile();
		Bitmap bitmap = getResizeBitmap(photoFile, width, heigth);
		FileUtil.saveBitmapToFile(file, bitmap);
		return file;
	}

	/*
	 * 获取调整大小后的bitmap
	 */
	public static Bitmap getResizeBitmap(File photoFile, int width, int heigth) {
		if (!photoFile.exists()) {
			return null;
		}
		Bitmap originalBitmap = getOriginalBitmap(photoFile, width, heigth);
		Bitmap zoomedBitmap = zoomBitmap(originalBitmap, width, heigth);
		if (!originalBitmap.isRecycled()) {
			originalBitmap.recycle();
		}
		return zoomedBitmap;
	}

	/*
	 * 获取调整大小并转正后的bitmap
	 */
	public static Bitmap getResizeRotatedBitmap(File photoFile, int width,
			int heigth1) {
		if (!photoFile.exists()) {
			return null;
		}
		Bitmap bitmap = getResizeBitmap(photoFile, width, heigth1);
		int ori = getImageFileOrientation(photoFile);
		return rotateBitmap(ori, bitmap);
	}

	public static int getImageFileOrientation(File photoFile) {
		if (photoFile == null) {
			return 0;
		}
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(photoFile.getPath());
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
				ExifInterface.ORIENTATION_UNDEFINED);
	}

	public static Bitmap rotateBitmap(int ori, Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}

		int digree;
		switch (ori) {
		case ExifInterface.ORIENTATION_ROTATE_90:
			digree = 90;
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			digree = 180;
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			digree = 270;
			break;
		default:
			digree = 0;
			break;
		}

		Matrix m = new Matrix();
		m.postRotate(digree);

		Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
				m, true);
		return rotatedBitmap;
	}

	private static Bitmap zoomBitmap(Bitmap originalBitmap, int width,
			int heigth) {
		if (originalBitmap == null) {
			return null;
		}
		int targetHeight;
		int targetWidth;
		float photoWHRatio = (float) originalBitmap.getWidth()
				/ (float) originalBitmap.getHeight();
		float targetWHRatio = (float) width / (float) heigth;

		// 根据长宽比计算，确保图片等比缩放到最大适应目标大小
		if (photoWHRatio > targetWHRatio) { // 当前图片比目标图片更遍
			targetWidth = width;
			targetHeight = (int) (width / photoWHRatio);
		} else {
			targetWidth = (int) (heigth * photoWHRatio);
			targetHeight = heigth;
		}

		Bitmap targetBitmap = Bitmap.createScaledBitmap(originalBitmap,
				targetWidth, targetHeight, true);
		return targetBitmap;
	}

	private static Bitmap getOriginalBitmap(File photoFile, int width,
			int heigth) {
		if (photoFile == null) {
			return null;
		}
		Bitmap originalBitmap = null;
		FileInputStream fs = null;
		BufferedInputStream bs = null;
		try {
			fs = new FileInputStream(photoFile);
			bs = new BufferedInputStream(fs);
			BitmapFactory.Options options = getBitmapOptions(photoFile, width,
					heigth);
			if (options == null) {
				return null;
			}
			originalBitmap = BitmapFactory.decodeStream(bs, null, options);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bs.close();
				fs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return originalBitmap;
	}

	private static BitmapFactory.Options getBitmapOptions(File photoFile,
			int width, int heigth) {
		if (photoFile == null) {
			return null;
		}
		// 计算缩小比例
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(photoFile.getPath(), opts);
		int outWidth = opts.outWidth; // 获得图片的实际高和宽
		int outHeight = opts.outHeight;
		opts.inDither = false;

		opts.inSampleSize = 1;
		if (outWidth != 0 && outHeight != 0 && width != 0 && heigth != 0) {
			int sampleSize = Math.min(outWidth / width, outHeight / heigth);// 使用较小的比例进行缩放
			opts.inSampleSize = sampleSize;
		}
		opts.inJustDecodeBounds = false;
		return opts;
	}
}
