package com.linxcool.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SlipButton extends View implements View.OnTouchListener{

	private boolean selected;

	private Bitmap bgOn;
	private Bitmap bgOff;
	private Bitmap bgSlip;

	private Matrix matrix;
	private Paint paint;

	private float x,dx;
	private int maxSlipRight;
	private int midSlip;
	private int minSlipLeft;

	private boolean onSlip;

	public SlipButton(Context context) {
		super(context);
		init(context);
	}

	public SlipButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SlipButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	void init(Context context){
		setOnTouchListener(this);
		matrix = new Matrix();
		paint = new Paint();
	}
	
	public void setResouce(Bitmap bgOn,Bitmap bgOff,Bitmap bgSlip){
		this.bgOn = bgOn;
		this.bgOff = bgOff;
		this.bgSlip = bgSlip;
		maxSlipRight = bgOn.getWidth() - bgSlip.getWidth()/2;
		minSlipLeft = bgSlip.getWidth()/2;
		midSlip = bgOn.getWidth()/2;
	}
	
	public void setResouce(int bgOn,int bgOff,int bgSlip){
		setResouce(
				BitmapFactory.decodeResource(getResources(), bgOn), 
				BitmapFactory.decodeResource(getResources(), bgOff), 
				BitmapFactory.decodeResource(getResources(), bgSlip));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(bgOn==null || bgOff==null || bgSlip==null)
			return;
		
		if( x < midSlip) canvas.drawBitmap(bgOff, matrix, paint);
		else canvas.drawBitmap(bgOn, matrix, paint);

		if(onSlip){
			if(x > maxSlipRight) dx = bgOn.getWidth() - bgSlip.getWidth();
			else if(x < minSlipLeft) dx = 0;
			else dx = x - bgSlip.getWidth()/2;
		}
		else{
			if(selected) dx = bgOn.getWidth() - bgSlip.getWidth();
			else dx = 0;
		}
		
		canvas.drawBitmap(bgSlip, dx, 0, paint);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(!isEnabled())
			return false;
		if(bgOn==null || bgOff==null || bgSlip==null)
			return false;

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if(event.getX() > bgOn.getWidth())
				return false;
			if(event.getY() > bgOn.getHeight())
				return false;
			onSlip = true;
			x = event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			x = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			onSlip = false;
			x = event.getX();
			if(x > bgOn.getWidth()/2)selected = true;
			else selected = false;
			break;
		}
		// 重画控件
		invalidate();
		return true;
	}

}
