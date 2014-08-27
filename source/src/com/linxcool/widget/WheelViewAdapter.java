package com.linxcool.widget;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * WheelView适配器
 * <P>
 * <STRONG>Time：</STRONG>2012-9-10 下午10:09:35
 * </P>
 * 
 * @author 胡昌海(linxcool)
 */
public abstract class WheelViewAdapter {

	protected static final int NO_RESOURCE = 0;

	private List<DataSetObserver> datasetObservers;

	protected Context context;
	protected LayoutInflater inflater;
	protected int itemResourceId;
	protected int itemTextResourceId;
	protected int emptyItemResourceId;

	public int getItemResource() {
		return itemResourceId;
	}

	public void setItemResource(int itemResourceId) {
		this.itemResourceId = itemResourceId;
	}

	public int getItemTextResource() {
		return itemTextResourceId;
	}

	public void setItemTextResource(int itemTextResourceId) {
		this.itemTextResourceId = itemTextResourceId;
	}

	public int getEmptyItemResource() {
		return emptyItemResourceId;
	}

	public void setEmptyItemResource(int emptyItemResourceId) {
		this.emptyItemResourceId = emptyItemResourceId;
	}

	protected WheelViewAdapter(Context context) {
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public abstract int getItemsCount();

	public abstract View getItem(int index, View convertView, ViewGroup parent);

	public void registerDataSetObserver(DataSetObserver observer) {
		if (datasetObservers == null) {
			datasetObservers = new LinkedList<DataSetObserver>();
		}
		datasetObservers.add(observer);
	}

	public View getEmptyItem(View convertView, ViewGroup parent) {
		return null;
	}
	
	public void unregisterDataSetObserver(DataSetObserver observer) {
		if (datasetObservers != null) {
			datasetObservers.remove(observer);
		}
	}

	protected void notifyDataChangedEvent() {
		if (datasetObservers != null) {
			for (DataSetObserver observer : datasetObservers) {
				observer.onChanged();
			}
		}
	}

	protected void notifyDataInvalidatedEvent() {
		if (datasetObservers != null) {
			for (DataSetObserver observer : datasetObservers) {
				observer.onInvalidated();
			}
		}
	}
}
