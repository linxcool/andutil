package com.linxcool.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ListView;

/**
 * 下拉时具有弹性的ListView
 * <p><b>Time:</b> 2014-1-2
 * @author 胡昌海(Linxcool.Hu)
 */
public class SpringbackListView extends ListView {

	private static final int MAX_OVERSCROLL = 50;   
	private static int maxOverscrollY;

	public SpringbackListView(Context context) {
		super(context);
		init(context);
	}

	public SpringbackListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context){
		setFadingEdgeLength(0);
		setFastScrollEnabled(false);
		final DisplayMetrics metrics = getResources().getDisplayMetrics();  
		maxOverscrollY = (int) (metrics.density * MAX_OVERSCROLL);  
	}   
	@Override  
	protected boolean overScrollBy(int deltaX, int deltaY, 
			int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, 
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent){
		return super.overScrollBy(deltaX, deltaY, 
				scrollX, scrollY, scrollRangeX, scrollRangeY, 
				maxOverScrollX, maxOverscrollY, isTouchEvent); 
	}
}