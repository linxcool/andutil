package com.linxcool.widget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.linxcool.media.ImageItem;
import com.linxcool.util.BitmapMemoryCache;

/**
 * 瀑布流视图
 * @author 胡昌海(linxcool.hu)
 *
 */
public class PinterestView extends ScrollView implements Callback,View.OnTouchListener {

	/** 每页要加载的图片数量 */
	public static final int PAGE_SIZE = 15;
	/** 当前已加载到第几页 */
	private int page;
	private int columnSize = 2;
	/** 每一列的宽度 */
	private int columnWidth;
	/** 列的高度  */
	private SparseIntArray columnHeights;

	private boolean loadOnce;

	private List<LinearLayout> itemLayouts;

	/** 正在下载或等待下载的任务 */
	private static Set<LoadImageTask> taskCollection;

	private static LinearLayout scrollLayout;
	private static int scrollViewHeight;

	/** 记录上次垂直方向的滚动距离 */
	private static int lastScrollY = -1;

	/** 记录所有界面上的图片，用以可以随时控制对图片的释放  */
	private List<ImageView> imageViewList;

	private Handler handler;

	List<ImageItem> imgeList;
	BitmapMemoryCache bitmapCache;

	boolean checkVisible;
	
	public void setCheckVisible(boolean checkVisible) {
		this.checkVisible = checkVisible;
	}

	public void setImgeItems(List<ImageItem> imgeList) {
		this.imgeList = imgeList;
	}

	public PinterestView(Context context) {
		this(context, null);
	}

