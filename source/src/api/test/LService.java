package api.test;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * 服务
 * <p><b>Time:</b> 2014-2-14
 * @author 胡昌海(Linxcool.Hu)
 */
public class LService extends Service implements Callback{

	private static final String TAG = "LService";

	public static final int MSG_CLIENT_MESSENGER = 0x1001;
	public static final int MSG_SEEND_MSG = 0x1002;

	Messenger sMessenger;
	Messenger cMessenger;

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate()");
		sMessenger = new Messenger(new Handler(this));
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "onBind()");
		return sMessenger.getBinder();
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_CLIENT_MESSENGER:
			cMessenger = msg.replyTo;
			break;
		case MSG_SEEND_MSG:
			Intent data = (Intent) msg.obj;
			Log.i(TAG, "get message : " + data.getStringExtra("data"));
			
			if(cMessenger == null){
				Log.i(TAG, "no client messenger");
				break;
			}
			
			Log.i(TAG, "try send message to client");
			try {
				msg = Message.obtain();
				data = new Intent();
				data.putExtra("data", "i received you message");
				msg.obj = data;
				msg.what = LService.MSG_SEEND_MSG;
				cMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}

		return false;
	}

}
