package api.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.linxcool.app.BaseActivity;
import com.linxcool.util.R;

public class MainActivity extends BaseActivity implements OnClickListener{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.rotateImageView1).setOnClickListener(this);
		findViewById(R.id.rotateImageView2).setOnClickListener(this);
		findViewById(R.id.rotateImageView3).setOnClickListener(this);
		findViewById(R.id.rotateImageView4).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rotateImageView1:
			Intent intent = new Intent(this, UiActivity.class);
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out); 
			break;
		case R.id.rotateImageView2:
			break;
		case R.id.rotateImageView3:
			intent = new Intent(this, AlbumsActivity.class);
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
			break;
		case R.id.rotateImageView4:
			intent = new Intent(this, LActivity.class);
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
			break;
		default:
			break;
		}
	}
}
