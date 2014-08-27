package com.linxcool.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * 下拉自动加载的 Listview 
 * <P><STRONG>Time：</STRONG>2013-6-10 下午5:18:02</P>
 * @author 胡昌海(linxcool.hu)
 */
public abstract class LoaderListView extends ListView implements 
OnScrollListener, OnItemClickListener,OnClickListener {

	protected boolean onLoading;

	public enum FootViewType {
		NONE,LOADING,TOTOP,RETRY
	}

	private LinearLayout onLoadingView;
	private LinearLayout loadingFailedView;
	private LinearLayout  backTopView;
	private View footView;

	private int scrollState;

	public int getScrollState() {
		return scrollState;
	}

	public LoaderListView(Context context) {
		super(context);
		init(context);
	}

	public LoaderListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public LoaderListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		onLoadingView = crateFootView(context, "加载中...",true);

		loadingFailedView = crateFootView(context, "加载失败，点击重试！",false);
		loadingFailedView.setOnClickListener(this);

		backTopView = crateFootView(context, "返回顶部！",false);
		backTopView.setOnClickListener(this);

		setFootviewType(FootViewType.LOADING);

		setOnScrollListener(this);
		scrollState = SCROLL_STATE_IDLE;

		setOnItemClickListener(this);

		setCacheColorHint(Color.TRANSPARENT);
	}

	private LinearLayout crateFootView(
			Context context,String text,boolean needProgress){
		LinearLayout footView = new LinearLayout(context);
		footView.setOrientation(LinearLayout.HORIZONTAL);
		footView.setGravity(Gravity.CENTER);
		footView.setBackgroundColor(Color.parseColor("#eaeaea"));
		LayoutParams params=new LayoutParams(-1,getFixPx(40));
		footView.setLayoutParams(params);
		if(needProgress){
			ProgressBar bar = new ProgressBar(context);
			LinearLayout.LayoutParams pbParams = new LinearLayout.LayoutParams(getFixPx(30),getFixPx(30));
			pbParams.rightMargin = getFixPx(10);
			footView.addView(bar,pbParams);
		}
		TextView textView = new TextView(context);
		textView.setText(text);
		textView.setTextColor(Color.parseColor("#000000"));
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12);
		footView.addView(textView);
		return footView;
	}

	@Override
	public void onClick(View v) {
		if(v==backTopView) setSelection(0);
		else if(v==loadingFailedView){
			setFootviewType(FootViewType.LOADING);
			load();
		}
	}

	public void setFootviewType(FootViewType type) {
		if(footView!=null && footView.getTag()==type)
			return;
		if(footView != null)
			removeFooterView(footView);

		switch (type) {
		case LOADING:footView = onLoadingView;break;
		case TOTOP:footView = backTopView;break;
		case RETRY:footView = loadingFailedView;break;
		case NONE:return;
		}

		addFooterView(footView);
		footView.setTag(type);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		//Empty
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState != this.scrollState) {
			onScrollStateChanged(this.scrollState, scrollState);
			this.scrollState = scrollState;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if(footView != backTopView 
				&& firstVisibleItem+visibleItemCount >= totalItemCount)
			load();
	}

	public int getFixPx(int dp){
		float scale=getContext().getResources().getDisplayMetrics().density;
		return (int)(scale*dp+0.5);
	}

	public abstract void load();

	public void onScrollStateChanged(int oldState, int newState){
		switch (newState) {  
		case SCROLL_STATE_FLING:  

			break;  
		case SCROLL_STATE_IDLE:  

			break;  
		case SCROLL_STATE_TOUCH_SCROLL:  

			break;  
		}
	}
}
