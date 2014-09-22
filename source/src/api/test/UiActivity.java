package api.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.linxcool.app.BaseActivity;
import com.linxcool.util.ImageUtil;
import com.linxcool.util.R;
import com.linxcool.widget.AdvertiseView;
import com.linxcool.widget.CoverFlowAdapter;
import com.linxcool.widget.CoverFlowGallery;
import com.linxcool.widget.FloatWindow;
import com.linxcool.widget.RadialMenuWidget;
import com.linxcool.widget.RadialMenuWidget.RadialMenuEntry;
import com.linxcool.widget.Rotate3dView;
import com.linxcool.widget.ScaleArea;
import com.linxcool.widget.SelectView;
import com.linxcool.widget.SelectView.OnItemSelectedListener;
import com.linxcool.widget.SlipButton;

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
		initRadialMenuWidget(this);

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

	private RadialMenuWidget PieMenu;
	private FloatWindow radialMenu;
	
	void initRadialMenuWidget(Context context){

		class IconOnly implements RadialMenuEntry {
			public String getName() { return "IconOnly"; }
			public String getLabel() { return null; } 
			public int getIcon() { return R.drawable.ic_launcher; }
			public List<RadialMenuEntry> getChildren() { return null; }
			public void menuActiviated(){
				System.out.println( "IconOnly Menu Activated");
			}
		}

		class StringAndIcon implements RadialMenuEntry{
			public String getName() { return "StringAndIcon"; }
			public String getLabel() { return "String"; } 
			public int getIcon() { return R.drawable.ic_launcher; }
			public List<RadialMenuEntry> getChildren() { return null; }
			public void menuActiviated(){
				System.out.println( "StringAndIcon Menu Activated");
			}
		}

		class StringOnly implements RadialMenuEntry{
			public String getName() { return "StringOnly"; } 
			public String getLabel() { return "String\nOnly"; }
			public int getIcon() { return 0; }
			public List<RadialMenuEntry> getChildren() { return null; }
			public void menuActiviated(){
				System.out.println( "StringOnly Menu Activated");
			}
		}

		class Close implements RadialMenuEntry {
			public String getName() { return "Close"; }
			public String getLabel() { return null; }
			public int getIcon() { return android.R.drawable.ic_menu_close_clear_cancel; }
			public List<RadialMenuEntry> getChildren() { return null; }
			public void menuActiviated() {
				System.out.println("Close Menu Activated");
				radialMenu.dismiss();
			}
		}

		class Menu1 extends Close {
			public String getName() { return "Menu1 - No Children"; }
			public String getLabel() { return "Menu1\nTest"; }
			public int getIcon() { return 0; }
			public void menuActiviated() {
				System.out.println("Menu #1 Activated - No Children");
			}
		}

		class Menu2 implements RadialMenuEntry {
			public String getName() { return "Menu2 - Children"; }
			public String getLabel() { return "Menu2"; }
			public int getIcon() { return R.drawable.ic_launcher; }
			private List<RadialMenuEntry> children = new ArrayList<RadialMenuEntry>(
					Arrays.asList(new StringOnly(), new IconOnly(), new StringAndIcon()));
			public List<RadialMenuEntry> getChildren() {
				return children;
			}
			public void menuActiviated() {
				System.out.println("Menu #2 Activated - Children");
			}
		}

		class Menu3 implements RadialMenuEntry {
			public String getName() { return "Menu3 - No Children"; }
			public String getLabel() { return null; }
			public int getIcon() { return R.drawable.ic_launcher; }
			public List<RadialMenuEntry> getChildren() { return null; }
			public void menuActiviated() {
				System.out.println("Menu #3 Activated - No Children");
			}
		}

		class NewTestMenu implements RadialMenuEntry{
			public String getName() { return "NewTestMenu"; } 
			public String getLabel() { return "New\nTest\nMenu"; }
			public int getIcon() { return 0; }
			private List<RadialMenuEntry> children = new ArrayList<RadialMenuEntry>( Arrays.asList( new StringOnly(), new IconOnly() ) );
			public List<RadialMenuEntry> getChildren() { return children; }
			public void menuActiviated(){
				System.out.println( "New Test Menu Activated");
			}
		}

		class RedCircle implements RadialMenuEntry {
			public String getName() { return "RedCircle"; } 
			public String getLabel() { return "Red"; }
			public int getIcon() { return R.drawable.red_circle; }
			public List<RadialMenuEntry> getChildren() { return null; }
			public void menuActiviated(){
				System.out.println( "Red Circle Activated");
			}
		}

		class YellowCircle implements RadialMenuEntry{
			public String getName() { return "YellowCircle"; } 
			public String getLabel() { return "Yellow"; }
			public int getIcon() { return R.drawable.yellow_circle; }
			public List<RadialMenuEntry> getChildren() { return null; }
			public void menuActiviated(){
				System.out.println( "Yellow Circle Activated");
			}
		}

		class GreenCircle implements RadialMenuEntry{
			public String getName() { return "GreenCircle"; } 
			public String getLabel() { return "Green"; }
			public int getIcon() { return R.drawable.green_circle; }
			public List<RadialMenuEntry> getChildren() { return null; }
			public void menuActiviated(){
				System.out.println( "Green Circle Activated");
			}
		}

		class BlueCircle implements RadialMenuEntry{
			public String getName() { return "BlueCircle"; } 
			public String getLabel() { return "Blue"; }
			public int getIcon() { return R.drawable.blue_circle; }
			public List<RadialMenuEntry> getChildren() { return null; }
			public void menuActiviated(){
				System.out.println( "Blue Circle Activated");
			}
		}

		class CircleOptions implements RadialMenuEntry{
			public String getName() { return "CircleOptions"; } 
			public String getLabel() { return "Circle\nSymbols"; }
			public int getIcon() { return 0; }
			private List<RadialMenuEntry> children = new ArrayList<RadialMenuEntry>( Arrays.asList( new RedCircle(), new YellowCircle(), new GreenCircle(), new BlueCircle() ) );
			public List<RadialMenuEntry> getChildren() { return children; }
			public void menuActiviated(){
				System.out.println( "Circle Options Activated");
			}
		}	

		PieMenu = new RadialMenuWidget(getBaseContext());
		PieMenu.setAnimationSpeed(0L);
		//PieMenu.setSourceLocation(100,100);
		//PieMenu.setCenterLocation(240,400);
		//PieMenu.setInnerRingRadius(50, 120);
		//PieMenu.setInnerRingColor(Color.LTGRAY, 255);
		//PieMenu.setHeader("Menu Header", 20);

		PieMenu.setSourceLocation(100,100);
		PieMenu.setIconSize(15, 30);
		PieMenu.setTextSize(13);				

		PieMenu.setCenterCircle(new Close());
		PieMenu.addMenuEntry(new Menu1());
		PieMenu.addMenuEntry(new NewTestMenu());
		PieMenu.addMenuEntry(new CircleOptions());
		PieMenu.addMenuEntry(new Menu2());
		PieMenu.addMenuEntry(new Menu3());

		radialMenu = new FloatWindow(context);
		radialMenu.show(PieMenu);
	}

	public boolean onTouchEvent(android.view.MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_UP){
		}
		return super.onTouchEvent(event);
	};
}

