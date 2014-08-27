package com.linxcool.media;

import java.io.Serializable;

/**
 * 图片对象
 * <p><b>Time:</b> 2013-11-30
 * @author 胡昌海(linxcool.hu)
 */
public class ImageItem implements Serializable {

	private static final long serialVersionUID = 1L;

	public String imageId;
	public String thumbnailPath;
	public String imagePath;
	public boolean isSelected;
	
	public int arg1;
	public int arg2;
	
	public ImageItem(){
	}
	
	public ImageItem(ImageItem item){
		this.imageId = item.imageId;
		this.thumbnailPath = item.thumbnailPath;
		this.imagePath = item.imagePath;
		this.isSelected = item.isSelected;
		this.arg1 = item.arg1;
		this.arg2 = item.arg2;
	}
}
