package api.test;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.linxcool.app.BaseActivity;
import com.linxcool.util.R;

public class LActivity extends BaseActivity implements Callback, ServiceConnection, OnClickListener{

	private static final String TAG = "LClient";

	Messenger sMessenger;
	boolean connected;

	Messenger cMessenger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v = getLayoutInflater().inflate(R.layout.activity_blank, null);
		setContentView(v);

		enabledHomeBack();

		cMessenger = new Messenger(new Handler(this));
		
		v.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		Intent service = new Intent(this, LService.class);
		boolean rs = bindService(service, this, Context.BIND_AUTO_CREATE);
		Log.i(TAG, "bindService " + rs);
		super.onStart();
	}

	@Override
	protected void onStop() {
		unbindService(this);
		super.onStop();
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		Log.i(TAG, "onServiceConnected()");

		sMessenger = new Messenger(service);

		try {
			Message msg = new Message();
			msg.what = LService.MSG_CLIENT_MESSENGER;
			msg.replyTo = cMessenger;
			sMessenger.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		connected = true;
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.i(TAG, "onServiceDisconnected()");
		sMessenger = null;
		connected = false;
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch ( msg.what ) {
		case LService.MSG_SEEND_MSG:
			Intent data = (Intent) msg.obj;
			Log.i(TAG, "get message : " + data.getStringExtra("data"));
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		if(!connected){
			Log.i(TAG, "service unconnected");
			return;
		}

		try {
			Log.i(TAG, "try send message to service");

			Message msg = Message.obtain();
			
			Intent data = new Intent();
			data.putExtra("data", "i come from LActivity");
			msg.obj = data;
			msg.what = LService.MSG_SEEND_MSG;
			sMessenger.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
