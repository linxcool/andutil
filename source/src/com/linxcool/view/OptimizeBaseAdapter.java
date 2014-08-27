package com.linxcool.view;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.linxcool.util.BitmapMemoryCache;

/**
 * 优化后的BaseAdapter，但需要设置ListView的ScrollListener为该类对象<p>
 * 
 * 1、支持可视化加载<br>
 * 2、减少卡顿现象<br>
 * 3、优化图片展示</p>
 * 
 * @author 胡昌海(linxcool.hu)
 */
public abstract class OptimizeBaseAdapter extends BaseAdapter implements Callback, OnScrollListener{

	protected Context context;
	protected Handler handler;

	protected List<?> data;
	protected BitmapMemoryCache cache;
	protected ExecutorService taskService;

	protected int firstVisibleItem;
	protected int visibleItemCount;

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public OptimizeBaseAdapter(Context context, List<?> data){
		this.context = context;
		this.data = data;
		this.handler = new Handler(this);

		firstVisibleItem = -1;
		visibleItemCount = -1;

		cache = new BitmapMemoryCache(context);
		taskService = Executors.newFixedThreadPool(1);
	}

	/**
	 * 获得展示的视图
	 * @param position 索引
	 * @param itemView 不为空的视图结构
	 * @return
	 */
	protected abstract View getView(int position, View itemView);

	/**
	 * 视图为空时将调用该方法返回新的视图结构对象
	 * @param context
	 * @return
	 */
	protected abstract View newItemView(Context context);

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) convertView = newItemView(context);
		else setVisibleItemCount(convertView);
		return getView(position, convertView);
	}

	/**
	 * 设置可视化视图的个数
	 * @param convertView
	 */
	protected void setVisibleItemCount(View convertView){
		if(visibleItemCount == -1 
				&& convertView.getWidth() != 0 
				&& convertView.getHeight() != 0){
			DisplayMetrics dm = context.getResources().getDisplayMetrics();
			int columns = dm.widthPixels / convertView.getWidth();
			int rows = dm.heightPixels / convertView.getHeight();
			visibleItemCount = columns * (rows + 1);
		}
	}

	/**
	 * 设置ImageView的展示图片 如果调用该方法则必须重载 {@link #loadImageViewBitmap} 方法
	 * @param iv ImageView对象
	 * @param position 当前项索引
	 * @param bitmapKey 图片的Key
	 */
	protected void setImageViewBitmap(final ImageView iv, final int position, final String bitmapKey){
		Bitmap bitmap = cache.getBitmapFromCache(bitmapKey);
		if(bitmap != null) {
			iv.setImageBitmap(bitmap);
			return;
		}
		
		iv.setImageBitmap(null);
		iv.setTag(bitmapKey);
		
		taskService.submit(new Runnable() {
			@Override
			public void run() {
				if(!isItemVisible(position)){
					return;
				}
				final Bitmap bitmap = loadImageViewBitmap(position, bitmapKey);
				cache.addBitmapToCache(bitmapKey, bitmap);
				if(!isItemVisible(position)){
					return;
				}
				handler.post(new Runnable() { @Override public void run() {
					if(!bitmapKey.equals(iv.getTag())){
						return;
					}
					iv.setImageBitmap(bitmap);
				}});
			}
		});
	}

	protected boolean isItemVisible(int position){
		if(firstVisibleItem == -1 || visibleItemCount == -1 )
			return true;
		return position >= firstVisibleItem && position <= (firstVisibleItem + visibleItemCount);
	}

	/**
	 * 异步或同步加载ImageView的图片
	 * @param position 当前项索引
	 * @param bitmapKey 图片的Key
	 * @return
	 */
	protected Bitmap loadImageViewBitmap(int position, String bitmapKey){
		return null;
	}

	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		this.firstVisibleItem = firstVisibleItem;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.firstVisibleItem = view.getFirstVisiblePosition();
		if(scrollState == SCROLL_STATE_IDLE){
			notifyDataSetChanged();
		}
	}

	public void release(){
		cache.clearCache();
	}
}
