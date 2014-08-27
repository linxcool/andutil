package api.test;

import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.linxcool.app.BaseActivity;
import com.linxcool.media.ImageAlbumHelper;
import com.linxcool.media.ImageBucket;
import com.linxcool.media.ImageBucketAdapter;
import com.linxcool.media.ImageGridAdapter;
import com.linxcool.media.ImageItem;
import com.linxcool.util.R;

/**
 * 图片专辑
 * @author 胡昌海(linxcool.hu)
 */
public class AlbumsActivity extends BaseActivity implements OnItemClickListener{

	List<ImageBucket> imageBuckets;

	List<ImageItem> dataList;
	ImageGridAdapter adapter;
	boolean isBucketModel;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dataList = (List<ImageItem>) getIntent().getSerializableExtra("imageList");
		if(dataList == null){
			isBucketModel = true;
			initAlbums(this);
		}
		else{
			isBucketModel = false;
			initPhotos(this);
		}
		
		enabledHomeBack();
	}

	void initAlbums(Context context){
		setContentView(R.layout.activity_albums);

		ImageAlbumHelper helper = ImageAlbumHelper.getHelper(this);
		imageBuckets = helper.getImagesBucketList(true);
		ImageBucketAdapter adapter = new ImageBucketAdapter(this, imageBuckets);

		GridView gridView = (GridView) findViewById(R.id.gridview);
		gridView.setOnItemClickListener(this);
		gridView.setAdapter(adapter);
		gridView.setOnScrollListener(adapter);
	}

	void initPhotos(Context context){
		setContentView(R.layout.activity_photos);
		
		adapter = new ImageGridAdapter(this, dataList);
		
		GridView gridView = (GridView) findViewById(R.id.gridview);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gridView.setOnItemClickListener(this);
		gridView.setAdapter(adapter);
		gridView.setOnScrollListener(adapter);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		if(isBucketModel){
			Intent intent = new Intent(this,AlbumsActivity.class);
			intent.putExtra("imageList",(Serializable) imageBuckets.get(position).imageList);
			startActivity(intent);
		}
		else{
			adapter.notifyDataSetChanged();
		}
	}
}
