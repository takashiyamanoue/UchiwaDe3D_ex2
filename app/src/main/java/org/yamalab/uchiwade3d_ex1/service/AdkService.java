package org.yamalab.uchiwade3d_ex1.service;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbAccessory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.yamalab.uchiwade3d_ex1.R;
import org.yamalab.uchiwade3d_ex1.AdkTwitterActivity;
import org.yamalab.uchiwade3d_ex1.twitterconnector.TwitterApplication;
import org.yamalab.uchiwade3d_ex1.twitterconnector.TwitterController;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

//import com.android.future.usb.UsbAccessory;

public class AdkService extends Service
		implements TwitterApplication, LocationListener {

	AdkThread demoKitActivity;
	private static final String TAG = "AdkService";

	public static final String ACTION_USB_PERMISSION = "com.google.android.DemoKit.action.USB_PERMISSION";

	UsbAccessory mAccessory;
	ParcelFileDescriptor mFileDescriptor;
	public FileInputStream mInputStream;
	public FileOutputStream mOutputStream;
	Intent callerIntent;
	private AdkThread thread;
	boolean mBound;
	//	PukiWikiConnectorService connectorService;
	public long readCommandInterval = 0; // default 10 min.
	public long sendResultInterval = 0; // default 10 min.
	String pageName = "";

	BinaryFileReader binaryFileReader;
	private FontAccessor fontAccessor;
	private FontTransmitter fontTransmitter;

	private TwitterController mTwitterController;
	private LocationManager locationManager;    // ���������������������������������
	private long locationSendingInterval;
	private String locProvider;

	public SdLog sdLog;

	@Override
//	public void onStart(Intent intent, int startId) {
	public int onStartCommand(Intent intent, int flag, int startId) {
		/* */
		callerIntent = intent;
		//Log.d(TAG, "onStartCommand start-" + flag + "-" + startId);
		sdLog.put(TAG+"-"+"onStartCommand start-" + flag + "-" + startId);
		Toast.makeText(this, "start Service/GPS location sending.-" + flag + "-" + startId, Toast.LENGTH_SHORT).show();

		// ���������������������������������������������������������������������
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Criteriaオブジェクトを生成
		Criteria criteria = new Criteria();
		// Accuracyを指定(低精度)
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		// PowerRequirementを指定(低消費電力)
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		// ロケーションプロバイダの取得
		locProvider = locationManager.getBestProvider(criteria, true);

		// 取得したロケーションプロバイダを表示
//		TextView tv_provider = (TextView) findViewById(R.id.Provider);
//		tv_provider.setText("Provider: "+provider);
		Toast.makeText(this, "Location provider:" + locProvider, Toast.LENGTH_SHORT).show();

		// ���������������������������������������������������
		// GPSが利用可能になっているかどうかをチェック
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locProvider = LocationManager.GPS_PROVIDER;
		}
		// GPSプロバイダーが有効になっていない場合は基地局情報が利用可能になっているかをチェック
		else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locProvider = LocationManager.NETWORK_PROVIDER;
		}
		// いずれも利用可能でない場合は、GPSを設定する画面に遷移する
		else {
			//Log.d("GPS Enabled", "NG");
			sdLog.put("GPS Enabled NG");
//			Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//			startActivity(settingsIntent);
			return START_STICKY;
		}
		//Log.d("GPS Enabled", "OK");
		sdLog.put("GPS Enabled OK");

		LocationListener myLocationListener = new LocationListener() {
			/*
			@Override
			public void onLocationChanged(Location location) {
				Log.d("dj","on location changed: "+location.getLatitude()+" & "+location.getLongitude());
//				toastLocation(location);
			}
			*/
			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				if (locationSendingEnabled) {
					String latitude = Double.toString(location.getLatitude());
					String longitude = Double.toString(location.getLongitude());
					sendCommandToTwitter("tweet",
							"http://maps.google.co.jp/maps?" +
									"f=q&hl=ja&geocode=&q=" + latitude + "," + longitude
					);
				}
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {

			}

			@Override
			public void onProviderEnabled(String provider) {

			}

			@Override
			public void onProviderDisabled(String provider) {

			}
		};
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			//Log.d("GPS Enabled", "Manifest.permission.ACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED");
			sdLog.put("GPS Enabled-"+"Manifest.permission.ACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED");
			return START_STICKY;
		}
		locationManager.requestLocationUpdates(locProvider, //
				300000L, // ������������������������������������(milli sec.)
				0.0F,
				myLocationListener); // ������������������������
		/* */
		return START_STICKY;
	}

	/** For showing and hiding our notification. */
	NotificationManager mNM;
