package com.linxcool.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * 支持嵌套横向滚动的ScrollView
 * <p>保证滚动流畅
 * @author: 胡昌海(linxcool.hu)
 */
public class CustomScrollView extends ScrollView {
	
	class YScrollDetector extends SimpleOnGestureListener { 
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, 
        		float distanceX, float distanceY) { 
            if(Math.abs(distanceY) > Math.abs(distanceX)) 
                return true; 
            return false; 
        } 
    } 
	
	private GestureDetector mGestureDetector;   
	
	public CustomScrollView(Context context) {
		super(context);
		init(context);
	}

	public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public CustomScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) { 
        return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev); 
    } 
  
	private void init(Context context){
		mGestureDetector = new GestureDetector(context, new YScrollDetector());
        setFadingEdgeLength(0); 
	}
}
