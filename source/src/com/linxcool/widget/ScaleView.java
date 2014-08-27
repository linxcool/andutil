package com.linxcool.widget;

import android.content.Context;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * 缩放图片视图
 * <p><b>Time:</b> 2013-11-12
 * @author 胡昌海(linxcool.hu)
 */
public class ScaleView extends ImageView {
	/**
	 * 缩放事件监听
	 * <p><b>Time:</b> 2013-11-12
	 * @author 胡昌海(linxcool.hu)
	 */
	public interface OnZoomListener {
		public void doZoom(int w, int h);
	};

	/**
	 * 拖拽事件监听
	 * <p><b>Time:</b> 2013-11-12
	 * @author 胡昌海(linxcool.hu)
	 */
	public interface OnFlingListener {
		public void doFling(int direction);
	};

	private static final int TRANS_DURATION = 400;

	private static final int NONE 	 = 0; // 没有状态
	private static final int DRAG 	 = 1; // 移动状态
	private static final int ZOOM 	 = 2; // 缩放状态
	private static final int BIGGER  = 3; // 放大图片
	private static final int SMALLER = 4; // 缩小图片
	// 当前状态
	private int mode = NONE; 	 
	// 缩放因子
	private float scale = 0.04f; 
	// 第一次触摸两点的距离 
	private float beforeLenght;  
	// 图片移动范围
	private int areaW;
	private int areaH;
	// 开始触摸点
	private int start_x;
	private int start_y;
	// 结束触摸点
	private int stop_x;
	private int stop_y;
	// 回弹动画
	private TranslateAnimation trans; 

	// 单点按下时的坐标与起来时的坐标
	float moveX, moveY, toX, toY;
	// 是否处于缩放（指放大）状态
	boolean isZoom = false;
	// 是否处于拖拽状态
	boolean isSingle = true;

	private OnZoomListener onZoomListener;
	private OnFlingListener onFlingListener;

	public static final int LEFT = 20;
	public static final int RIGHT = 30;
	public static final int NO = 40;
	int direction = NO;

	float maxWigth = 1800;
	float maxHeight = 2600;

	private ScaleView(Context context) {
		super(context);
	}

	/**
	 * 构造器
	 * @param context
	 * @param w 图片移动的范围长
	 * @param h 图片移动的范围宽
	 */
	public ScaleView(Context context, int w, int h) {
		this(context);

		this.areaW = w;
		this.areaH = h;

		setPadding(0, 0, 0, 0);
		setLongClickable(true);
	}

	public void setOnZoomListener(OnZoomListener onZoomListener) {
		this.onZoomListener = onZoomListener;
	}

	public void setOnFlingListener(OnFlingListener onFlingListener) {
		this.onFlingListener = onFlingListener;
	}

	// 用来计算2个触摸点的距离
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			if (getWidth() > areaW || getHeight() > areaH) {
				mode = DRAG;
			}
			// 相对于屏幕左上角为原点的坐标
			stop_x = (int) event.getRawX();
			stop_y = (int) event.getRawY();

			start_x = stop_x - this.getLeft();
			start_y = stop_y - this.getTop();

			if (event.getPointerCount() == 2) {
				beforeLenght = spacing(event);
			}
			// 获取此时坐标
			moveX = event.getX();
			moveY = event.getY();
			break;

		case MotionEvent.ACTION_POINTER_DOWN:
			if (spacing(event) > 10f) {
				mode = ZOOM;
				beforeLenght = spacing(event);
			}
			isSingle = false;
			break;
		case MotionEvent.ACTION_UP:
			if (getWidth() < areaW) {
				setPosition(
						(areaW - getWidth()) / 2,
						(areaH - getHeight()) / 2,
						(areaW - getWidth()) / 2 + getWidth(),
						(areaH - getHeight()) / 2 + getHeight()
				);
			}
			// 获取此时坐标
			toX = event.getX();
			toY = event.getY();

			// 是否拖拽
			if (event.getPointerCount() == 1 
					&& isSingle 
					&& !isZoom 
					&& Math.abs(moveX - toX) > 60) {
				if (moveX - toX > 0) direction = LEFT;
				else direction = RIGHT;
				if(onFlingListener != null) onFlingListener.doFling(direction);
			}
			isSingle = true;
			direction = NO;

