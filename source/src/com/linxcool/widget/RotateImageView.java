package com.linxcool.widget;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * 支持缩放及位移的图片视图
 * <p><b>Time:</b> 2013-9-29
 * @author 胡昌海(linxcool.hu)
 */
public class RotateImageView extends ImageView implements Callback{

	private static final int MSG_BEGIN_SCALE_IMG = 1;
	private static final int MSG_REPEAT_SCALE_IMG = 2;
	private static final int MSG_END_SCALE_IMG = 3;
	private static final int MSG_BEGIN_ROTATE_IMG = 4;
	private static final int MSG_REPEAT_ROTATE_IMG = 5;
	private static final int MSG_END_ROTATE_IMG = 6;
	
	boolean isInited;
	DrawFilter drawFilter;

	int vWidth;
	int vHeight;

	Camera camera;
	Handler handler;

	boolean onAnim;
	boolean isXGY;
	float rotateX;
	float rotateY;
	boolean isScale;
	boolean isActionMove;
	
	float minScale = 0.95f;
	float scale;
	int scaleCount;
	Matrix matrix;
	
	private OnClickListener onClickListener;
	
	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public RotateImageView(Context context) {
		super(context);
		onCreate();
	}

	public RotateImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		onCreate();
	}

	public RotateImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		onCreate();
	}

	void onCreate(){
		camera = new Camera();
		handler = new Handler(this);
		matrix = new Matrix();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(!isInited){
			init();
			isInited = true;
		}
		canvas.setDrawFilter(drawFilter);
	}

	protected void init(){
		drawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		vWidth = getWidth() - getPaddingLeft() - getPaddingRight();
		vHeight = getHeight() - getPaddingTop() - getPaddingBottom();
		BitmapDrawable drawable = (BitmapDrawable) getDrawable();
		drawable.setAntiAlias(true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			float x = event.getX();
			float y = event.getY();
			
			rotateX = (vWidth / 2 - x)/vWidth;
			rotateY = (vHeight / 2 - y)/vHeight;
			
			isXGY = Math.abs(rotateX) > Math.abs(rotateY) ;

			isScale = x > vWidth / 3 && x < vWidth * 2 / 3 && y > vHeight / 3 && y < vHeight * 2 / 3;
			isActionMove = false;

			if (isScale) handler.sendEmptyMessage(MSG_BEGIN_SCALE_IMG);
			else handler.sendEmptyMessage(MSG_BEGIN_ROTATE_IMG);
			
			break;
		case MotionEvent.ACTION_MOVE:
			x = event.getX();
			y = event.getY();
			
			if (x > vWidth || y > vHeight || x < 0 || y < 0) {
				isActionMove = true;
			} else {
				isActionMove = false;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (isScale) handler.sendEmptyMessage(MSG_END_SCALE_IMG);
			else handler.sendEmptyMessage(MSG_END_ROTATE_IMG);
			
			break;
		}
		
		return true;
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		matrix.set(getImageMatrix());
		switch (msg.what) {
		case MSG_BEGIN_SCALE_IMG:
			if(onAnim){
				return false;
			}
			else{
				onAnim = true;
				scaleCount = 0;
				scale = (float) Math.sqrt(Math.sqrt(minScale));
				beginScale(matrix, scale);
				handler.sendEmptyMessage(MSG_REPEAT_SCALE_IMG);
			}
			break;
		case MSG_REPEAT_SCALE_IMG:
			beginScale(matrix, scale);
			scaleCount++;
			if(scaleCount < 3){
				msg = handler.obtainMessage(MSG_REPEAT_SCALE_IMG, msg.arg1, 0);
				msg.sendToTarget();
			}
			else{
				onAnim = false;
				if(!isActionMove && onClickListener != null && msg.arg1<0){
					onClickListener.onClick(this);
				}
			}
			break;
		case MSG_END_SCALE_IMG:
			if (onAnim) {
				handler.sendEmptyMessage(MSG_END_SCALE_IMG);
			} 
			else {
				onAnim = true;
				scaleCount = 0;
				scale = (float) Math.sqrt(Math.sqrt(1.0f / minScale));
				beginScale(matrix, scale);
				msg = handler.obtainMessage(MSG_REPEAT_SCALE_IMG, -2, 0);
				msg.sendToTarget();
			}
			break;
			
		case MSG_BEGIN_ROTATE_IMG:
			if(onAnim){
				return false;
			}
			else{
				onAnim = true;
				scaleCount = 0;
				beginRotate(matrix, (isXGY ? scaleCount : 0), (isXGY ? 0 : scaleCount));
				msg = handler.obtainMessage(MSG_REPEAT_ROTATE_IMG, 2, 0);
				msg.sendToTarget();
			}
			break;
		case MSG_REPEAT_ROTATE_IMG:
			beginRotate(matrix, (isXGY ? scaleCount : 0), (isXGY ? 0 : scaleCount));
			int count = msg.arg1;
			scaleCount += count;
			if ((count>0 && scaleCount<=10) || (count<0 && scaleCount>=0)) {
				msg = handler.obtainMessage(MSG_REPEAT_ROTATE_IMG,count, 0);
				msg.sendToTarget();
			} 
			else {
				onAnim = false;
				if(!isActionMove && onClickListener != null && count<0){
					onClickListener.onClick(this);
				}
			}
			break;
		case MSG_END_ROTATE_IMG:
			if (onAnim) {
				handler.sendEmptyMessage(MSG_END_ROTATE_IMG);
			} 
			else{
				onAnim = true;
				scaleCount = 10;
				beginRotate(matrix, (isXGY ? scaleCount : 0), (isXGY ? 0 : scaleCount));
				msg = handler.obtainMessage(MSG_REPEAT_ROTATE_IMG, -2, 0);
				msg.sendToTarget();
			}
			break;
		}
		return false;
	}
	
	synchronized void beginScale(Matrix matrix, float scale) {
		int scaleX = (int) (vWidth * 0.5f);
		int scaleY = (int) (vHeight * 0.5f);
		matrix.postScale(scale, scale, scaleX, scaleY);
		setImageMatrix(matrix);
	}
	
	synchronized void beginRotate(Matrix matrix, float dX,float dY) {
		int scaleX = (int) (vWidth * 0.5f);
		int scaleY = (int) (vHeight * 0.5f);

		/* 修改幅度，根据需求决定是否注释... */
		dX /= 2;
		dY /= 2;
		if(vWidth > vHeight)
			dX /= vWidth/vHeight;
		else if(vWidth < vHeight)
			dY /= vHeight/vWidth;
		/* ............................... */
		
		camera.save();
		camera.rotateX(rotateY > 0 ? dY : -dY);
		camera.rotateY(rotateX < 0 ? dX : -dX);
		camera.getMatrix(matrix);
		camera.restore();
		// 控制中心点
		if (rotateX > 0 && dX != 0) {
			matrix.preTranslate(-vWidth, -scaleY);
			matrix.postTranslate(vWidth, scaleY);
		} else if (rotateY > 0 && dY != 0) {
			matrix.preTranslate(-scaleX, -vHeight);
			matrix.postTranslate(scaleX, vHeight);
		} else if (rotateX < 0 && dX != 0) {
			matrix.preTranslate(-0, -scaleY);
			matrix.postTranslate(0, scaleY);
		} else if (rotateY < 0 && dY != 0) {
			matrix.preTranslate(-scaleX, -0);
			matrix.postTranslate(scaleX, 0);
		}
		setImageMatrix(matrix);
	}
}
