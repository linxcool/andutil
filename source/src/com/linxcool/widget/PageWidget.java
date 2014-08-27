package com.linxcool.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

/**
 * 翻书效果控件
 * <p><b>Time:</b> 2013-11-15
 * @author 胡昌海(linxcool.hu)
 */
public class PageWidget extends View implements View.OnTouchListener,Callback{

	/**
	 * 可翻页接口
	 * <p><b>Time:</b> 2013-11-18
	 * @author 胡昌海(linxcool.hu)
	 */
	public interface Pageable{
		/** 是否为第一页 */
		public boolean isFirstPage();

		/** 是否为最后一页 */
		public boolean isLastPage();

		/** 前一页 */
		public void onPrePage();

		/** 后一页 */
		public void onNextPage();

		/** 绘制接口  */
		public void doDraw(Canvas canvas);
	}

	boolean dragToRight;

	private int width;
	private int height;

	// 拖拽点对应的页脚
	private int cornerX; 
	private int cornerY;

	private Path path0;
	private Path path1;

	// 当前页
	Bitmap curPage; 
	Bitmap nextPage;

	// 拖拽点
	PointF touch = new PointF();

	// 贝塞尔曲线起始点
	PointF bezierStart1 = new PointF(); 
	// 贝塞尔曲线控制点
	PointF bezierControl1 = new PointF(); 
	// 贝塞尔曲线顶点
	PointF bezierVertex1 = new PointF();
	// 贝塞尔曲线结束点
	PointF bezierEnd1 = new PointF();
	// 另一条贝塞尔曲线
	PointF bezierStart2 = new PointF(); 
	PointF bezierControl2 = new PointF();
	PointF bezierVertex2 = new PointF();
	PointF bezierEnd2 = new PointF();

	float middleX;
	float middleY;
	float degrees;
	float touchToCornerDis;

	ColorMatrixColorFilter colorMatrixFilter;
	Matrix matrix;
	float[] matrixArray = { 0, 0, 0, 0, 0, 0, 0, 0, 1.0f };

	// 是否属于右上左下
	boolean isRTandLB; 

	float maxLength;
	int[] backShadowColors;
	int[] frontShadowColors;

	GradientDrawable backShadowDrawableLR;
	GradientDrawable backShadowDrawableRL;
	GradientDrawable folderShadowDrawableLR;
	GradientDrawable folderShadowDrawableRL;

	GradientDrawable frontShadowDrawableHBT;
	GradientDrawable frontShadowDrawableHTB;
	GradientDrawable frontShadowDrawableVLR;
	GradientDrawable frontShadowDrawableVRL;

	Paint paint;

	Scroller scroller;

	Pageable pageable;
	Canvas curCanvas;
	Canvas nextCanvas;

