package com.linxcool.view;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;

public class SnowSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	Bitmap[] snowBitmaps;
	Bitmap bg;
	Thread thread;

	boolean isRunning;

	SurfaceHolder holder;
	float screenWidth;
	float screenHeiht;

	Paint paint;
	Random random;
	RectF rectF;
	
	Snow snow;
	ArrayList<Snow> snowflake_xxl;
	ArrayList<Snow> snowflake_xl;
	ArrayList<Snow> snowflake_m;
	ArrayList<Snow> snowflake_s;
	ArrayList<Snow> snowflake_l;

	public SnowSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SnowSurfaceView(Context context) {
		super(context);
		init(context);
	}

	void init(Context context){
		isRunning = true;
		random = new Random();
		snowBitmaps = new Bitmap[5];
		snowflake_l = new ArrayList<Snow>();
		snowflake_s = new ArrayList<Snow>();
		snowflake_m = new ArrayList<Snow>();
		snowflake_xl = new ArrayList<Snow>();
		snowflake_xxl = new ArrayList<Snow>();

		holder = getHolder();
		holder.addCallback(this);
		// 顶层绘制SurfaceView设成透明
		holder.setFormat(PixelFormat.RGBA_8888);

		setLayoutParams(new LayoutParams(-1,-1));
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		screenHeiht = metrics.heightPixels;
		screenWidth = metrics.widthPixels;
		rectF = new RectF(0, 0, screenWidth, screenHeiht);
		
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);

		loadSnowImage();
		addRandomSnow();
	}

	void loadSnowImage() {
		try{
			AssetManager am = getResources().getAssets();
			snowBitmaps[0] = BitmapFactory.decodeStream(am.open("imgs/snows/snowflake_l.png"));
			snowBitmaps[1] = BitmapFactory.decodeStream(am.open("imgs/snows/snowflake_s.png"));
			snowBitmaps[2] = BitmapFactory.decodeStream(am.open("imgs/snows/snowflake_m.png"));
			snowBitmaps[3] = BitmapFactory.decodeStream(am.open("imgs/snows/snowflake_xl.png"));
			snowBitmaps[4] = BitmapFactory.decodeStream(am.open("imgs/snows/snowflake_xxl.png"));
			
			bg = BitmapFactory.decodeStream(am.open("imgs/snows/bg.jpg"));
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	void addRandomSnow() {
		for (int i = 0; i < 20; i++) {
			snowflake_xxl.add(new Snow(snowBitmaps[4], random.nextFloat()
					* screenWidth, random.nextFloat() * screenHeiht, 7f,
					1 - random.nextFloat() * 2));
			snowflake_xl.add(new Snow(snowBitmaps[3], random.nextFloat()
					* screenWidth, random.nextFloat() * screenHeiht, 5f,
					1 - random.nextFloat() * 2));
			snowflake_m.add(new Snow(snowBitmaps[2], random.nextFloat()
					* screenWidth, random.nextFloat() * screenHeiht, 3f,
					1 - random.nextFloat() * 2));
			snowflake_s.add(new Snow(snowBitmaps[1], random.nextFloat()
					* screenWidth, random.nextFloat() * screenHeiht, 2f,
					1 - random.nextFloat() * 2));
			snowflake_l.add(new Snow(snowBitmaps[0], random.nextFloat()
					* screenWidth, random.nextFloat() * screenHeiht, 2f,
					1 - random.nextFloat() * 2));
		}
	}

	void drawSnow(Canvas canvas) {
		canvas.drawRect(rectF, paint);
		canvas.drawBitmap(bg, null, rectF, paint);
		
		for (int i = 0; i < 20; i++) {
			snow = snowflake_xxl.get(i);
			if(snow.bitmap == null || snow.bitmap.isRecycled())
				break;
			canvas.drawBitmap(snow.bitmap, snow.x, snow.y, paint);
			snow = snowflake_xl.get(i);
			canvas.drawBitmap(snow.bitmap, snow.x, snow.y, paint);
			snow = snowflake_m.get(i);
			canvas.drawBitmap(snow.bitmap, snow.x, snow.y, paint);
			snow = snowflake_s.get(i);
			canvas.drawBitmap(snow.bitmap, snow.x, snow.y, paint);
			snow = snowflake_l.get(i);
			canvas.drawBitmap(snow.bitmap, snow.x, snow.y, paint);
		}
	}

	void updateSnow(){
		for (int i = 0; i < 20; i++) {
			snow = snowflake_xxl.get(i);
			snowDown(snow);

			snow = snowflake_xl.get(i);
			snowDown(snow);

			snow = snowflake_m.get(i);
			snowDown(snow);

			snow = snowflake_s.get(i);
			snowDown(snow);

			snow = snowflake_l.get(i);
			snowDown(snow);
		}
	}

	void snowDown(Snow snow) {
		// 雪花的落出屏幕后又让它从顶上下落
		if (snow.x > screenWidth || snow.y > screenHeiht) {
			snow.y = 0;
			snow.x = random.nextFloat() * screenWidth;
		}
		// 下落飘的偏移量
		snow.x += snow.offset;
		// 下落的速度
		snow.y += snow.speed;
	}

	@Override
	public void run() {
		while (isRunning) {
			synchronized (this) {
				Canvas canvas = holder.lockCanvas();
				if (canvas != null) {
					drawSnow(canvas);
					updateSnow();
				}
				try {
					Thread.sleep(15);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					if (null != canvas) {
						holder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			isRunning = false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		//Empty
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isRunning = false;
		for (int i = 0; i < snowBitmaps.length; i++) {
			if(snowBitmaps[i] != null)
				snowBitmaps[i].recycle();
			snowBitmaps[i] = null;
		}
	}

	public class Coordinate {
		int x;
		int y;
		public Coordinate(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	public class Snow {

		Bitmap bitmap;
		float x;
		float y;
		float speed;
		float offset;

		public Snow(Bitmap bitmap, float x, float y, float speed, float offset) {
			this.bitmap = bitmap;
			this.x = x;
			this.y = y;
			this.speed = speed;
			this.offset = offset;
		}
	}
}