			int disX = 0;
			int disY = 0;
			if (getHeight() <= areaH) {
				if (getTop() < 0) {
					disY = getTop();
					setPosition(getLeft(), 0, getRight(),0 + getHeight());
				}
				else if (getBottom() >= areaH) {
					disY = getTop() + getHeight() - areaH;
					setPosition(getLeft(), areaH - getHeight(),getRight(), areaH);
				}
			} 
			else {
				int y1 = getTop();
				int y2 = getHeight() - areaH + getTop();
				if (y1 > 0) {
					disY = y1;
					setPosition(getLeft(), 0, getRight(),0 + getHeight());
				} 
				else if (y2 < 0) {
					disY = y2;
					setPosition(getLeft(), areaH - getHeight(),getRight(), areaH);
				}
			}

			if (getWidth() <= areaW) {
				if (getLeft() < 0) {
					disX = getLeft();
					setPosition(0, getTop(), 0 + getWidth(),getBottom());
				} 
				else if (getRight() > areaW) {
					disX = getWidth() - areaW + getLeft();
					setPosition(areaW - getWidth(), getTop(), areaW,getBottom());
				}
			} 
			else {
				int X1 = getLeft();
				int X2 = getWidth() - areaW + getLeft();
				if (X1 > 0) {
					disX = X1;
					setPosition(0, getTop(), 0 + getWidth(),getBottom());
				} 
				else if (X2 < 0) {
					disX = X2;
					setPosition(areaW - getWidth(), getTop(), areaW,getBottom());
				}
			}

			// 如果图片缩放到宽高任意一个小于200，那么自动放大，直到大于200.
			while (getWidth() < areaW - 2 * (areaW * 0.02)) {
				setScale(scale, BIGGER, 0);
			}
			// 根据disX和disY的偏移量采用移动动画回弹归位，动画时间为400毫秒。
			if (disX != 0 || disY != 0) {
				trans = new TranslateAnimation(disX, 0, disY, 0);
				trans.setDuration(TRANS_DURATION);
				startAnimation(trans);
			}
			mode = NONE;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				// 执行拖动事件的时，不断变换自定义imageView的位置从而达到拖动效果
				if (getTop() < 0 || getBottom() > areaH) {
					setPosition(
							stop_x - start_x, 
							stop_y - start_y, 
							stop_x + getWidth() - start_x, 
							stop_y - start_y+ getHeight()
					);
				} 
				else {
					setPosition(
							stop_x - start_x, 
							getTop(), 
							stop_x + getWidth() - start_x, 
							getBottom()
					);
				}
				stop_x = (int) event.getRawX();
				stop_y = (int) event.getRawY();

			} 
			else if (mode == ZOOM) {
				if (spacing(event) > 10f) {
					float afterLenght = spacing(event);
					float gapLenght = afterLenght - beforeLenght;
					if (gapLenght == 0) {
						break;
					}
					// 图片宽度（也就是自定义imageView）必须大于70才可以缩放
					else if (Math.abs(gapLenght) > 5f && getWidth() > 70) {
						if (gapLenght > 0) 
							setScale(scale, BIGGER, 1);
						else
							setScale(scale, SMALLER, 1);
						beforeLenght = afterLenght;
					}
				}
			}
			break;
		}
		return true;
	}

	private void setScale(float temp, int flag, int zoom) {
		if (flag == BIGGER) {
			if (getWidth() > maxWigth || getHeight() > maxHeight) {
				// Empty
			} 
			else {
				// setFrame(left , top, right,bottom)函数表示改变当前view的框架，也就是大小。
				setFrame(
						getLeft() - (int) (temp * getWidth()),
						getTop() - (int) (temp * getHeight()),
						getRight() + (int) (temp * getWidth()),
						getBottom() + (int) (temp * getHeight())
				);
				setPosition(
						(areaW - getWidth()) / 2,
						(areaH - getHeight()) / 2,
						(areaW - getWidth()) / 2 + getWidth(),
						(areaH - getHeight()) / 2 + getHeight()
				);
			}
		}
		else if (flag == SMALLER) {
			setFrame(
					getLeft() + (int) (temp * getWidth()),
					getTop() + (int) (temp * getHeight()),
					getRight() - (int) (temp * getWidth()),
					getBottom() - (int) (temp * getHeight())
			);
		}

		if (mode == ZOOM && onZoomListener != null) 
			onZoomListener.doZoom(getWidth(), getHeight());
		if (zoom != 0) 
			isZoom = getWidth() > areaW;
	}

	private void setPosition(int left, int top, int right, int bottom) {
		layout(left, top, right, bottom);
	}

}