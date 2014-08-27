package com.linxcool.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.linxcool.view.Rotate3dAnimation;

/**
 * 3D旋转视图
 * <p><b>Time:</b> 2013-12-27
 * @author 胡昌海(Linxcool.Hu)
 */
public class Rotate3dView extends FrameLayout implements AnimationListener,Runnable{

	private View view;
	private boolean onRotate;
	private Rotate3dAnimation firstHalfAnim;
	private Rotate3dAnimation secondHalfAnim;
	
	public Rotate3dView(Context context) {
		super(context);
		init(context);
	}

	public Rotate3dView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public Rotate3dView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	void init(Context context){
		setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
	}
	
	public void applyRotation(View view, float start, float end) {
		if(onRotate || this.view == view)
			return;

		onRotate = true;
		this.view = view;
		
		float centerX = getWidth() / 2.0f;
		float centerY = getHeight() / 2.0f;

		firstHalfAnim = new Rotate3dAnimation(
				start, end, centerX, centerY, 310.0f, true);
		firstHalfAnim.setDuration(200);
		firstHalfAnim.setFillAfter(true);
		firstHalfAnim.setInterpolator(new AccelerateInterpolator());
		firstHalfAnim.setAnimationListener(this);

		startAnimation(firstHalfAnim);
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// Empty
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if(animation == firstHalfAnim) post(this);
		else onRotate = false;
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// Empty
	}

	@Override
	public void run() {
		float centerX = getWidth() / 2.0f;
		float centerY = getHeight() / 2.0f;

		removeAllViews();
		addView(view);

		secondHalfAnim = new Rotate3dAnimation(
				-90, 0, centerX, centerY,310.0f, false);
		secondHalfAnim.setDuration(500);
		secondHalfAnim.setFillAfter(true);
		secondHalfAnim.setInterpolator(new DecelerateInterpolator());
		secondHalfAnim.setAnimationListener(this);
		
		startAnimation(secondHalfAnim);
	}
}
