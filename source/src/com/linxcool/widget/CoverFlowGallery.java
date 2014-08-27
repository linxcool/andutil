package com.linxcool.widget;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * 3D画廊视图
 * <p><b>Time:</b> 2013-12-6
 * @author 胡昌海(linxcool.hu)
 */
@SuppressWarnings("deprecation")
public class CoverFlowGallery extends Gallery {

	private Camera camera = new Camera();
	private int maxRotationAngle = 50;
	private int maxZoom = -380;
	private int coveflowCenter;
	private boolean alphaMode = true;
	private boolean circleMode = false;

	public CoverFlowGallery(Context context) {
		super(context);
		this.setStaticTransformationsEnabled(true);
	}

	public CoverFlowGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setStaticTransformationsEnabled(true);
	}

	public CoverFlowGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setStaticTransformationsEnabled(true);
	}

	public int getMaxRotationAngle() {
		return maxRotationAngle;
	}

	public void setMaxRotationAngle(int maxRotationAngle) {
		this.maxRotationAngle = maxRotationAngle;
	}

	public boolean getCircleMode() {
		return circleMode;
	}

	public void setCircleMode(boolean isCircle) {
		circleMode = isCircle;
	}

	public boolean getAlphaMode() {
		return alphaMode;
	}

	public void setAlphaMode(boolean isAlpha) {
		alphaMode = isAlpha;
	}

	public int getMaxZoom() {
		return maxZoom;
	}

	public void setMaxZoom(int maxZoom) {
		this.maxZoom = maxZoom;
	}

	private int getCenterOfCoverflow() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
	}

	private static int getCenterOfView(View view) {
		return view.getLeft() + view.getWidth() / 2;
	}

	protected boolean getChildStaticTransformation(View child, Transformation t) {
		final int childCenter = getCenterOfView(child);
		final int childWidth = child.getWidth();
		int rotationAngle = 0;
		t.clear();
		t.setTransformationType(Transformation.TYPE_MATRIX);
		if (childCenter == coveflowCenter)
			transformImageBitmap((ImageView) child, t, 0);
		else {
			rotationAngle = (int) (((float) (coveflowCenter - childCenter) / childWidth) * maxRotationAngle);
			if (Math.abs(rotationAngle) > maxRotationAngle) 
				rotationAngle = (rotationAngle < 0) ? -maxRotationAngle : maxRotationAngle;
			transformImageBitmap((ImageView) child, t, rotationAngle);
		}
		return true;
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		coveflowCenter = getCenterOfCoverflow();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	private void transformImageBitmap(ImageView child, Transformation t, int rotationAngle) {
		camera.save();
		final Matrix imageMatrix = t.getMatrix();
		final int imageHeight = child.getLayoutParams().height;
		final int imageWidth = child.getLayoutParams().width;
		final int rotation = Math.abs(rotationAngle);
		//平移变换，x,y,z三轴
		camera.translate(0.0f, 0.0f, 0.0f);
		// 如视图的角度更少,放大 
		if (rotation <= maxRotationAngle) {
			float zoomAmount = (float) (maxZoom + (rotation * 1.5));
			camera.translate(0.0f, 0.0f, zoomAmount);
			if (circleMode) {
				if (rotation < 40) camera.translate(0.0f, 155, 0.0f);
				else camera.translate(0.0f, (255 - rotation * 2.5f), 0.0f);
			}
			if (alphaMode) {
				((ImageView) (child)).setAlpha((int) (255 - rotation * 2.5));
			}
		}
		camera.rotateY(rotationAngle);
		camera.getMatrix(imageMatrix);
		imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
		imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
		camera.restore();
	}

	@Override 
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) { 
		//降低滑动速度
		return super.onFling(e1, e2, velocityX/2, velocityY);
	} 
}