	Handler handler;
	boolean onPlay;
	int autoDelayed;
	boolean onTouch;
	
	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}

	public PageWidget(Context context) {
		super(context);

		path0 = new Path();
		path1 = new Path();
		createDrawable();

		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);

		ColorMatrix cm = new ColorMatrix();
		float array[] = { 
				0.55f, 0, 0, 0, 80.0f, 0, 0.55f, 0, 0, 80.0f, 0, 0,
				0.55f, 0, 80.0f, 0, 0, 0, 0.2f, 0 
		};
		cm.set(array);
		colorMatrixFilter = new ColorMatrixColorFilter(cm);
		matrix = new Matrix();
		scroller = new Scroller(getContext());

		// 不让x,y为0,否则在点计算时会有问题
		touch.x = 0.01f;
		touch.y = 0.01f;

		setOnTouchListener(this);

		handler = new Handler(this);
	}

	private void setSize(int width,int height){
		this.width = width;
		this.height = height;
		maxLength = (float) Math.hypot(width, height);

		if(width == 0 || height == 0)
			return;
		if(curCanvas != null || nextCanvas != null)
			return;

		curPage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		nextPage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		curCanvas = new Canvas(curPage);
		nextCanvas = new Canvas(nextPage);

		if(pageable != null) pageable.doDraw(curCanvas);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		setSize(getWidth(), getHeight());
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/**
	 * 计算拖拽点对应的拖拽脚
	 * @param x
	 * @param y
	 */
	public void calcCornerXY(float x, float y) {
		/*if (x <= width / 2)
			cornerX = 0;
		else
			cornerX = width;
		if (y <= height / 2)
			cornerY = 0;
		else
			cornerY = height;
		if ((cornerX == 0 && cornerY == height)
				|| (cornerX == width && cornerY == 0))
			isRTandLB = true;
		else
			isRTandLB = false;*/

		dragToRight = x < width / 2 ;
		cornerX = width;

		if (y <= height / 2){
			cornerY = 0;
			isRTandLB = true;
		}
		else{
			cornerY = height;
			isRTandLB = false;
		}
	}

	public boolean doTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			touch.x = event.getX();
			touch.y = event.getY();

			if(isDragToRight()){
				if(isRTandLB) touch.y = 1.0f;
				else touch.y = height - 0.01F;
			}

			this.postInvalidate();
		}
		else if (event.getAction() == MotionEvent.ACTION_DOWN) {
			touch.x = event.getX();
			touch.y = event.getY();

			if(isDragToRight()){
				if(isRTandLB) touch.y = 1.0f;
				else touch.y = height - 0.01F;
			}
			
			onTouch = true;
		}
		else if (event.getAction() == MotionEvent.ACTION_UP) {
			if (canDragOver()) {
				startAnimation(1000);
			} 
			else {
				touch.x = cornerX - 0.09f;
				touch.y = cornerY - 0.09f;
			}
			
			onTouch = false;
			
			this.postInvalidate();
		}
		return true;
	}

	/**
	 * 求解直线P1P2和直线P3P4的交点坐标
	 * @param P1
	 * @param P2
	 * @param P3
	 * @param P4
	 * @return
	 */
	public PointF getCross(PointF P1, PointF P2, PointF P3, PointF P4) {
		PointF CrossP = new PointF();
		// 二元函数通式： y=ax+b
		float a1 = (P2.y - P1.y) / (P2.x - P1.x);
		float b1 = ((P1.x * P2.y) - (P2.x * P1.y)) / (P1.x - P2.x);

		float a2 = (P4.y - P3.y) / (P4.x - P3.x);
		float b2 = ((P3.x * P4.y) - (P4.x * P3.y)) / (P3.x - P4.x);
		CrossP.x = (b2 - b1) / (a1 - a2);
		CrossP.y = a1 * CrossP.x + b1;
		return CrossP;
	}

	private void calcPoints() {
		middleX = (touch.x + cornerX) / 2;
		middleY = (touch.y + cornerY) / 2;
		bezierControl1.x = middleX - (cornerY - middleY) * (cornerY - middleY) / (cornerX - middleX);
		bezierControl1.y = cornerY;
		bezierControl2.x = cornerX;
		bezierControl2.y = middleY - (cornerX - middleX) * (cornerX - middleX) / (cornerY - middleY);

		bezierStart1.x = bezierControl1.x - (cornerX - bezierControl1.x) / 2;
		bezierStart1.y = cornerY;

		// 当bezierStart1.x < 0或者bezierStart1.x > width时
		// 如果继续翻页 会出现BUG 故在此限制
		if (touch.x > 0 && touch.x < width) {
			if (bezierStart1.x < 0 || bezierStart1.x > width) {
				if (bezierStart1.x < 0)
					bezierStart1.x = width - bezierStart1.x;

				float f1 = Math.abs(cornerX - touch.x);
				float f2 = width * f1 / bezierStart1.x;
				touch.x = Math.abs(cornerX - f2);

				float f3 = Math.abs(cornerX - touch.x) * Math.abs(cornerY - touch.y) / f1;
				touch.y = Math.abs(cornerY - f3);

				middleX = (touch.x + cornerX) / 2;
				middleY = (touch.y + cornerY) / 2;

				bezierControl1.x = middleX - (cornerY - middleY) * (cornerY - middleY) / (cornerX - middleX);
				bezierControl1.y = cornerY;

				bezierControl2.x = cornerX;
				bezierControl2.y = middleY - (cornerX - middleX) * (cornerX - middleX) / (cornerY - middleY);
				bezierStart1.x = bezierControl1.x - (cornerX - bezierControl1.x) / 2;
			}
		}
		bezierStart2.x = cornerX;
		bezierStart2.y = bezierControl2.y - (cornerY - bezierControl2.y) / 2;

		touchToCornerDis = (float) Math.hypot((touch.x - cornerX),(touch.y - cornerY));

		bezierEnd1 = getCross(touch, bezierControl1, bezierStart1, bezierStart2);
		bezierEnd2 = getCross(touch, bezierControl2, bezierStart1, bezierStart2);


		/*
		 * beziervertex1.x 推导
		 * ((bezierStart1.x+bezierEnd1.x)/2+bezierControl1.x)/2 化简等价于
		 * (bezierStart1.x+ 2*bezierControl1.x+bezierEnd1.x) / 4
		 */
		bezierVertex1.x = (bezierStart1.x + 2 * bezierControl1.x + bezierEnd1.x) / 4;
		bezierVertex1.y = (2 * bezierControl1.y + bezierStart1.y + bezierEnd1.y) / 4;
		bezierVertex2.x = (bezierStart2.x + 2 * bezierControl2.x + bezierEnd2.x) / 4;
		bezierVertex2.y = (2 * bezierControl2.y + bezierStart2.y + bezierEnd2.y) / 4;
	}

	private void drawCurrentPageArea(Canvas canvas, Bitmap bitmap, Path path) {
		path0.reset();
		path0.moveTo(bezierStart1.x, bezierStart1.y);
		path0.quadTo(bezierControl1.x, bezierControl1.y, bezierEnd1.x, bezierEnd1.y);
		path0.lineTo(touch.x, touch.y);
		path0.lineTo(bezierEnd2.x, bezierEnd2.y);
		path0.quadTo(bezierControl2.x, bezierControl2.y, bezierStart2.x, bezierStart2.y);
		path0.lineTo(cornerX, cornerY);
		path0.close();

		canvas.save();
		canvas.clipPath(path, Region.Op.XOR);
		canvas.drawBitmap(bitmap, 0, 0, null);
		canvas.restore();
	}

	private void drawNextPageAreaAndShadow(Canvas canvas, Bitmap bitmap) {
		path1.reset();
		path1.moveTo(bezierStart1.x, bezierStart1.y);
		path1.lineTo(bezierVertex1.x, bezierVertex1.y);
		path1.lineTo(bezierVertex2.x, bezierVertex2.y);
		path1.lineTo(bezierStart2.x, bezierStart2.y);
		path1.lineTo(cornerX, cornerY);
		path1.close();

		degrees = (float) Math.toDegrees(Math.atan2(bezierControl1.x - cornerX, bezierControl2.y - cornerY));
		int leftx;
		int rightx;
		GradientDrawable backShadowDrawable;
		if (isRTandLB) {
			leftx = (int) (bezierStart1.x);
			rightx = (int) (bezierStart1.x + touchToCornerDis / 4);
			backShadowDrawable = backShadowDrawableLR;
		} else {
			leftx = (int) (bezierStart1.x - touchToCornerDis / 4);
			rightx = (int) bezierStart1.x;
			backShadowDrawable = backShadowDrawableRL;
		}
		canvas.save();
		canvas.clipPath(path0);
		canvas.clipPath(path1, Region.Op.INTERSECT);
		canvas.drawBitmap(bitmap, 0, 0, null);
		canvas.rotate(degrees, bezierStart1.x, bezierStart1.y);
		backShadowDrawable.setBounds(leftx, (int) bezierStart1.y, rightx, (int) (maxLength + bezierStart1.y));
		backShadowDrawable.draw(canvas);
		canvas.restore();
	}

	public void setBitmaps(Bitmap curPage, Bitmap nextPage) {
		this.curPage = curPage;
		this.nextPage = nextPage;
	}

	public void setScreen(int w, int h) {
		width = w;
		height = h;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(0xFFAAAAAA);
		calcPoints();
		drawCurrentPageArea(canvas, curPage, path0);
		drawNextPageAreaAndShadow(canvas, nextPage);
		drawCurrentPageShadow(canvas);
		drawCurrentBackArea(canvas, curPage);
	}

	/**
	 * 创建阴影的GradientDrawable
	 */
	private void createDrawable() {
		int[] color = { 0x333333, 0xb0333333 };
		folderShadowDrawableRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, color);
		folderShadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		folderShadowDrawableLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, color);
		folderShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		backShadowColors = new int[] { 0xff111111, 0x111111 };
		backShadowDrawableRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, backShadowColors);
		backShadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		backShadowDrawableLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, backShadowColors);
		backShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		frontShadowColors = new int[] { 0x80111111, 0x111111 };
		frontShadowDrawableVLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, frontShadowColors);
		frontShadowDrawableVLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		frontShadowDrawableVRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, frontShadowColors);
		frontShadowDrawableVRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		frontShadowDrawableHTB = new GradientDrawable(
				GradientDrawable.Orientation.TOP_BOTTOM, frontShadowColors);
		frontShadowDrawableHTB.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		frontShadowDrawableHBT = new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP, frontShadowColors);
		frontShadowDrawableHBT.setGradientType(GradientDrawable.LINEAR_GRADIENT);
	}

	/**
	 *  绘制翻起页的阴影
	 * @param canvas
	 */
	public void drawCurrentPageShadow(Canvas canvas) {
		double degree;
		if (isRTandLB) {
			degree = Math.PI / 4 - Math.atan2(bezierControl1.y - touch.y, touch.x
					- bezierControl1.x);
		} else {
			degree = Math.PI / 4 - Math.atan2(touch.y - bezierControl1.y, touch.x
					- bezierControl1.x);
		}
		// 翻起页阴影顶点与touch点的距离
		double d1 = (float) 25 * 1.414 * Math.cos(degree);
		double d2 = (float) 25 * 1.414 * Math.sin(degree);
		float x = (float) (touch.x + d1);
		float y;
		if (isRTandLB) {
			y = (float) (touch.y + d2);
		} else {
			y = (float) (touch.y - d2);
		}
		path1.reset();
		path1.moveTo(x, y);
		path1.lineTo(touch.x, touch.y);
		path1.lineTo(bezierControl1.x, bezierControl1.y);
		path1.lineTo(bezierStart1.x, bezierStart1.y);
		path1.close();
		float rotateDegrees;
		canvas.save();

		canvas.clipPath(path0, Region.Op.XOR);
		canvas.clipPath(path1, Region.Op.INTERSECT);
		int leftx;
		int rightx;
		GradientDrawable currentPageShadow;
		if (isRTandLB) {
			leftx = (int) (bezierControl1.x);
			rightx = (int) bezierControl1.x + 25;
			currentPageShadow = frontShadowDrawableVLR;
		} else {
			leftx = (int) (bezierControl1.x - 25);
			rightx = (int) bezierControl1.x + 1;
			currentPageShadow = frontShadowDrawableVRL;
		}

		rotateDegrees = (float) Math.toDegrees(Math.atan2(touch.x
				- bezierControl1.x, bezierControl1.y - touch.y));
		canvas.rotate(rotateDegrees, bezierControl1.x, bezierControl1.y);
		currentPageShadow.setBounds(leftx,
				(int) (bezierControl1.y - maxLength), rightx,
				(int) (bezierControl1.y));
		currentPageShadow.draw(canvas);
		canvas.restore();

		path1.reset();
		path1.moveTo(x, y);
		path1.lineTo(touch.x, touch.y);
		path1.lineTo(bezierControl2.x, bezierControl2.y);
		path1.lineTo(bezierStart2.x, bezierStart2.y);
		path1.close();
		canvas.save();
		canvas.clipPath(path0, Region.Op.XOR);
		canvas.clipPath(path1, Region.Op.INTERSECT);
		if (isRTandLB) {
			leftx = (int) (bezierControl2.y);
			rightx = (int) (bezierControl2.y + 25);
			currentPageShadow = frontShadowDrawableHTB;
		} else {
			leftx = (int) (bezierControl2.y - 25);
			rightx = (int) (bezierControl2.y + 1);
			currentPageShadow = frontShadowDrawableHBT;
		}
		rotateDegrees = (float) Math.toDegrees(Math.atan2(bezierControl2.y
				- touch.y, bezierControl2.x - touch.x));
		canvas.rotate(rotateDegrees, bezierControl2.x, bezierControl2.y);
		float temp;
		if (bezierControl2.y < 0)
			temp = bezierControl2.y - height;
		else
			temp = bezierControl2.y;

		int hmg = (int) Math.hypot(bezierControl2.x, temp);
		if (hmg > maxLength)
			currentPageShadow.setBounds((int) (bezierControl2.x - 25) - hmg, leftx,
					(int) (bezierControl2.x + maxLength) - hmg,
					rightx);
		else
			currentPageShadow.setBounds(
					(int) (bezierControl2.x - maxLength), leftx,
					(int) (bezierControl2.x), rightx);

		currentPageShadow.draw(canvas);
		canvas.restore();
	}

	/**
	 * 绘制翻起页背面
	 * @param canvas
	 * @param bitmap
	 */
	private void drawCurrentBackArea(Canvas canvas, Bitmap bitmap) {
		int i = (int) (bezierStart1.x + bezierControl1.x) / 2;
		float f1 = Math.abs(i - bezierControl1.x);
		int i1 = (int) (bezierStart2.y + bezierControl2.y) / 2;
		float f2 = Math.abs(i1 - bezierControl2.y);
		float f3 = Math.min(f1, f2);
		path1.reset();
		path1.moveTo(bezierVertex2.x, bezierVertex2.y);
		path1.lineTo(bezierVertex1.x, bezierVertex1.y);
		path1.lineTo(bezierEnd1.x, bezierEnd1.y);
		path1.lineTo(touch.x, touch.y);
		path1.lineTo(bezierEnd2.x, bezierEnd2.y);
		path1.close();
		GradientDrawable folderShadowDrawable;
		int left;
		int right;
		if (isRTandLB) {
			left = (int) (bezierStart1.x - 1);
			right = (int) (bezierStart1.x + f3 + 1);
			folderShadowDrawable = folderShadowDrawableLR;
		} else {
			left = (int) (bezierStart1.x - f3 - 1);
			right = (int) (bezierStart1.x + 1);
			folderShadowDrawable = folderShadowDrawableRL;
		}
		canvas.save();
		canvas.clipPath(path0);
		canvas.clipPath(path1, Region.Op.INTERSECT);

		paint.setColorFilter(colorMatrixFilter);

		float dis = (float) Math.hypot(cornerX - bezierControl1.x,
				bezierControl2.y - cornerY);
		float f8 = (cornerX - bezierControl1.x) / dis;
		float f9 = (bezierControl2.y - cornerY) / dis;
		matrixArray[0] = 1 - 2 * f9 * f9;
		matrixArray[1] = 2 * f8 * f9;
		matrixArray[3] = matrixArray[1];
		matrixArray[4] = 1 - 2 * f8 * f8;
		matrix.reset();
		matrix.setValues(matrixArray);
		matrix.preTranslate(-bezierControl1.x, -bezierControl1.y);
		matrix.postTranslate(bezierControl1.x, bezierControl1.y);
		canvas.drawBitmap(bitmap, matrix, paint);
		// canvas.drawBitmap(bitmap, mMatrix, null);
		paint.setColorFilter(null);
		canvas.rotate(degrees, bezierStart1.x, bezierStart1.y);
		folderShadowDrawable.setBounds(left, (int) bezierStart1.y, right,
				(int) (bezierStart1.y + maxLength));
		folderShadowDrawable.draw(canvas);
		canvas.restore();
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (scroller.computeScrollOffset()) {
			touch.x = scroller.getCurrX();
			touch.y = scroller.getCurrY();

			if (touch.y >= height - 1) touch.y = height - 0.01F;
			else if(touch.y < 1) touch.y = 1.0f;

			postInvalidate();
		}
		else{

		}
	}

	private void startAnimation(int delayMillis) {
		int dx, dy;
		// dx 水平方向滑动的距离，负值会使滚动向左滚动
		// dy 垂直方向滑动的距离，负值会使滚动向上滚动
		if (!isDragToRight()) {//if (cornerX > 0) {
			dx = -(int) (width + touch.x);
		} else {
			dx = (int) (width - touch.x + width);
		}
		if (cornerY > 0) {
			dy = (int) (height - touch.y);
		} else {
			dy = (int) (1 - touch.y); // 防止touch.y最终变为0
		}
		scroller.startScroll((int) touch.x, (int) touch.y, dx, dy,delayMillis);
	}

	public void abortAnimation() {
		if (!scroller.isFinished()) {
			scroller.abortAnimation();
		}
	}

	/**
	 * 判断是否可以翻页
	 * @return
	 */
	public boolean canDragOver() {
		if (touchToCornerDis > width / 10)
			return true;
		return false;
	}

	/**
	 * 是否从左边翻向右边
	 * @return
	 */
	public boolean isDragToRight() {
		return dragToRight;
	}

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		if(nextCanvas == null || curCanvas == null)
			return false;

		if(e.getAction() != MotionEvent.ACTION_DOWN)
			return doTouchEvent(e);

		abortAnimation();
		calcCornerXY(e.getX(), e.getY());

		if (isDragToRight()) {
			if(pageable.isFirstPage())return false;
			pageable.doDraw(nextCanvas);
			pageable.onPrePage();
			pageable.doDraw(curCanvas);
		} else {
			if(pageable.isLastPage())return false;
			pageable.doDraw(curCanvas);
			pageable.onNextPage();
			pageable.doDraw(nextCanvas);
		}

		return doTouchEvent(e);
	}

	public void play(int autoDelayed){
		if(onPlay)
			return;
		if(pageable == null || pageable.isLastPage())
			return;
		onPlay = true;
		this.autoDelayed = autoDelayed;
		handler.sendEmptyMessageDelayed(0, autoDelayed);
	}

	public boolean autoPlay(){
		if(nextCanvas == null || curCanvas == null)
			return false;

		touch.x = width *2/3;
		touch.y = height * 4 / 5;

		abortAnimation();
		calcCornerXY(touch.x, touch.y);

		pageable.doDraw(curCanvas);
		pageable.onNextPage();
		if(pageable.isLastPage())
			return false;
		pageable.doDraw(nextCanvas);

		if(autoDelayed<2000)
			autoDelayed = 2000;
		startAnimation(2000);
		postInvalidate();
		return true;
	}

	@Override
	public boolean handleMessage(Message msg) {
		if(onTouch || autoPlay())
			handler.sendEmptyMessageDelayed(0, autoDelayed);
		else
			onPlay = false;
		return false;
	}
}
