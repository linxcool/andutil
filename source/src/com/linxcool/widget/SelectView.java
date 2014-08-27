package com.linxcool.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 下拉视图
 * @author: 胡昌海(linxcool.hu)
 */
public class SelectView extends RelativeLayout implements Callback,OnClickListener{

	static final int MSG_SELECT_ITEM = 1;
	static final int MSG_DELETE_ITEM = 2;
	
	/**
	 * 下拉选项选中监听
	 * @author: 胡昌海(linxcool.hu)
	 */
	public interface OnItemSelectedListener{
		public void onSelected(Object item);
	}
	
	/**
	 * 下拉视图适配器
	 * @author: 胡昌海(linxcool.hu)
	 */
	public class SelectAdapter extends BaseAdapter implements OnClickListener {
		Context context;
		List<Object> data;
		Handler handler;

		int bgId;
		int bgColor;
		int icId;
		
		int textColor;
		float textSize;
		
		public SelectAdapter(Context context,List<Object> data,Handler handler){
			this.context = context;
			this.data = data;
			this.handler = handler;
			
			textColor = Color.BLACK;
			textSize = 14;
		}
		
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			RelativeLayout layout;

			TextView tv;
			ImageView iv;
			
			if(convertView != null){
				layout = (RelativeLayout) convertView;
				tv = (TextView) layout.findViewById(0x1001);
				iv = (ImageView) layout.findViewById(0x1002);
			} else {
				layout = new RelativeLayout(context);
				layout.setGravity(Gravity.CENTER_VERTICAL);
				
				tv = new TextView(context);
				tv.setId(0x1001);
				int padding = px2dp(context, 15);
				tv.setPadding(padding, padding, 0, padding);
				tv.setTextColor(textColor);
				tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
				
				iv = new ImageView(context);
				iv.setId(0x1002);
				
				LayoutParams ivParams = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				ivParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				ivParams.addRule(RelativeLayout.CENTER_VERTICAL);
				ivParams.rightMargin = padding;
				layout.addView(iv, ivParams);
				
				LayoutParams tvParams = new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				tvParams.addRule(RelativeLayout.LEFT_OF,iv.getId());
				tvParams.addRule(RelativeLayout.CENTER_VERTICAL);
				layout.addView(tv, tvParams);
			}
			
			if(bgId > 0) layout.setBackgroundResource(bgId);
			else layout.setBackgroundColor(bgColor);
			
			tv.setText(getItem(position).toString());
			tv.setTag(position);
			tv.setOnClickListener(this);
			
			if(icId > 0) iv.setBackgroundResource(icId);
			iv.setTag(position);
			iv.setOnClickListener(this);
			
			return layout;
		}

