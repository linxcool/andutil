package com.linxcool.widget;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.linxcool.util.ImageUtil;

/**
 * 广告视图
 * <p><b>Time:</b> 2014-2-18
 * @author 胡昌海(Linxcool.Hu)
 */
public class AdvertiseView extends ViewFlipper {

	private static final long SPEED = 800;
	
	private int imgSize = 35;
	private int viewHight = 36;
	private int textColor;
	private TextView tv;
	
	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public void setImgSize(int imgSize) {
		this.imgSize = imgSize;
	}

	public void setViewHight(int viewHight) {
		this.viewHight = viewHight;
	}

	public AdvertiseView(Context context) {
		super(context);
		init();
	}

	public AdvertiseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	protected void init(){
		TranslateAnimation toTopIn = new TranslateAnimation(
				0x1, 0, 0x1, 0, 0x1, 1, 0x1, 0);
		toTopIn.setDuration(SPEED);

		TranslateAnimation toTopOut = new TranslateAnimation(
				0x1, 0, 0x1, 0, 0x1, 0, 0x1, -1);
		toTopOut.setDuration(SPEED);
		
		setInAnimation(toTopIn);
		setOutAnimation(toTopOut);
		
		setPadding(dp2px(10), dp2px(2), dp2px(10), dp2px(2));
		
		textColor = Color.BLACK;
	}
	
	public int getCurrentId(){
		Object tag = getCurrentView().getTag();
		return (Integer) tag;
	}
	
	public void addViewContent(int id, String imgPath,String text){
		TextView tv = crateTextView(id, imgPath, text);
		LayoutParams params = new LayoutParams(-1, dp2px(viewHight));
		addView(tv, params);
	}
	
	protected TextView crateTextView(int id, String imgPath,String text){
		Bitmap bitmap = null;
		if(!imgPath.startsWith("imgs")){
			bitmap = ImageUtil.revisionImageSize(imgPath);
		} else {
			AssetManager am = getResources().getAssets();
			try {
				bitmap = ImageUtil.revisionImageSize(am.open(imgPath));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		tv = new TextView(getContext());
		
		if(bitmap != null){
			bitmap = ImageUtil.zoomImg(bitmap, imgSize, imgSize);
			BitmapDrawable left = new BitmapDrawable(getResources(), bitmap);
			
			Bitmap ic = BitmapFactory.decodeResource(getResources(), R.drawable.abc_ic_ab_back_holo_dark);
			ic = ImageUtil.rotateBitmap(ic, 180);
			Drawable right = new BitmapDrawable(getResources(), ic);
			
			tv.setCompoundDrawablesWithIntrinsicBounds(left, null, right, null);	
		}

		tv.setTag(id);
		tv.setText(text);
		tv.setTextColor(textColor);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		tv.setGravity(Gravity.CENTER_VERTICAL);
		tv.setCompoundDrawablePadding(dp2px(5));
		
		return tv;
	}
	
	public int dp2px(float dp){
		float density = getResources().getDisplayMetrics().density;
		return (int) (density*dp + 0.5);
	}
}
