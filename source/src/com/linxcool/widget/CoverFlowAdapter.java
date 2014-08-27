package com.linxcool.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.linxcool.util.ImageUtil;

/**
 * 3D画廊适配器
 * <p><b>Time:</b> 2013-12-6
 * @author 胡昌海(linxcool.hu)
 */
@SuppressWarnings("deprecation")
public class CoverFlowAdapter extends BaseAdapter {

	Context context;
	int ItemBackground;
	List<Bitmap> data;
	
	public CoverFlowAdapter(Context context,List<Bitmap> srcs){
		this.context = context;
		this.data = new ArrayList<Bitmap>();
		for (int i = 0; i < srcs.size(); i++) {
			Bitmap des = ImageUtil.createReflectedBitmap(srcs.get(i));
			data.add(des);
		}
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView iv = (ImageView) convertView;
		if(iv == null){
			iv = new ImageView(context);
			iv.setLayoutParams(new CoverFlowGallery.LayoutParams(dp2px(120), dp2px(100)));
			iv.setScaleType(ScaleType.CENTER_INSIDE);
		}
		
		iv.setImageBitmap(data.get(position));
		BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
		drawable.setAntiAlias(true);
		
		return iv;
	}

	public float getScale(boolean focused, int offset) {
		return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
	}
	
	int dp2px(float dp){
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (dp * density + 0.5);
	}
}
