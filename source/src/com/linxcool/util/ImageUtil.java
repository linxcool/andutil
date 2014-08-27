package com.linxcool.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;

/**
 * 图片处理工具
 * <p><b>Time:</b> 2014-2-18
 * @author 胡昌海(Linxcool.Hu)
 */
public class ImageUtil {

	/**
	 * Bitmap转Drawable 支持.9.png
	 * @param res
	 * @param src
	 * @return
	 */
	public static Drawable Bitmap2Drawable(Resources res,Bitmap src){
		byte[] chunk = src.getNinePatchChunk();
		boolean isNinePatch = NinePatch.isNinePatchChunk(chunk);
		if (isNinePatch) return new NinePatchDrawable(
				res, src, chunk, new Rect(), null);
		return new BitmapDrawable(res,src);
	}
	
	/**
	 * 旋转图片
	 * @param img
	 * @param degrees
	 * @return
	 */
	public static Bitmap rotateBitmap(Bitmap img, float degrees){
        Matrix matrix = new Matrix(); 
        matrix.postRotate(degrees);
        int width = img.getWidth();
        int height = img.getHeight();
        return Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
	}
	
	/**
	 * 缩放图片
	 * @param bm
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
	   // 获得图片的宽高
	   int width = bm.getWidth();
	   int height = bm.getHeight();
	   
	   if(width == newWidth && height == newHeight){
		   return bm;
	   }
	   
	   // 计算缩放比例
	   float scaleWidth = ((float) newWidth) / width;
	   float scaleHeight = ((float) newHeight) / height;
	   // 取得想要缩放的matrix参数
	   Matrix matrix = new Matrix();
	   matrix.postScale(scaleWidth, scaleHeight);
	   // 得到新的图片
	   Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
	   return newbm;
	}
	
	/**
	 * 创建倒影图片
	 * @param originalImage
	 * @return
	 */
	public static Bitmap createReflectedBitmap(Bitmap originalImage){
		final int reflectionGap = 4;

		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		//Matrix 旋转、镜像处理的矩阵类
		Matrix matrix = new Matrix();
		
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
				height / 3, width, height / 3, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(
				width, (height + height / 3), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);

		canvas.drawBitmap(originalImage, 0, 0, null);
		Paint deafaultPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(
				0, originalImage.getHeight(), 
				0, bitmapWithReflection.getHeight() + reflectionGap, 
				0x70ffffff, 0x00ffffff,
				TileMode.MIRROR);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
		
		return bitmapWithReflection;
	}
	
	/**
	 * 修正图片尺寸(默认<=256)
	 * @param filePath 文件完整路径
	 * @param size 尺寸
	 * @return
	 */
	public static Bitmap revisionImageSize(String filePath){
		File file = new File(filePath);
		if(!file.exists() || file.isDirectory())
			return null;
		
		Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		
		int i = 0;
		while (!( (options.outWidth >> i <= 256) && (options.outHeight >> i <= 256) ))
			i++;
		options.inSampleSize = (int) Math.pow(2.0d, i);
		options.inJustDecodeBounds = false;
		options.inDither = false;
		
		return BitmapFactory.decodeFile(filePath, options);
	}
	
	/**
	 * 修正图片尺寸(默认<=256)
	 * @param is 图片输入流(会自动关闭)
	 * @param size 尺寸(可为空)
	 * @return
	 */
	public static Bitmap revisionImageSize(InputStream is) {
		try{
			Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(is, null, options);

			int i = 0;
			while (!( (options.outWidth >> i <= 256) && (options.outHeight >> i <= 256) ))
				i++;
			
			options.inSampleSize = (int) Math.pow(2.0d, i);
			options.inJustDecodeBounds = false;
			options.inDither = false;

			return BitmapFactory.decodeStream(is, null, options);
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 添加水印
	 * @param src
	 * @param watermark
	 * @return
	 */
	public static Bitmap addWaterMark(Bitmap src, Bitmap watermark) {
		if (src == null || watermark  == null) {
			return src;
		}

		int sWid = src.getWidth();
		int sHei = src.getHeight();
		int wWid = watermark.getWidth();
		int wHei = watermark.getHeight();
		if (sWid == 0 || sHei == 0) {
			return null;
		}

		if (sWid < wWid || sHei < wHei) {
			return src;
		}

		Bitmap bitmap = Bitmap.createBitmap(sWid, sHei, Config.ARGB_8888);
		try {
			Canvas cv = new Canvas(bitmap);
			cv.drawBitmap(src, 0, 0, null);
			cv.drawBitmap(watermark, 0, 0, null);
			cv.save(Canvas.ALL_SAVE_FLAG);
			cv.restore();
		} catch (Exception e) {
			bitmap = null;
			e.getStackTrace();
		}
		return bitmap;
	}
}
