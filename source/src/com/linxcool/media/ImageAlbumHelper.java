package com.linxcool.media;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.linxcool.util.ResourceUtil;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;

/**
 * 图片专辑帮助类
 * <p><b>Time:</b> 2013-11-30
 * @author 胡昌海(linxcool.hu)
 */
public class ImageAlbumHelper {

	Context context;
	ContentResolver resolver;

	// 缩略图列表
	HashMap<String, String> thumbMap;
	// 专辑列表
	List<HashMap<String, String>> albumList;
	HashMap<String, ImageBucket> bucketMap;
	//是否创建了图片集
	boolean hasBuildedBucketMap;

	private static ImageAlbumHelper instance;

	private ImageAlbumHelper() {
		thumbMap = new HashMap<String, String>();
		albumList = new ArrayList<HashMap<String, String>>();
		bucketMap = new HashMap<String, ImageBucket>();
	}

	public static ImageAlbumHelper getHelper(Context context) {
		if (instance == null) {
			instance = new ImageAlbumHelper();
			instance.context = context;
			instance.resolver = context.getContentResolver();
		}
		return instance;
	}

	/**
	 * 得到图片集
	 * @param refresh
	 * @return
	 */
	public List<ImageBucket> getImagesBucketList(boolean refresh) {
		if (refresh || (!refresh && !hasBuildedBucketMap)){
			getThumbnail();
			getBucket();
			hasBuildedBucketMap = true;
		}

		List<ImageBucket> tmpList = new ArrayList<ImageBucket>();
		Iterator<Entry<String, ImageBucket>> itr = bucketMap.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, ImageBucket> entry = (Entry<String, ImageBucket>) itr.next();
			tmpList.add(entry.getValue());
		}

		return tmpList;
	}

	/**
	 * 得到缩略图索引
	 */
	private void getThumbnail() {
		String[] projection = { 
				Thumbnails._ID, 
				Thumbnails.IMAGE_ID,
				Thumbnails.DATA 
		};
		Cursor cur = resolver.query(
				Thumbnails.EXTERNAL_CONTENT_URI, 
				projection,
				null, 
				null, 
				null);

		thumbMap.clear();
		
		while(cur.moveToNext()){
			//int _id = cursor.getInt(cur.getColumnIndex(Thumbnails._ID));
			int imgId = cur.getInt(cur.getColumnIndex(Thumbnails.IMAGE_ID));
			String imgPath = cur.getString(cur.getColumnIndex(Thumbnails.DATA));
			thumbMap.put(String.valueOf(imgId), imgPath);
		}
	}

	/**
	 * 得到相册索引
	 */
	private void getBucket(){
		String columns[] = new String[] { 
				Media._ID, 
				Media.BUCKET_ID,
				Media.PICASA_ID, 
				Media.DATA, 
				Media.DISPLAY_NAME, 
				Media.TITLE,
				Media.SIZE, 
				Media.BUCKET_DISPLAY_NAME 
		};
		Cursor cur = resolver.query(
				Media.EXTERNAL_CONTENT_URI, 
				columns, 
				null, 
				null, 
				null);

		bucketMap.clear();
		
		while (cur.moveToNext()){
			String _id = cur.getString(cur.getColumnIndexOrThrow(Media._ID));
			String path = cur.getString(cur.getColumnIndexOrThrow(Media.DATA));
			String bucketName = cur.getString(cur.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME));
			String bucketId = cur.getString(cur.getColumnIndexOrThrow(Media.BUCKET_ID));
			//String name = cur.getString(cur.getColumnIndexOrThrow(Media.DISPLAY_NAME));
			//String title = cur.getString(cur.getColumnIndexOrThrow(Media.TITLE));
			//String size = cur.getString(cur.getColumnIndexOrThrow(Media.SIZE));
			//String picasaId = cur.getString(cur.getColumnIndexOrThrow(Media.PICASA_ID));

			if(!checkImage(path))
				continue;
			
			ImageBucket bucket = bucketMap.get(bucketId);
			if (bucket == null) {
				bucket = new ImageBucket();
				bucketMap.put(bucketId, bucket);
				bucket.imageList = new ArrayList<ImageItem>();
				bucket.bucketName = bucketName;
			}
			bucket.count++;
			ImageItem imageItem = new ImageItem();
			imageItem.imageId = _id;
			imageItem.imagePath = path;
			imageItem.thumbnailPath = thumbMap.get(_id);
			bucket.imageList.add(imageItem);
		}
	}

	/**
	 * 检查图片是否可用
	 * @param path
	 * @return
	 */
	boolean checkImage(String path){
		if(!ResourceUtil.isFileExist(path)){
			String where = Media.DATA + "='" + path + "'"; 
            resolver.delete(Media.EXTERNAL_CONTENT_URI, where, null);
			return false;
		}
		return true;
	}
	
	/**
	 * 得到原图
	 */
	void getAlbum() {
		String[] projection = { 
				Albums._ID, 
				Albums.ALBUM, 
				Albums.ALBUM_ART,
				Albums.ALBUM_KEY, 
				Albums.ARTIST, 
				Albums.NUMBER_OF_SONGS 
		};
		Cursor cur = resolver.query(
				Albums.EXTERNAL_CONTENT_URI, 
				projection, 
				null,
				null, 
				null);

		while (cur.moveToFirst()) {
			// Get the field values
			int _id = cur.getInt(cur.getColumnIndex(Albums._ID));
			String album = cur.getString(cur.getColumnIndex(Albums.ALBUM));
			String albumArt = cur.getString(cur.getColumnIndex(Albums.ALBUM_ART));
			String albumKey = cur.getString(cur.getColumnIndex(Albums.ALBUM_KEY));
			String artist = cur.getString(cur.getColumnIndex(Albums.ARTIST));
			int numOfSongs = cur.getInt(cur.getColumnIndex(Albums.NUMBER_OF_SONGS));

			// Do something with the values.
			HashMap<String, String> hash = new HashMap<String, String>();
			hash.put("_id", _id + "");
			hash.put("album", album);
			hash.put("albumArt", albumArt);
			hash.put("albumKey", albumKey);
			hash.put("artist", artist);
			hash.put("numOfSongs", String.valueOf(numOfSongs));
			albumList.add(hash);
		}
	}

	/**
	 * 得到原始图像路径
	 * @param image_id
	 * @return
	 */
	String getOriginalImagePath(String image_id) {
		String[] projection = { Media._ID, Media.DATA };
		Cursor cursor = resolver.query(
				Media.EXTERNAL_CONTENT_URI, 
				projection,
				Media._ID + "=" + image_id, 
				null, 
				null);
		if (cursor != null) {
			cursor.moveToFirst();
			return cursor.getString(cursor.getColumnIndex(Media.DATA));
		}
		return null;
	}

}
