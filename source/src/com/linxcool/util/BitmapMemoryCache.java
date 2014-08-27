package com.linxcool.util;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * 图片缓存
 * <p><b>Time:</b> 2013-11-30
 * @author 胡昌海(linxcool.hu)
 */
public class BitmapMemoryCache {

	// 软引用缓存容量
	private final int SOFT_CACHE_SIZE = 15;
	// 硬引用缓存
	private LruCache<String, Bitmap> lruCache; 
	// 软引用缓存
	private LinkedHashMap<String, SoftReference<Bitmap>> softCache; 

	public BitmapMemoryCache(Context context) {
		ActivityManager am = null;
		try{
			am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		}catch (Exception e) {
			am = null;
		}
		
		int memCls = am != null ? am.getMemoryClass():1;
		int cacheSize = 1024 * 1024 * memCls / 4;

		lruCache = new LruCache<String, Bitmap>(cacheSize) {
			
			@Override
			protected int sizeOf(String key, Bitmap value) {
				if(value == null)
					return 0;
				return value.getRowBytes() * value.getHeight();
			}

			/**
			 * 硬引用缓存容量满的时候
			 * 会根据LRU算法把最近没有被使用的图片转入此软引用缓存
			 */
			@Override
			protected void entryRemoved(boolean evicted, String key,Bitmap oldValue, Bitmap newValue) {
				if(oldValue == null)
					return;
				softCache.put(key, new SoftReference<Bitmap>(oldValue));
			}
		};

		softCache = new LinkedHashMap<String, SoftReference<Bitmap>>(SOFT_CACHE_SIZE, 0.75f, true) {

			private static final long serialVersionUID = 6040103833179403725L;

			@Override
			protected boolean removeEldestEntry(Entry<String, SoftReference<Bitmap>> eldest) {
				return size() > SOFT_CACHE_SIZE;
			}
		};
	}

	/**
	 * 从缓存中获取图片
	 */
	public Bitmap getBitmapFromCache(String url) {
		// 先从硬引用缓存中获取
		synchronized (lruCache) {
			Bitmap bitmap = lruCache.get(url);
			if (bitmap != null) {
				// 如果找到的话，把元素移到LinkedHashMap的最前面，从而保证在LRU算法中是最后被删除
				lruCache.remove(url);
				lruCache.put(url, bitmap);
				return bitmap;
			}
		}
		// 如果硬引用缓存中找不到，到软引用缓存中找
		synchronized (softCache) {
			SoftReference<Bitmap> bitmapReference = softCache.get(url);
			if (bitmapReference != null) {
				Bitmap bitmap = bitmapReference.get();
				if (bitmap != null) {
					// 将图片移回硬缓存
					lruCache.put(url, bitmap);
					softCache.remove(url);
					return bitmap;
				} 
				else softCache.remove(url);
			}
		}
		return null;
	}

	/**
	 * 添加图片到缓存
	 */
	public void addBitmapToCache(String url, Bitmap bitmap) {
		synchronized (lruCache) {
			if (bitmap != null) {
				lruCache.put(url, bitmap);
			}
		}
	}

	public void clearCache() {
		softCache.clear();
	}
}