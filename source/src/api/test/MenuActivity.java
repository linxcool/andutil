package api.test;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linxcool.util.R;
import com.linxcool.widget.ResideMenu;
import com.linxcool.widget.ResideMenuItem;
import com.linxcool.widget.SpringbackListView;

public class MenuActivity extends ActionBarActivity implements View.OnClickListener,ResideMenu.OnMenuListener{

	private ResideMenu resideMenu;
	private ResideMenuItem itemHome;
	private ResideMenuItem itemSetting;
	private ResideMenuItem itemCalendar;
	private ResideMenuItem itemProfile;

	SpringbackListView springbackListView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reside_menu);

		initResideMenu();
		
		initListView();
		
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_action_bar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void initResideMenu() {
		resideMenu = new ResideMenu(this);
		resideMenu.setBackground(R.drawable.menu_background);
		resideMenu.attachToActivity(this);
		resideMenu.setMenuListener(this);

		itemHome = new ResideMenuItem(this, R.drawable.ic_home, "首页");
		resideMenu.addMenuItem(itemHome);
		itemHome.setOnClickListener(this);
		
		itemCalendar = new ResideMenuItem(this, R.drawable.ic_calendar, "日历");
		resideMenu.addMenuItem(itemCalendar);
		itemCalendar.setOnClickListener(this);
		
		itemProfile = new ResideMenuItem(this, R.drawable.ic_profile, "简介");
		resideMenu.addMenuItem(itemProfile);
		itemProfile.setOnClickListener(this);
		
		itemSetting = new ResideMenuItem(this, R.drawable.ic_setting, "返回");
		resideMenu.addMenuItem(itemSetting);
		itemSetting.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		if (view == itemSetting){
			resideMenu.closeMenu();
		}
		else if(view == itemHome){
			finish();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home){
			finish();
			overridePendingTransition(R.anim.anim_l2r_in,R.anim.anim_l2r_out);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return resideMenu.onInterceptTouchEvent(ev) || super.dispatchTouchEvent(ev);
	}

	@Override
	public void onOpenMenu() {
		//Empty
	}

	@Override
	public void onCloseMenu() {
		//Empty
	}

	private void initListView(){
		springbackListView = (SpringbackListView) findViewById(R.id.springbackListView);
		springbackListView.setAdapter(new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView tv = new TextView(getApplicationContext());
				tv.setHeight(dp2px(40));
				tv.setGravity(Gravity.CENTER_VERTICAL);
				tv.setBackgroundColor(Color.WHITE);
				tv.setPadding(dp2px(10), 0, dp2px(10), 0);
				tv.setTextColor(Color.parseColor("#2FA7D3"));
				tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
				tv.setText(getItem(position).toString());
				return tv;
			}
			
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				return "> LINXCOOL|" + (position<10?"0"+position:position);
			}
			
			@Override
			public int getCount() {
				return 100;
			}
			
			int dp2px(float dp){
				float density = getResources().getDisplayMetrics().density;
				return (int) (dp * density + 0.5);
			}
		});
	}
}
