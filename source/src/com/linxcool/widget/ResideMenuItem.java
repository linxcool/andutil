package com.linxcool.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 侧边导航选项
 * <p><b>Time:</b> 2013-12-27
 * @author 胡昌海(Linxcool.Hu)
 */
public class ResideMenuItem extends LinearLayout{

	/** menu item  icon  */
	private ImageView iv_icon;
	/** menu item  title */
	private TextView tv_title;

	public ResideMenuItem(Context context) {
		super(context);
		initViews(context);
	}

	public ResideMenuItem(Context context, int icon, int title) {
		super(context);
		initViews(context);
		iv_icon.setImageResource(icon);
		tv_title.setText(title);
	}

	public ResideMenuItem(Context context, int icon, String title) {
		super(context);
		initViews(context);
		iv_icon.setImageResource(icon);
		tv_title.setText(title);
	}

	private void initViews(Context context){
		setOrientation(HORIZONTAL);
		setGravity(Gravity.CENTER_VERTICAL);
		setPadding(0, dp2px(30), 0, 0);

		iv_icon = new ImageView(context);
		iv_icon.setScaleType(ScaleType.CENTER_CROP);
		LayoutParams params = new LayoutParams(dp2px(30), dp2px(30));
		addView(iv_icon,params);

		tv_title = new TextView(context);
		tv_title.setTextColor(Color.WHITE);
		tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		params = new LayoutParams(-1, -2);
		params.leftMargin = dp2px(10);
		addView(tv_title,params);
	}

	public int dp2px(int dp){
		float density = getResources().getDisplayMetrics().density;
		return (int) (dp*density + 0.5f);
	}

	/**
	 * set the icon color;
	 *
	 * @param icon
	 */
	 public void setIcon(int icon){
		 iv_icon.setImageResource(icon);
	 }

	 /**
	  * set the title with resource
	  * ;
	  * @param title
	  */
	 public void setTitle(int title){
		 tv_title.setText(title);
	 }

	 /**
	  * set the title with string;
	  *
	  * @param title
	  */
	 public void setTitle(String title){
		 tv_title.setText(title);
	 }
}