//    private int NOTIFICATION = R.string.local_service_started;  	
	/** Keeps track of all current registered clients. */
	ArrayList<Messenger> mClients = new ArrayList<Messenger>();
	/** Holds last value set by a client. */
	int mValue = 0;
	/*
	 * Command to the service to register a client, receiving callbacks
	 * from the service.  The Message's replyTo field must be a Messenger of
	 * the client where callbacks should be sent.
	 */
	public static final int MSG_REGISTER_CLIENT = 1;
	/*
	 * Command to the service to unregister a client, ot stop receiving callbacks
	 * from the service.  The Message's replyTo field must be a Messenger of
	 * the client as previously given with MSG_REGISTER_CLIENT.
	 */
	public static final int MSG_UNREGISTER_CLIENT = 2;
	/*
	 * Command to service to set a new value.  This can be sent to the
	 * service to supply a new value, and will be sent by the service to
	 * any registered clients with the new value.
	 */
	public static final int MSG_SET_VALUE = 3;
	/* stop */
	public static final int MSG_STOP = 4;
	/* file descriptor */
	public static final int MSG_SET_USBFILEDESCRIPTOR = 5;
	/* commands */
	public static final int MSG_EXEC_COMMAND = 6;

	/**     * Handler of incoming messages from clients.     */
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_REGISTER_CLIENT:
					Log.d(TAG, "handleMessage-MSG_REGISTER_CLIENT");
					mClients.add(msg.replyTo);
					break;
				case MSG_UNREGISTER_CLIENT:
					Log.d(TAG, "handleMessage-MSG_UNREGISTER_CLIENT");
					mClients.remove(msg.replyTo);
					break;
				case MSG_SET_VALUE:
					Log.d(TAG, "handleMessage-MSG_SET_VALUE");
					mValue = msg.arg1;
					for (int i = mClients.size() - 1; i >= 0; i--) {
						try {
							mClients.get(i).send(Message.obtain(null,
									MSG_SET_VALUE, mValue, 0));
						} catch (RemoteException e) {
							// The client is dead.  Remove it from the list;
							// we are going through the list from back to front
							// so this is safe to do inside the loop.
							mClients.remove(i);
						}
					}
					break;
				case MSG_SET_USBFILEDESCRIPTOR:
					//Log.d(TAG, "handleMessage-MSG_SET_USBFILEDESCRIPTOR");
					sdLog.put(TAG+"-"+"handleMessage-MSG_SET_USBFILEDESCRIPTOR");
					mFileDescriptor = (ParcelFileDescriptor) (msg.obj);
					if (mFileDescriptor != null) {
						FileDescriptor fd = mFileDescriptor.getFileDescriptor();
						mInputStream = new FileInputStream(fd);
						mOutputStream = new FileOutputStream(fd);
					}
					startThread();
					break;
				case MSG_STOP:
					//Log.d(TAG, "handleMessage-MSG_REGISTER_CLIENTonCreate");
					sdLog.put(TAG+" - "+"handleMessage-MSG_REGISTER_CLIENTonCreate");
					onDestroy();
					break;
				case MSG_EXEC_COMMAND:
					//Log.d(TAG, "handleMessage-MSG_EXEC_COMMAND");
					sdLog.put(TAG+" - "+"handleMessage-MSG_EXEC_COMMAND" );
					StringMsg cmd = (StringMsg) (msg.obj);
					parseCommand(cmd);
				default:
					super.handleMessage(msg);
			}
		}
	}

	public void sendMessage(Message m) {
		if (mClients == null) return;
		if (!isBound()) return;
		for (int i = mClients.size() - 1; i >= 0; i--) {
			try {
				mClients.get(i).send(m);
			} catch (RemoteException e) {
				// The client is dead.  Remove it from the list;
				// we are going through the list from back to front
				// so this is safe to do inside the loop.
				mClients.remove(i);
			}
		}
	}

	private static final String mActivityName = AdkTwitterActivity.class.getCanonicalName();

	public boolean isActivityRunning() {
		//Log.d(TAG, "isActivityRunning");
		sdLog.put("TAG"+" - "+"isActivityRunning");
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningApp = activityManager.getRunningServices(Integer.MAX_VALUE);
		for (RunningServiceInfo info : runningApp) {
			if (mActivityName.equals(info.service.getClassName())) {
				//Log.d(TAG, "isActivityRunning ... true");
				sdLog.put(TAG+ " - "+"isActivityRunning ... true");
				return true;
			}
		}
		//Log.d(TAG, "isActivityRunning ... false");
		sdLog.put(TAG+ " - "+"isActivityRunning ... false");
		return false;
	}

	/*
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger mMessenger = new Messenger(new IncomingHandler());

	@Override
	public void onCreate() {
		sdLog=new SdLog();
		//Log.d(TAG, "onCreate");
		sdLog.put(TAG+"-onCreate");
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Display a notification about us starting.
		showNotification();
//  		connectUsb();
//        connectorService=new PukiWikiConnectorService(this);
  		/*
		if(binaryFileReader==null){
	  		   binaryFileReader=new BinaryFileReader(this,"ShinonomeMin_16.pdb");
	  		  fontAccessor=new FontAccessor(binaryFileReader.getBinaryArray(), 16, 0x00F0);
		}  		
  		mTwitterController=new TwitterController(this,this);
  		*/
		this.thread = null;
	}

	@Override
	public void onDestroy() {
		// Cancel the persistent notification.
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		this.mBound = false;
//		this.doUnbindService();
		if (this.thread != null) {
			this.thread.stop();
		}
		if (this.mInputStream != null) {
			try {
				mInputStream.close();
			} catch (Exception e) {
				//Log.d(TAG, "onDestroy-mInputStream.close error:" + e.toString());
				sdLog.put(TAG+" - "+ "onDestroy-mInputStream.close error:" + e.toString());
			}
		}
		if (this.mOutputStream != null) {
			try {
				mOutputStream.close();
			} catch (Exception e) {
				//Log.d(TAG, "onDestroy-mOutputStream.close error:" + e.toString());
				sdLog.put(TAG+" - "+"onDestroy-mOutputStream.close error:" + e.toString());
			}
		}
		if (this.mFileDescriptor != null) {
			try {
				mFileDescriptor.close();
			} catch (Exception e) {
				//Log.d(TAG, "onDestroy-mFileDescriptor.close error:" + e.toString());
				sdLog.put(TAG+" - "+"onDestroy-mFileDescriptor.close error:" + e.toString());
			}
		}
//  		this.closeAccessory();
		if (mMessenger != null) {
//  			mMessenger
		}
		mNM.cancel(0);

		// Tell the user we stopped.
		Toast.makeText(this, "remote_service_stopped", Toast.LENGTH_SHORT).show();
		Toast.makeText(this, "Service has been terminated.", Toast.LENGTH_SHORT).show();
//		unregisterReceiver(mUsbReceiver);
		super.onDestroy();
	}

	/*
	 * When binding to the service, we return an interface to our messenger
	 * for sending messages to the service.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		this.mBound = true;
		callerIntent = intent;
		//Log.d(TAG, "onBind");
		sdLog.put(TAG+" - onBind");
		/*
		if(this.mFileDescriptor==null){
			this.connectUsb();
		}
		*/