		@Override
		public void onClick(View v) {
			int what;
			
			if(v instanceof TextView) what = MSG_SELECT_ITEM;
			else what = MSG_DELETE_ITEM;
			
			Message msg = handler.obtainMessage(what);
			msg.arg1 = (Integer) v.getTag();
			msg.sendToTarget();
		}
		
	}
	
	private Handler handler;
	private SelectAdapter selectAdapter;
	
	private TextView icon;
	private EditText input;
	private ImageView drop;

	private int iconId;
	private int dropId;
	
	private boolean inited;
	
	private PopupWindow popupWindow;
	private ListView items;
	private List<Object> data;
	
	private OnItemSelectedListener itemSelectedListener;
	
	/**
	 * 设置弹出项背景
	 * @param rid 资源ID
	 */
	public void setItemBackground(int rid){
		selectAdapter.bgId = rid;
	}
	
	public void setItemBackgroundColor(int color){
		selectAdapter.bgColor = color;
	}
	
	/**
	 * 设置弹出项删除图标
	 * @param rid
	 */
	public void setItemDeleteIcon(int rid){
		selectAdapter.icId = rid;
	}
	
	public void setItemTextColor(int colr){
		selectAdapter.textColor = colr;
	}
	
	public void setItemTextSize(float size){
		selectAdapter.textSize = size;
	}
	
	/**
	 * 添加弹出项
	 * @param item
	 */
	public void addItem(Object item){
		data.add(item);
	}
	
	/**
	 * 通知数据更改
	 */
	public void notifyDataSetChanged(){
		selectAdapter.notifyDataSetChanged();
	}
	
	/**
	 * 设置下拉图标
	 * @param resId
	 */
	public void setDropIcon(int resId){
		drop.setImageResource(resId);
	}
	
	/**
	 * 设置左边提醒图标
	 * @param rid
	 */
	public void setNoteIcon(int rid){
		icon.setBackgroundResource(rid);
	}
	
	/**
	 * 设置左边提醒文字
	 * @param text
	 */
	public void setNoteText(String text){
		icon.setText(text);
	}
	
	/**
	 * 设置左边提醒文字大小
	 * @param size
	 */
	public void setNoteTextSize(float size){
		icon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
	}
	
	/**
	 * 设置左边提醒文字颜色
	 * @param color
	 */
	public void setNoteTextColor(int color){
		icon.setTextColor(color);
	}
	
	/**
	 * 设置输入框显示的文字
	 * @param text
	 */
	public void setInputText(String text){
		input.setText(text);
	}
	
	/**
	 * 设置输入框显示的文字大小
	 */
	public void setInputTextSize(float size){
		input.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
	}
	
	/**
	 * 设置输入框显示的文字颜色
	 * @param color
	 */
	public void setInputTextColor(int color){
		input.setTextColor(color);
	}
	
	/**
	 * 设置输入框显示的文字暗示
	 * @param hint
	 */
	public void setInputHint(String hint){
		input.setHint(hint);
	}
	
	public void setOnItemSelectedListener(OnItemSelectedListener itemSelectedListener) {
		this.itemSelectedListener = itemSelectedListener;
	}

	public SelectView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		prepare(context);
	}

	public SelectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		prepare(context);
	}

	public SelectView(Context context) {
		super(context);
		prepare(context);
	}

	void prepare(Context context){
		
		data = new ArrayList<Object>();
		handler = new Handler(this);
		
		selectAdapter = new SelectAdapter(context, data, handler);
		
		icon = new TextView(context);
		icon.setId(0x1001);
		
		input = new EditText(context);
		input.setId(0x1002);
		input.setGravity(Gravity.CENTER_VERTICAL);
		input.setPadding(0, 0, 0, 0);
		input.setInputType(EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		input.setBackgroundResource(0);
		
		drop = new ImageView(context);
		drop.setId(0x1003);
		drop.setOnClickListener(this);
		
		if(iconId > 0)
			icon.setBackgroundResource(iconId);
		if(dropId > 0)
			drop.setBackgroundResource(dropId);
		
		LayoutParams iconParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		iconParams.addRule(RelativeLayout.CENTER_VERTICAL);
		this.addView(icon, iconParams);
		
		LayoutParams dropParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		dropParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		dropParams.addRule(RelativeLayout.CENTER_VERTICAL);
		this.addView(drop, dropParams);
		
		LayoutParams inputParams = new LayoutParams(
				LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		inputParams.addRule(RelativeLayout.RIGHT_OF,icon.getId());
		inputParams.addRule(RelativeLayout.LEFT_OF,drop.getId());
		inputParams.addRule(RelativeLayout.CENTER_VERTICAL);
		this.addView(input, inputParams);
	}

	@Override
	public void onClick(View v) {
		showItems();
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		if(!inited){
			init(getContext());
			inited = true;
		}
	}
	
	@SuppressWarnings("deprecation")
	void init(Context context){
		int width = getWidth();
		
		items = new ListView(context);
		items.setAdapter(selectAdapter);
		
		popupWindow = new PopupWindow(items, width,LayoutParams.WRAP_CONTENT, true); 
		popupWindow.setOutsideTouchable(true); 
		popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources()));  
	}
	
	public void showItems(){
		popupWindow.showAsDropDown(this,0,3); 
	}
	
	public void dismissItems(){ 
		popupWindow.dismiss(); 
    }
	
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_SELECT_ITEM:
			Object item = data.get(msg.arg1);
			input.setText(item.toString());
			input.setSelection(item.toString().length());
			dismissItems();
			if(itemSelectedListener != null){
				itemSelectedListener.onSelected(item);
			}
			break;
		case MSG_DELETE_ITEM:
			data.remove(msg.arg1);
			selectAdapter.notifyDataSetChanged();
			break;
		}
		return false;
	}

	int px2dp(Context context,float pxValue){
		float cmnscale = getResources().getDisplayMetrics().density;
		return (int) (pxValue / cmnscale + 0.5f);  
	}

}
