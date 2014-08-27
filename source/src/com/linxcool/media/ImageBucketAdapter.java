package com.linxcool.media;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.linxcool.util.ImageUtil;
import com.linxcool.util.R;
import com.linxcool.view.OptimizeBaseAdapter;
import com.linxcool.view.ViewHolder;

/**
 * 一个目录的相册适配器
 * @author 胡昌海(linxcool.hu)
 */
public class ImageBucketAdapter extends OptimizeBaseAdapter{

	public ImageBucketAdapter(Context context, List<?> buckets){
		super(context, buckets);
	}

	@Override
	protected View newItemView(Context context) {
		return View.inflate(context, R.layout.item_albums, null);
	}
	
	@Override
	protected View getView(int position, View itemView) {
		ImageView iv = ViewHolder.get(itemView, R.id.image);
		ImageView selected = ViewHolder.get(itemView, R.id.isselected);
		TextView name = ViewHolder.get(itemView, R.id.name);
		TextView count = ViewHolder.get(itemView, R.id.count);
		
		ImageBucket imageBucket = (ImageBucket) getItem(position);
		name.setText(imageBucket.bucketName);
		count.setText(String.valueOf(imageBucket.count));
		selected.setVisibility(View.GONE);
		
		if(imageBucket.imageList == null || imageBucket.imageList.size() == 0){
			iv.setImageBitmap(null);
			return itemView;
		}
		
		ImageItem item = imageBucket.imageList.get(0);
		if(item.thumbnailPath != null)
			setImageViewBitmap(iv, position, item.thumbnailPath);
		else if(item.imagePath != null)
			setImageViewBitmap(iv, position, item.imagePath);
		else {
			iv.setImageBitmap(null);
		}
		
		return itemView;
	}

	@Override
	protected Bitmap loadImageViewBitmap(int position, String bitmapKey) {
		if(TextUtils.isEmpty(bitmapKey))
			return null;
		return ImageUtil.revisionImageSize(bitmapKey);
	}
}
