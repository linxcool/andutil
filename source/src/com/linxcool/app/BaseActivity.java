package com.linxcool.app;

import android.os.Bundle;
import android.view.MenuItem;

import com.linxcool.util.R;

public class BaseActivity extends SwipeBackActivity {

	protected boolean homeBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	protected void enabledHomeBack(){
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		homeBack = true;
	}
	
	protected int dp2px(float dp){
		float density = getResources().getDisplayMetrics().density;
		return (int) (dp * density + 0.5f);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(homeBack && item.getItemId() == android.R.id.home){
			finish();
			overridePendingTransition(R.anim.anim_l2r_in,R.anim.anim_l2r_out);
		}
		return super.onOptionsItemSelected(item);
	}
}
