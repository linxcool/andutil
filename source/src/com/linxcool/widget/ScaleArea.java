package com.linxcool.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.linxcool.widget.ScaleView.OnFlingListener;
import com.linxcool.widget.ScaleView.OnZoomListener;

/**
 * 缩放区域视图
 * <p><b>Time:</b> 2013-11-12
 * @author 胡昌海(linxcool.hu)
 */
public class ScaleArea extends FrameLayout {

	private ScaleView touchView;
	private Context context;
	private Bitmap img;

	private OnZoomListener onZoomListener;
	private OnFlingListener onFlingListener;

	public ScaleArea(Context context) {
		super(context);
		init(context);
	}

	public ScaleArea(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScaleArea(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	void init(Context context){
		this.context = context;
	}

	public void setOnZoomListener(OnZoomListener onZoomListener) {
		this.onZoomListener = onZoomListener;
	}

	public void setOnFlingListener(OnFlingListener onFlingListener) {
		this.onFlingListener = onFlingListener;
	}

	public void setImg(Bitmap img) {
		this.img = img;
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		initScaleView(img,w,h);
	}

	public ScaleView getView() {
		return touchView;
	}

	public void show() {
		if(touchView != null){
			touchView.setVisibility(View.VISIBLE);
		}
	}

	public void hide() {
		if(touchView != null){
			touchView.setVisibility(View.GONE);
		}
	}

	private void initScaleView(Bitmap bimap,int imgDisplayW,int imgDisplayH) {
		if (bimap == null || touchView != null) 
			return;
		if (null == touchView) {
			touchView = new ScaleView(context, imgDisplayW, imgDisplayH);
			touchView.setOnZoomListener(onZoomListener);
			touchView.setOnFlingListener(onFlingListener);
			this.addView(touchView);
		}
		
		img = bimap;

		int imgW = img.getWidth();
		int imgH = img.getHeight();
		touchView.setImageBitmap(bimap);

		// 宽与高都小于屏幕时 直接取屏幕的宽与高
		int layout_w = imgDisplayW;
		int layout_h = imgDisplayH;

		if (imgW >= imgH) {
			if (layout_w == imgDisplayW) {
				layout_h = (int) (imgH * ((float) imgDisplayW / imgW));
			}
		} 
		else {
			if (layout_h == imgDisplayH) {
				layout_w = (int) (imgW * ((float) (imgDisplayH - 460) / imgH));
			}
		}

		LayoutParams params = new LayoutParams(layout_w,layout_h);
		params.gravity = Gravity.CENTER;
		touchView.setLayoutParams(params);
	}

	/**
	 * 回收图片
	 */
	public void recycleImg() {
		if(img == null)
			return;
		img.recycle();
		img = null;
	}
}
