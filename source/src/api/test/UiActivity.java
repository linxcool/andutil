package api.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.linxcool.app.BaseActivity;
import com.linxcool.util.ImageUtil;
import com.linxcool.util.R;
import com.linxcool.widget.AdvertiseView;
import com.linxcool.widget.CoverFlowAdapter;
import com.linxcool.widget.CoverFlowGallery;
import com.linxcool.widget.Rotate3dView;
import com.linxcool.widget.ScaleArea;
import com.linxcool.widget.SelectView;
import com.linxcool.widget.SlipButton;
import com.linxcool.widget.SelectView.OnItemSelectedListener;

/**
 * 控件演示界面
 * <p><b>Time:</b> 2013-12-30
 * @author 胡昌海(Linxcool.Hu)
 */
public class UiActivity extends BaseActivity implements OnClickListener{

	Rotate3dView container;
	View v1;
	View v2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ui);
		
		enabledHomeBack();
		
		container = (Rotate3dView) findViewById(R.id.container);

		v1 = getLayoutInflater().inflate(R.layout.view_widget_show_1, null);
		v1.findViewById(R.id.tv).setOnClickListener(this);
		initSelectView(this);
		initSlipButton(this);
		initScaleArea(this);
		initAd(this);

		v2 = getLayoutInflater().inflate(R.layout.view_widget_show_2, null);
		initCoverFlowGallery(this);
		
		container.addView(v1);
	}

	void initCoverFlowGallery(Context context){
		List<Bitmap> data = new ArrayList<Bitmap>();
		AssetManager am = getAssets();
		for (int i = 0; i < 4; i++) {
			try {
				String fileName = String.format(Locale.getDefault(), "imgs/0%d.jpg",i);
				Bitmap bitmap = ImageUtil.revisionImageSize(am.open(fileName));
				data.add(bitmap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		CoverFlowAdapter adapter = new CoverFlowAdapter(this,data);
		
		CoverFlowGallery gallery = (CoverFlowGallery) v2.findViewById(R.id.coverFlowGallery);
		gallery.setAdapter(adapter);
	}
	
	
	
	void initSelectView(Context context){
		SelectView selectView = (SelectView) v1.findViewById(R.id.selectView);

		selectView.setDropIcon(R.drawable.ic_drop_down);
		selectView.setItemDeleteIcon(android.R.drawable.presence_offline );
		selectView.setNoteIcon(R.drawable.ic_user);
		selectView.setInputHint("用户名");
		selectView.setInputTextSize(14);
		selectView.setItemBackgroundColor(Color.WHITE);

		class SelectItem{
			String userName;
			String password;
			public SelectItem(String userName, String password) {
				this.userName = userName;
				this.password = password;
			}
			@Override
			public String toString() {
				return userName + "|" + password;
			}
		}
		
		selectView.addItem(new SelectItem("北京","12345678"));
		selectView.addItem(new SelectItem("上海",""));
		selectView.addItem(new SelectItem("青岛","62345671"));

		selectView.notifyDataSetChanged();

		selectView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onSelected(Object item) {
				System.out.println((SelectItem)item);
			}
		});
	}

	void initSlipButton(Context context){
		SlipButton sbtn = (SlipButton) v1.findViewById(R.id.slipButton);
		sbtn.setResouce(R.drawable.slip_btn_on, R.drawable.slip_btn_off, R.drawable.slip_btn);
	}

	void initScaleArea(Context context){
		ScaleArea scaleArea = (ScaleArea) v1.findViewById(R.id.scaleArea);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aestheticism);
		scaleArea.setImg(bitmap);
	}

	void initAd(Context context){
		AdvertiseView adView = (AdvertiseView) v1.findViewById(R.id.ad);
		adView.setTextColor(Color.WHITE);
		adView.addViewContent(1, "imgs/00.jpg", "美女集中营，等你来相会！");
		adView.addViewContent(2, "imgs/01.jpg", "不过瘾？那就来这里试试！");
		adView.setFlipInterval(7000);
		adView.startFlipping();
		adView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), MenuActivity.class);
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out); 
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(homeBack && item.getItemId() == android.R.id.home){
			if(container.getChildAt(0) == v1) {
				finish();
				overridePendingTransition(R.anim.anim_l2r_in,R.anim.anim_l2r_out);
			}
			else container.applyRotation(v1, 0, 90);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv:
			container.applyRotation(v2, 0, 90);;
			break;
		default:
			break;
		}
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.ACTION_UP){
		}
		return super.onKeyUp(keyCode, event);
	}
}

