package com.linxcool.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.TranslateAnimation;
import android.widget.ViewFlipper;

/**
 * 支持手势的ViewFlipper视图
 * <p><b>Time:</b> 2013-12-27
 * @author 胡昌海(Linxcool.Hu)
 */
public class SlideView extends ViewFlipper implements GestureDetector.OnGestureListener,Callback{

	public interface OnFlingListener{
		public void onFling(int flingTo);
	}
	
	static final int SENSITIVITY = 5;
	static final int SPEED = 200;

	GestureDetector detector;

	TranslateAnimation toLeftIn;
	TranslateAnimation toLeftOut;
	TranslateAnimation toRightIn;
	TranslateAnimation toRightOut;

	Handler handler;
	
	OnFlingListener onFlingListener;
	
	public OnFlingListener getOnFlingListener() {
		return onFlingListener;
	}

	public void setOnFlingListener(OnFlingListener onFlingListener) {
		this.onFlingListener = onFlingListener;
	}

	public SlideView(Context context) {
		super(context);
		init(context);
	}

	public SlideView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	void init(Context context){
		handler = new Handler(this);
		handler.sendEmptyMessage(0);
		//type = Animation.RELATIVE_TO_SELF
		toLeftIn = new TranslateAnimation(0x1, 1, 0x1, 0, 0x1, 0, 0x1, 0);
		toLeftIn.setDuration(SPEED);

		toLeftOut = new TranslateAnimation(0x1, 0, 0x1, -1, 0x1, 0, 0x1, 0);
		toLeftOut.setDuration(SPEED);

		toRightIn = new TranslateAnimation(0x1, -1, 0x1, 0, 0x1, 0, 0x1, 0);
		toRightIn.setDuration(SPEED);

		toRightOut = new TranslateAnimation(0x1, 0, 0x1, 1, 0x1, 0, 0x1, 0);
		toRightOut.setDuration(SPEED);
	}

	@Override
	public boolean handleMessage(Message msg) {
		detector = new GestureDetector(getContext(),this);
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {
		float dx = e1.getX() - e2.getX();
		float dy=e1.getY() - e2.getY();
		if(Math.abs(dy)>Math.abs(dx)) 
			return true;
		if(Math.abs(dx)<SENSITIVITY)
			return true;
		if(dx > 0)
			onFlingLeft();
		else
			onFlingRight();
		return true;
	}

	public void setToLeftAnimation(){
		setInAnimation(toLeftIn);
		setOutAnimation(toLeftOut);
	}
	
	void onFlingLeft(){
		if(getDisplayedChild() == getChildCount()-1)
			return;
		setToLeftAnimation();
		showNext();
	}

	public void setToRightAnimation(){
		setInAnimation(toRightIn);
		setOutAnimation(toRightOut);
	}
	
	void onFlingRight(){
		if(getDisplayedChild() == 0)
			return;
		setToRightAnimation();
		showPrevious();
	}

	@Override
	public void setDisplayedChild(int whichChild) {
		int currentChild = getDisplayedChild();
		if(currentChild == whichChild)
			return;
		if(currentChild < whichChild)
			setToLeftAnimation();
		else 
			setToRightAnimation();
		super.setDisplayedChild(whichChild);
		if(onFlingListener != null){
			onFlingListener.onFling(whichChild);
		}
	}
	
	@Override
	protected void onDetachedFromWindow() {
		try { 
			super.onDetachedFromWindow(); 
		}catch (IllegalArgumentException e) {
			stopFlipping(); 
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(detector == null)
			return super.onTouchEvent(event);
		return detector.onTouchEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		this.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}
}