	public PinterestView(Context context, AttributeSet attrs) {
		super(context, attrs);
		taskCollection = new HashSet<LoadImageTask>();
		imageViewList = new ArrayList<ImageView>();
		handler = new Handler(this);
		setOnTouchListener(this);

		columnWidth = getResources().getDisplayMetrics().widthPixels / columnSize;
		columnHeights = new SparseIntArray();
		
		scrollLayout = new LinearLayout(context);
		scrollLayout.setOrientation(LinearLayout.HORIZONTAL);
		addView(scrollLayout, new LayoutParams(-1, -2));

		itemLayouts = new ArrayList<LinearLayout>();
		for (int i = 0; i < columnSize; i++) {
			LinearLayout itemLayout = new LinearLayout(context);
			itemLayout.setOrientation(LinearLayout.VERTICAL);
			itemLayouts.add(itemLayout);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -2, 1);
			scrollLayout.addView(itemLayout, params);
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		int scrollY = getScrollY();
		// 如果当前的滚动位置和上次相同，表示已停止滚动
		if (scrollY == lastScrollY) {
			// 当滚动的最底部，并且当前没有正在下载的任务时，开始加载下一页的图片
			if (scrollViewHeight + scrollY >= scrollLayout.getHeight() && taskCollection.isEmpty()) {
				loadMoreImages();
			}
			checkVisibility();
		}
		else {
			lastScrollY = scrollY;
			handler.sendEmptyMessageDelayed(0, 5);
		}
		return false;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed && !loadOnce) {
			bitmapCache = new BitmapMemoryCache(getContext());

			scrollViewHeight = getHeight();

			loadOnce = true;
			loadMoreImages();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			handler.sendEmptyMessageDelayed(0, 5);
		}
		return false;
	}

	/**
	 * 开始加载下一页的图片，每张图片都会开启一个异步线程去下载。
	 */
	public void loadMoreImages() {
		if(imgeList == null)
			return;
		int begin = page * PAGE_SIZE;
		int end = begin + PAGE_SIZE;
		int size = imgeList.size();
		if(begin >= size){
			//Toast.makeText(getContext(), "已没有更多图片", Toast.LENGTH_SHORT).show();
			return;
		}
		if (end > size)
			end = size;
		for (int i = begin; i < end; i++) {
			LoadImageTask task = new LoadImageTask();
			taskCollection.add(task);
			task.execute(imgeList.get(i));
		}
		page++;
	}

	/**
	 * 遍历imageViewList中的每张图片，对图片的可见性进行检查，
	 * 如果图片已经离开屏幕可见范围，则将图片替换成一张空图。
	 */
	public void checkVisibility() {
		for (int i = 0; i < imageViewList.size(); i++) {
			ImageView iv = imageViewList.get(i);
			ImageItem item = (ImageItem) iv.getTag();
			int borderTop = item.arg1;
			int borderBottom = item.arg2;
			
			boolean onVisible = borderBottom > getScrollY() && borderTop < getScrollY() + scrollViewHeight;
			
			if (onVisible) {
				Bitmap bitmap = bitmapCache.getBitmapFromCache(item.imagePath);
				if (bitmap != null) 
					iv.setImageBitmap(bitmap);
				else new LoadImageTask(iv).execute(item);
			} 
			else{
				if(!checkVisible){
					Bitmap bitmap = bitmapCache.getBitmapFromCache(item.imagePath);
					if (bitmap != null)iv.setImageBitmap(bitmap);
				}
				else iv.setImageBitmap(null);
			}
		}
	}

	class LoadImageTask extends AsyncTask<ImageItem, Void, Bitmap> {

		private ImageView iv;
		private ImageItem imageItem;

		public LoadImageTask() {
		}

		public LoadImageTask(ImageView iv) {
			this.iv = iv;
		}

		@Override
		protected Bitmap doInBackground(ImageItem... params) {
			imageItem = params[0];
			Bitmap imageBitmap = bitmapCache.getBitmapFromCache(imageItem.imagePath);
			if (imageBitmap == null) {
				imageBitmap = loadImage(imageItem);
			}
			return imageBitmap;
		}

		private Bitmap loadImage(ImageItem imageItem) {
			String path = imageItem.thumbnailPath;
			if(path == null || path.trim().length() == 0)
				path = imageItem.imagePath;
			if(path == null || path.trim().length() == 0)
				return null;
			Bitmap image = BitmapFactory.decodeFile(path);
			if (image == null) {
				return null;
			}
			bitmapCache.addBitmapToCache(path, image);
			return image;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (bitmap != null) {
				double ratio = bitmap.getWidth() / (columnWidth * 1.0);
				int scaledHeight = (int) (bitmap.getHeight() / ratio);
				addImage(bitmap, columnWidth, scaledHeight);
			}
			taskCollection.remove(this);
		}

		private void addImage(Bitmap bitmap, int imageWidth, int imageHeight) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					imageWidth, imageHeight);
			if (iv != null)
				iv.setImageBitmap(bitmap);
			else {
				ImageView imageView = new ImageView(getContext());
				imageView.setLayoutParams(params);
				imageView.setImageBitmap(bitmap);
				imageView.setScaleType(ScaleType.FIT_XY);
				imageView.setPadding(5, 5, 5, 5);
				findColumnToAdd(imageView, imageHeight).addView(imageView);
				imageViewList.add(imageView);
			}
		}

		/**
		 * 对列高度进行判断，当前高度最小的一列就是应该添加的一列
		 * @param imageView
		 * @param imageHeight
		 * @return
		 */
		private LinearLayout findColumnToAdd(ImageView imageView,int imageHeight) {
			ImageItem tag = new ImageItem(imageItem);
			imageView.setTag(tag);
			int column = findMinHeightColumn();
			tag.arg1 = columnHeights.get(column);
			tag.arg2 = tag.arg1 + imageHeight;
			columnHeights.put(column, tag.arg2);
			return itemLayouts.get(column);
		}
	}

	private int findMinHeightColumn(){
		int min = columnHeights.get(0);
		int j = 0;
		for (int i = 1; i < columnSize; i++) {
			int temp = columnHeights.get(i);
			if(min > temp){
				min = temp;
				j = i;
			}
		}
		return j;
	}
}
