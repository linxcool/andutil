
package com.linxcool.view;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

/**
 * 旋转动画绘图
 * <p><b>Time:</b> 2013-12-19
 * @author 胡昌海(Linxcool.Hu)
 */
public class AnimatedRotateDrawable extends AnimationDrawable implements Drawable.Callback, Runnable{

	private AnimatedRotateState arState;
	private boolean isMutated;
	private float currentDegrees;
	private float increment;
	private boolean isRunning;

	public AnimatedRotateDrawable(Drawable drawable) {
		this(null,drawable);
	}

	public AnimatedRotateDrawable(AnimatedRotateState state,Drawable drawable) {
		arState = new AnimatedRotateState(state, this);
		arState.drawable = drawable;
		init();
	}


	private void init() {
		final AnimatedRotateState state = arState;
		increment = 360.0f / state.framesCount;
		final Drawable drawable = state.drawable;
		if (drawable != null) {
			drawable.setFilterBitmap(true);
			if (drawable instanceof BitmapDrawable) {
				((BitmapDrawable) drawable).setAntiAlias(true);
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		int saveCount = canvas.save();

		final AnimatedRotateState st = arState;
		final Drawable drawable = st.drawable;
		final Rect bounds = drawable.getBounds();

		int w = bounds.right - bounds.left;
		int h = bounds.bottom - bounds.top;

		float px = st.xRelative ? (w * st.pivotX) : st.pivotX;
		float py = st.yRelative ? (h * st.pivotY) : st.pivotY;

		canvas.rotate(currentDegrees, px + bounds.left, py + bounds.top);

		drawable.draw(canvas);

		canvas.restoreToCount(saveCount);
	}

	public void start() {
		if (!isRunning) {
			isRunning = true;
			nextFrame();
		}
	}

	public void stop() {
		isRunning = false;
		unscheduleSelf(this);
	}

	public boolean isRunning() {
		return isRunning;
	}

	private void nextFrame() {
		unscheduleSelf(this);
		scheduleSelf(this, SystemClock.uptimeMillis() + arState.frameDuration);
	}

	public void run() {
		currentDegrees += increment;
		if (currentDegrees > (360.0f - increment)) {
			currentDegrees = 0.0f;
		}
		invalidateSelf();
		nextFrame();
	}

	@Override
	public boolean setVisible(boolean visible, boolean restart) {
		arState.drawable.setVisible(visible, restart);
		boolean changed = super.setVisible(visible, restart);
		if (visible) {
			if (changed || restart) {
				currentDegrees = 0.0f;
				nextFrame();
			}
		} else {
			unscheduleSelf(this);
		}
		return changed;
	}    

	public Drawable getDrawable() {
		return arState.drawable;
	}

	@Override
	public int getChangingConfigurations() {
		return super.getChangingConfigurations()
		| arState.changingConfigurations
		| arState.drawable.getChangingConfigurations();
	}

	@Override
	public void setAlpha(int alpha) {
		arState.drawable.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		arState.drawable.setColorFilter(cf);
	}

	@Override
	public int getOpacity() {
		return arState.drawable.getOpacity();
	}

	@Override
	public boolean getPadding(Rect padding) {
		return arState.drawable.getPadding(padding);
	}

	@Override
	public boolean isStateful() {
		return arState.drawable.isStateful();
	}

	@Override
	protected void onBoundsChange(Rect bounds) {
		arState.drawable.setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
	}

	@Override
	public int getIntrinsicWidth() {
		return arState.drawable.getIntrinsicWidth();
	}

	@Override
	public int getIntrinsicHeight() {
		return arState.drawable.getIntrinsicHeight();
	}

	@Override
	public ConstantState getConstantState() {
		if (arState.canConstantState()) {
			arState.changingConfigurations = getChangingConfigurations();
			return arState;
		}
		return null;
	}

	public void setFramesCount(int framesCount) {
		arState.framesCount = framesCount;
		increment = 360.0f / arState.framesCount;
	}

	public void setFramesDuration(int framesDuration) {
		arState.frameDuration = framesDuration;
	}

	@Override
	public Drawable mutate() {
		if (!isMutated && super.mutate() == this) {
			arState.drawable.mutate();
			isMutated = true;
		}
		return this;
	}

	static class AnimatedRotateState extends Drawable.ConstantState {

		Drawable drawable;
		int changingConfigurations;

		boolean xRelative = true;
		float pivotX = 0.5f;
		boolean yRelative = true;
		float pivotY = 0.5f;

		int frameDuration;
		int framesCount = 100;

		private boolean canConstantState;
		private boolean checkedConstantState;        

		public AnimatedRotateState(AnimatedRotateState source, AnimatedRotateDrawable owner) {
			if (source == null) 
				return;
			drawable.setCallback(owner);
			xRelative = source.xRelative;
			pivotX = source.pivotX;
			yRelative = source.yRelative;
			pivotY = source.pivotY;
			framesCount = source.framesCount;
			frameDuration = source.frameDuration;
			canConstantState = checkedConstantState = true;
		}

		@Override
		public Drawable newDrawable() {
			return new AnimatedRotateDrawable(this,drawable);
		}

		@Override
		public int getChangingConfigurations() {
			return changingConfigurations;
		}

		public boolean canConstantState() {
			if (!checkedConstantState) {
				canConstantState = drawable.getConstantState() != null;
				checkedConstantState = true;
			}
			return canConstantState;
		}
	}
}
