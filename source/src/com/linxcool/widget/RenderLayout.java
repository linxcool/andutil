package com.linxcool.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 渲染布局
 * <p><b>Time:</b> 2013-12-27
 * @author 胡昌海(Linxcool.Hu)
 */
public class RenderLayout extends RelativeLayout implements Callback,Animation.AnimationListener{

	class Circle extends View{
		Paint paint;
		float cx;
		float cy;
		float radius;
		public Circle(Context context) {
			super(context);
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(Color.WHITE);
			radius = dp2px(1);
			cx = (float) Math.sqrt(radius*radius/2);
			cy = cx;
		}
		
		void drawBase(Canvas canvas){
			super.draw(canvas);
		}
		
		@Override
		public void draw(Canvas canvas) {
			drawBase(canvas);
			canvas.drawCircle(cx, cy, radius, paint);
		}

		float dp2px(int dp){
			float density = getResources().getDisplayMetrics().density;
			return dp*density + 0.5f;
		}
	}

	class RenderAnimation extends TranslateAnimation implements Animation.AnimationListener{

		private View v;
		
		private float fromXDelta;
		private float toXDelta;
		private float fromYDelta;
		private float toYDelta;
		
		private long duration;
		
		private AnimationListener listener;
		
		public RenderAnimation(
				float fromXDelta, float toXDelta,float fromYDelta, float toYDelta) {
			super(
					fromXDelta, fromXDelta+(toXDelta-fromXDelta)/2, 
					fromYDelta, fromYDelta+(toYDelta-fromYDelta)/2
			);
			
			this.fromXDelta = fromXDelta+(toXDelta-fromXDelta)/2;
			this.toXDelta = toXDelta;
			this.fromYDelta = fromYDelta+(toYDelta-fromYDelta)/2;
			this.toYDelta = toYDelta;

			setFillAfter(true);
			setInterpolator(new DecelerateInterpolator());
			super.setAnimationListener(this);
		}

		@Override
		public void setAnimationListener(AnimationListener listener) {
			this.listener = listener;
		}
		
		public void start(View v){
			this.v = v;
			duration = getDuration()/2;
			setDuration(duration);
			v.startAnimation(this);
		}

		@Override
		public void onAnimationStart(Animation animation) {
			//Empty
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			TranslateAnimation anim = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
			anim.setInterpolator(new AccelerateInterpolator());
			anim.setDuration(duration);
			anim.setAnimationListener(listener);
			v.startAnimation(anim);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			//Empty
		}
	}

	private Handler handler;
	private Circle[] circles;
	private int count = 7;
	
	private LinearLayout container;
	
	public RenderLayout(Context context) {
		super(context);
		init(context);
	}

	public RenderLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public RenderLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	@Override
	public void addView(View child) {
		container.addView(child);
	}

	@Override
	public void addView(View child, android.view.ViewGroup.LayoutParams params) {
		container.addView(child, params);
	}
	
	void init(Context context){
		setEnabled(false);
		//修改容器
		//Drawable bg = getBackground();
		container = new LinearLayout(context);
		//container.setBackgroundDrawable(bg);
		container.setVisibility(View.INVISIBLE);
		//setBackgroundColor(Color.TRANSPARENT);
		super.addView(container,new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		//渲染视图
		circles = new Circle[count];
		for (int i = 0; i < count; i++) {
			circles[i] = new Circle(context);
			LayoutParams params = new LayoutParams(-2, -2);
			params.leftMargin = -(int) ((i+1)*circles[i].dp2px(5));
			super.addView(circles[i],params);
		}
		handler = new Handler(this);
		handler.sendEmptyMessageDelayed(0, 100);
	}

	@Override
	public boolean handleMessage(Message msg) {
		if(getWidth() == 0)
			handler.sendEmptyMessageDelayed(0, 100);
		else{
			RenderAnimation anim = new RenderAnimation(0, getWidth(), getHeight()/2, getHeight()/2);
			anim.setDuration(1800);
			
			int i = msg.arg1<0?0:msg.arg1;
			if(i < count-1){
				msg = handler.obtainMessage(0, i+1, 0);
				handler.sendMessageDelayed(msg, 120);
			}
			else anim.setAnimationListener(this);
			anim.start(circles[i++]);
		}
		return false;
	}

	@Override
	public void onAnimationStart(Animation animation) {
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		setEnabled(true);
		container.setVisibility(View.VISIBLE);
		AlphaAnimation anim = new AlphaAnimation(0, 1);
		anim.setDuration(1000);
		anim.setFillAfter(true);
		container.startAnimation(anim);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		
	}
}