//        this.startThread();

		return mMessenger.getBinder();
	}

	/*
	 * Show a notification while this service is running.
	 */
	private void showNotification() {
		// In this sample, we'll use the same text for the ticker and the expanded notification
		//CharSequence text = getText(R.string.remote_service_started);
		CharSequence text = "remote_service_started";
		// Set the icon, scrolling text and timestamp
//  		Notification notification = new Notification(R.drawable.stat_sample, text,
//  		System.currentTimeMillis());
		/*
		Notification notification = new Notification(drawable.indicator_button1_off_noglow, text,
				System.currentTimeMillis());
		// The PendingIntent to launch our activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, AdkTwitterActivity.class), 0);
		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this, "AdkService",
				text, contentIntent);
		// Send the notification.
		// We use a string id because it is a unique number.  We use it later to cancel.
		mNM.notify(0, notification);
		*/
		Intent notificationIntent = new Intent(this, AdkTwitterActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

//		Notification notification = new NotificationCompat.Builder(this)
		Notification notification = new NotificationCompat.Builder(this, "AdkService")
//		Notification notification = new NotificationCompat.Builder(getApplicationContext())
				.setSmallIcon(R.drawable.indicator_button1_off_noglow) // アイコン
				.setTicker("AdkService") // 通知バーに表示する簡易メッセージ
				.setWhen(System.currentTimeMillis()) // 時間
				.setContentTitle("AdkService") // 展開メッセージのタイトル
				.setContentText("AdkService") // 展開メッセージの詳細メッセージ
				.setContentIntent(contentIntent) // PendingIntent
				.build();


		mNM.notify(0, notification);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		super.onUnbind(intent);
		//Log.d(TAG, "onUnbind");
		sdLog.put(TAG+" - onUnbind");
		this.mBound = false;
		return true;
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		//Log.d(TAG, "onRebind");
		sdLog.put(TAG+" - onRebind");
		/*
		if(this.mFileDescriptor==null){
			this.connectUsb();
		}
		*/
//		this.startThread();
		this.mBound = true;
	}

	public boolean isBound() {
		return this.mBound;
	}

	public void startThread() {
		//Log.d(TAG, "startThread");
		sdLog.put(TAG+" - startThread");
		this.sendCommandToActivity("activity request setting", "");
		if (binaryFileReader == null) {
			binaryFileReader = new BinaryFileReader(this, "ShinonomeMin_16.pdb");
			fontAccessor = new FontAccessor(binaryFileReader.getBinaryArray(), 16, 0x00F0);
		}
		if (fontTransmitter == null) {
			fontTransmitter = new FontTransmitter(this);
		}
		if (mTwitterController == null) {
			mTwitterController = new TwitterController(this, this);
			mTwitterController.setSdLog(sdLog);
		}
		if (thread == null) {
			//Log.d(TAG, "onBind-new thread");
			sdLog.put(TAG+" - onBind-new thread");

			thread = new AdkThread(this); //(this)
			//Log.d(TAG, "onBind-start AdkThread");
			sdLog.put(TAG+" - onBind-start AdkThread");
			thread.start();
		}

	}

	protected boolean parseCommand(StringMsg m) {
		if (m == null) return false;
		String x = m.getCommand();
		//Log.d(TAG, "parseCommand(" + x + ")");
		sdLog.put(TAG+" - "+"parseCommand(" + x + ")");
//		this.showLog(TAG + "-parseCommand(" + x + ")");
		String cmd = Util.skipSpace(x);
		String[] rest = new String[1];
		String[] match = new String[1];
		int[] intv = new int[1];
		if (Util.parseKeyWord(cmd, "twitter ", rest)) {
			String subcmd = Util.skipSpace(rest[0]);
			/* */
			sendCommandToTwitter(subcmd, m.getValue());
		    /*
			if (mTwitterController != null) {
				return mTwitterController.parseCommand(subcmd, m.getValue());
			}		    	
		  *	*/
		} else if (Util.parseKeyWord(cmd, "adk ", rest)) {
			String subcmd = Util.skipSpace(rest[0]);
			if (subcmd.startsWith("#")) return true;
			if (subcmd.startsWith("command:")) {
				String command = subcmd.substring("command:".length());
				command = Util.skipSpace(command);
				boolean rtn = parseAdkCommand(command);
				return rtn;
			}
			if (subcmd.startsWith("send:")) {
				String command = subcmd.substring("send:".length());
				command = Util.skipSpace(command);
				boolean rtn=false;
				if(Util.parseKeyWord(command,"bitmap",rest)) {
					rtn = sendBitmap2Device(m.getValue());
				}
				return rtn;
			}
		} else if (Util.parseKeyWord(cmd, "service ", rest)) {
			String subcmd = Util.skipSpace(rest[0]);
			if (subcmd.startsWith("#")) return true;
			return parseServiceCommand(subcmd, m.getValue());
		}
		return false;
	}

	public void showLog(String x) {
		this.sendCommandToActivity("log ", x);
	}

	public void sendCommandToActivity(String c, String v) {
		// TODO Auto-generated method stub
		//Log.d(TAG, "sendCommandToActivity(" + c + "," + v + ")");
		sdLog.put(TAG+" - "+"sendCommandToActivity(" + c + "," + v + ")");
		StringMsg mx = new StringMsg(c, v);
		Message msg = Message.obtain(null, AdkThread.MESSAGE_STRING, mx);
		sendMessage(msg);
	}

	public void outputDevice(byte command, byte target, int value) {
		//Log.d(TAG, "outputDevice(" + command + "-" + target + "," + value + ")");
		sdLog.put(TAG+" - "+"outputDevice(" + command + "-" + target + "," + value + ")");
		byte[] buffer = new byte[64];
		if (value > 255)
			value = 255;

		buffer[0] = command;
		buffer[1] = target;
		buffer[2] = (byte) value;
		if (mOutputStream != null && buffer[1] != -1) {
			try {
				mOutputStream.write(buffer);
				mOutputStream.flush();
			} catch (IOException e) {
				Log.e(TAG, "write failed", e);
			}
		}
		else{
			this.dumpBinary(buffer);
		}
	}
	public boolean sendBitmap2Device(String x) {
		//Log.d(TAG, "outputDevice(" + command + "-" + target + "," + value + ")");
		int xlength=x.length();
		if(xlength<=0) return false;
		if(xlength<6){
			if(x.startsWith("b-r")||x.startsWith("b-n")){
				byte[] buffer = new byte[6];
				for(int i=0;i<xlength;i++){
					buffer[i]=(byte)(x.charAt(i));
				}
				if (mOutputStream != null && buffer[1] != -1) {
					try {
						mOutputStream.write(buffer);
						mOutputStream.flush();
					} catch (IOException e) {
						Log.e(TAG, "write failed", e);
						return false;
					}
				}
				else{
					this.dumpBinary(buffer);
					return false;
				}
				return true;
			}
			return false;
		}
		sdLog.put(TAG+" - "+"sendBitmap2Device( len="+xlength+"," + x + ")");
		byte[] buffer = new byte[1024];
		if(xlength>1024) return false;
		for(int i=0;i<xlength;i++){
			buffer[i]=(byte)(x.charAt(i));
		}
		if (mOutputStream != null && buffer[1] != -1) {
			try {
				mOutputStream.write(buffer);
				mOutputStream.flush();
			} catch (IOException e) {
				Log.e(TAG, "write failed", e);
				return false;
			}
		}
		else{
			this.dumpBinary(buffer);
			return false;
		}
		return true;
	}
	/*
	 *  16x16 -> 8x16?
	 */
	public void outputCommandToDevice(byte[] fa) {
		byte[] buffer = new byte[64];
		if (fa == null) {
			showLog(TAG + "-outputCommandToDevice, command seems null.");
			return;
		}
		for (int i = 0; i < 5; i++) {
			buffer[i] = (byte) (fa[i]);
		}
		if (mOutputStream != null && buffer[1] != -1) {
			try {
				mOutputStream.write(buffer);
				mOutputStream.flush();
			} catch (IOException e) {
//				Log.e(TAG, "write failed", e);
				showLog(TAG+" - "+"write failed"+e);
			}
		}
		else{
			this.dumpBinary(buffer);
		}

	}

	void dumpBinary(byte[] f){
		if(f==null) {
			System.out.println("read error");
			return;
		}
		long adr=0;
		String s="";
		String c="";
		int size=f.length;
		char[] cs=new char[1];
		while(adr<=size){
			long a=adr;
//        for(int y=0;y<16;y++) {
			if(a<size) {
				s= Integer.toHexString((int)a).toUpperCase();
				s="00000000".substring(s.length())+s;
			} else {
				s="        ";
			}
			System.out.print(s);
			String sline="";
			String cline="";
			for(int x=0;x<16;x++ ) {
				if(a<size) {
					int d=((int)f[(int)a]) & 0xff;
					String ss= Integer.toHexString(d).toUpperCase();
					sline=sline+"00".substring(ss.length())+ss+" ";

					if((d<0x20) || (d>0xde) ||
							((d>0x7e) && (d<0xa1))) { d=0x2e; }
					cs[0]=(char)d;
					cline = cline+new String(cs)+" ";
				} else {
					s="  ";
					c=" ";
				}
				a++;
			}
			System.out.print(" "+sline);
			System.out.println(" "+cline);
			adr=a;
		}
//      }
	}


	public void outputFontToDevice(byte[] fa) {
		byte[] buffer = new byte[64];
		if (fa == null) {
			showLog(TAG + "-outputFontToDevice, font seems null.");
			return;
		}
		int fontKind = 2; // default font kind = 16 bit width
		if (fa.length <= 16)
			fontKind = 1; // if font size <=16byte, font kind = 8 bit width

		buffer[0] = 'f'; // sending font
		buffer[1] = (byte) (fontKind); // font kind
		if (fontKind == 2) {
			for (int j = 0; j < 32; j++) {
				buffer[2 + j] = fa[j];
			}
		} else {
			for (int j = 0; j < 16; j++) {
				buffer[2 + j] = fa[j];
			}
		}
		if (mOutputStream != null && buffer[1] != -1) {
			try {
				mOutputStream.write(buffer);
			} catch (IOException e) {
				Log.e(TAG, "write failed", e);
			}
		}
		else{
			printFontToLog(fa);
		}

	}

	public void printFontToLog(byte[] f) {
		if (f == null) return;
		if (this.fontAccessor == null) return;
		if (f.length == 16) {
			printAsciiFontToLog(f);
			return;
		}
		String fline = "";
		int w = (fontAccessor.oneFontSize) / 8;
		for (int i = 0; i < (fontAccessor.oneFontSize); i++) {
			for (int j = 0; j < w; j++) {
				int l = f[i * w + j];
				int mask = 0x0080;
				for (int k = 0; k < 8; k++) {
					if ((mask & l) != 0)
						fline = fline + "*";
					else
						fline = fline + " ";
					mask = mask >> 1;
				}
			}
			Log.d(TAG, fline);
			fline = "";
		}
		Log.d(TAG, "");
	}

	public void printAsciiFontToLog(byte[] f) {
		if (this.fontAccessor == null) return;
		String fline = "";
		if (f == null) return;
		for (int i = 0; i < (fontAccessor.oneFontSize); i++) {
			int l = f[i];
			int mask = 0x0080;
			for (int k = 0; k < 8; k++) {
				if ((mask & l) != 0)
					fline = fline + "*";
				else
					fline = fline + " ";
				mask = mask >> 1;
			}
			Log.d(TAG, fline);
			fline = "";
		}
		Log.d(TAG, "");
	}

	String saveText = "";
	int uploadInterval = 10;
	int uploadNumber;
	Vector<String> reports = new Vector();
	int reportQueueLength = 120;

	public void putSendBuffer(String x) {
		//Log.d(TAG, "putSendBuffer(" + x + ")");
		sdLog.put(TAG+" - "+ "putSendBuffer(" + x + ")");
		this.sendCommandToActivity("activity append output", x);
//		mPirOutputView.append(line);
		reports.add(x);
		this.sendCommandToActivity("connector setMessage", "");
//		mMessageView.setText("");
		uploadNumber++;
	}

	public boolean parseAdkCommand(String line) {
		String[] rest = new String[1];
		String cmd = Util.skipSpace(line);
//		Log.d(TAG,"parseInputCommand-"+cmd);
//		this.showLog(TAG+"-parseInputCommand-"+cmd);
		if (Util.parseKeyWord(cmd, "get ", rest)) {
//			return this.parseGetCommand(rest[0]);
		} else if (Util.parseKeyWord(cmd, "set ", rest)) {
			return this.parseSetCommand(rest[0]);
		}
		return false;
	}

	public boolean parseServiceCommand(String line, String v) {
		String[] rest = new String[1];
		String cmd = Util.skipSpace(line);
		//Log.d(TAG, "parseInputCommand-" + cmd);
		sdLog.put(TAG+" - "+"parseInputCommand-" + cmd);
//		this.showLog(TAG + "-parseInputCommand-" + cmd);
		if (Util.parseKeyWord(cmd, "set ", rest)) {
			String param = Util.skipSpace(rest[0]);
			if (param.equals("fontRequest")) {
				if (fontTransmitter != null) {
//					fontTransmitter.setSendEnable(v);
				}
			} else if (param.equals("locationSendingEnabled")) {
				Toast.makeText(this, "locationSendingEnabled.-" + v, Toast.LENGTH_SHORT).show();
				String s = Util.skipSpace(rest[0]);
//					this.outputMessageToDevice(s);
				if (v.equals("true")) {
					this.locationSendingEnabled = true;
				} else {
					this.locationSendingEnabled = false;
					return true;
				}
			} else if (param.equals("sendLocInterval")) {
				Toast.makeText(this, "sendLocInterval.-" + v, Toast.LENGTH_SHORT).show();
				String s = Util.skipSpace(rest[0]);
				locationSendingInterval = getMilisec(v);
				// GPSが利用可能になっているかどうかをチェック
				if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					locProvider = LocationManager.GPS_PROVIDER;
				}
				// GPSプロバイダーが有効になっていない場合は基地局情報が利用可能になっているかをチェック
				else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
					locProvider = LocationManager.NETWORK_PROVIDER;
				}
				// いずれも利用可能でない場合は、GPSを設定する画面に遷移する
				else {
					//Log.d("GPS Enabled", "NG");
					sdLog.put("GPS Enabled - NG");
					return true;
//			Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//			startActivity(settingsIntent);
//			return 0;
				}
				//Log.d("GPS Enabled", "OK");
				sdLog.put("GPS Enabled - OK");

				LocationListener myLocationListener = new LocationListener() {
					/*
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.d("dj","on location changed: "+location.getLatitude()+" & "+location.getLongitude());
        //				toastLocation(location);
                    }
                    */
					@Override
					public void onLocationChanged(Location location) {
						// TODO Auto-generated method stub
						if (locationSendingEnabled) {
							String latitude = Double.toString(location.getLatitude());
							String longitude = Double.toString(location.getLongitude());
							sendCommandToTwitter("tweet",
									"http://maps.google.co.jp/maps?" +
											"f=q&hl=ja&geocode=&q=" + latitude + "," + longitude
							);
						}
					}

					@Override
					public void onStatusChanged(String provider, int status, Bundle extras) {

					}

					@Override
					public void onProviderEnabled(String provider) {

					}

					@Override
					public void onProviderDisabled(String provider) {

					}
				};
				if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					// TODO: Consider calling
					//    ActivityCompat#requestPermissions
					// here to request the missing permissions, and then overriding
					//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
					//                                          int[] grantResults)
					// to handle the case where the user grants the permission. See the documentation
					// for ActivityCompat#requestPermissions for more details.
					Log.d("GPS Enabled", "Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED");
					return true;
				}
				locationManager.requestLocationUpdates(locProvider, //
						locationSendingInterval, // ������������������������������������(milli sec.)
						0.0F,
						myLocationListener); // ������������������������
					Toast.makeText(this, "Location sending interval:" + v, Toast.LENGTH_SHORT).show();
				return true;
			}

			return true;
		}
		else
		if(cmd.equals("send")){
			if(v.equals("geocode")){
				
			}
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean parseCommand(String line, String v){
		String[] rest=new String[1];
		String cmd=Util.skipSpace(line);
		//Log.d(TAG,"parseInputCommand-"+cmd);
		sdLog.put(TAG+" - "+"parseInputCommand-"+cmd);
//		this.showLog(TAG+"parseCommand("+line+","+v);
		if(Util.parseKeyWord(cmd, "activity ", rest)){
			String subcmd=Util.skipSpace(rest[0]);
			this.sendCommandToActivity(subcmd, v);
			return true;
		}
		else
		if(Util.parseKeyWord(cmd, "adk ", rest)){
			String subcmd=Util.skipSpace(rest[0]);
			this.parseAdkCommand(subcmd+" "+v);
			return true;
		}
		else
		if(Util.parseKeyWord(cmd, "service ", rest)){
			String subcmd=Util.skipSpace(rest[0]);
			this.parseServiceCommand(subcmd,v);
			return true;
		}
		return false;
	}


    char[] devKind=new char[]{'a','d'};
	boolean parseSetCommand(String x){
		String[] rest=new String[1];
		int[] intv1=new int[1];
		int[] intv2=new int[1];
		char[] chrtn=new char[1];
		String cmd=Util.skipSpace(x);
		//Log.d(TAG,"parseSetCommand-"+cmd);
		sdLog.put(TAG+" - "+"parseSetCommand-"+cmd);
		if(Util.parseKeyWord(cmd,"out-",rest)){
			String v1=rest[0];
			if(!Util.parseChar(v1, devKind, chrtn, rest)) return false;
			char dc=chrtn[0]; // a (analog) or d (digital)
			String s2=rest[0];
			if(!Util.parseKeyWord(s2, "-", rest)) return false;
			s2=rest[0];
			if(!Util.parseInt(s2, intv1, rest)) return false;
			int port=intv1[0];
			v1=Util.skipSpace(rest[0]);
			if(!Util.parseKeyWord(v1, "=", rest)) return false;
			v1=Util.skipSpace(rest[0]);
			if(!Util.parseInt(v1, intv2, rest)) return false;
			this.outputDevice((byte)dc,(byte)port,(byte)(intv2[0]));
			this.sendCommandToActivity("activity set device "+dc+" "+port+" "+intv2[0], "");
			return true;
		}
		else
		if(Util.parseKeyWord(cmd,"xout-",rest)){
			String v1=rest[0];
			if(!Util.parseChar(v1, devKind, chrtn, rest)) return false;
			char dc=chrtn[0]; // a (analog) or d (digital)
			String s2=rest[0];
			if(!Util.parseKeyWord(s2, "-", rest)) return false;
			s2=rest[0];
			if(!Util.parseInt(s2, intv1, rest)) return false;
			int port=intv1[0];
			v1=Util.skipSpace(rest[0]);
			if(!Util.parseKeyWord(v1, "=", rest)) return false;
			v1=Util.skipSpace(rest[0]);
			if(!Util.parseInt(v1, intv2, rest)) return false;
			this.outputDevice((byte)dc,(byte)port,(byte)(intv2[0]));
			return true;
		}
		else
		if(Util.parseKeyWord(cmd,"message ",rest)){
			String s=Util.skipSpace(rest[0]);
//			this.outputMessageToDevice(s);
			if(this.fontTransmitter!=null){
				fontTransmitter.putString(s);
			}
			return true;
		}
		return false;
	}
	@Override
	public String getOutput() {
		// TODO Auto-generated method stub
		return null;
	}

	public void sendCommandToTwitter(String c, String v){
		if (mTwitterController != null) {
			if( mTwitterController.parseCommand(c, v)){
				//Log.d(TAG,"sendCommandToTwitter-true-"+c+","+v);
				sdLog.put(TAG+" - "+"sendCommandToTwitter-true-"+c+","+v);
			}
			else{
				//Log.d(TAG,"sendCommandToTwitter-true-"+c+","+v);
				sdLog.put(TAG+" - "+ "sendCommandToTwitter-false-"+c+","+v);
			}
		}		    	
		
	}
	
	public FontAccessor getFontAccessor(){
		return this.fontAccessor;
	}
	
	private boolean locationSendingEnabled;
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if(locationSendingEnabled){
		    String latitude= Double.toString(location.getLatitude());
		    String longitude= Double.toString(location.getLongitude());
		    this.sendCommandToTwitter("tweet", 
				"http://maps.google.co.jp/maps?"+
		        "f=q&hl=ja&geocode=&q="+latitude+","+longitude
		        );
		}
	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	private long getMilisec(String x){
		long rtn;
		StringTokenizer st=new StringTokenizer(x,"-");
		String numx=st.nextToken();
		String unit=st.nextToken();
		int dotPlace=numx.indexOf(".");
		long p1,p2;
		if(dotPlace<0){
			p1=(new Long(numx)).longValue();
			p2=0;
		}
		else{
			String p1s=numx.substring(0,dotPlace);
			String p2s=numx.substring(dotPlace+1);
			p1=(new Long(p1s)).longValue();
			p2=(new Long((p2s+"000").substring(0,3))).longValue();
		}
		if(unit.equals("sec")){
			rtn=p1*1000+p2;
		}
		else
		if(unit.equals("min")){
			rtn=(p1*1000+p2)*60;
		}
		else
		if(unit.equals("h")){
			rtn=(p1*1000+p2)*60*60;
		}
		else{
			rtn=0;
		}
//		this.messageArea.append("getCommandRequestInterval="+rtn+"\n");
		return rtn;
	}

  }
