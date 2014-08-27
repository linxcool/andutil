package com.linxcool.media;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.linxcool.util.R;
import com.linxcool.view.ViewHolder;

public class ImageGridAdapter extends ImageBucketAdapter{

	public ImageGridAdapter(Context context, List<?> data) {
		super(context, data);
	}

	@Override
	protected View newItemView(Context context) {
		return View.inflate(context, R.layout.item_photos, null);
	}
	
	@Override
	protected View getView(int position, View itemView) {
		ImageView iv = ViewHolder.get(itemView, R.id.image);
		ImageView selected = ViewHolder.get(itemView, R.id.isselected);
		TextView name = ViewHolder.get(itemView, R.id.name);
		
		final ImageItem item = (ImageItem) getItem(position);
		if(item.isSelected){
			selected.setImageResource(R.drawable.ic_selected);
			name.setBackgroundResource(R.drawable.bgd_relatly_line);
		} else {
			selected.setImageDrawable(null);
			name.setBackgroundColor(0x00000000);
		}
		
		if(item.thumbnailPath != null)
			setImageViewBitmap(iv, position, item.thumbnailPath);
		else if(item.imagePath != null)
			setImageViewBitmap(iv, position, item.imagePath);
		else {
			iv.setImageBitmap(null);
		}
		
		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				item.isSelected = !item.isSelected;
				notifyDataSetChanged();
			}
		});
		
		return itemView;
	}
	
}
