package org.yamalab.uchiwade3d_ex1;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.yamalab.uchiwade3d_ex1.service.AdkService;
import org.yamalab.uchiwade3d_ex1.service.AdkThread;
import org.yamalab.uchiwade3d_ex1.service.StringMsg;
import org.yamalab.uchiwade3d_ex1.service.Util;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

//import org.yamalab.android.AdkWikiConnector.AndroidWikiConnector;
//import org.yamalab.android.twitter2neomatrixex1.TweetGUI;
//import org.yamalab.android.twitter2neomatrixex1.TwitterControllerGUI;
//import org.yamalab.android.twitter2neomatrixex1.TwitterLoginControllerGUI;
//import org.yamalab.android.twitter2neomatrixex1.R;
//import org.yamalab.android.twitter2neomatrixex1.InputController;
//import org.yamalab.android.twitter2neomatrixex1.twitterconnector.StringMsg;

public class AdkTwitterActivity extends Activity implements OnClickListener {
	static final String TAG = "AdkTwitterActivity";
		
	/** Called when the activity is first created. */
	TextView mInputLabel;
	TextView mOutputLabel;
	TextView mInOutLabel;
	TextView mStopLabel;
	TextView mTwitterLabel;// = this.findViewById(R.id.main_twitter_label);
	TextView mTweetLabel;// = findViewById(R.id.main_tweet_label);
	TextView mTwitterLoginLabel;// = this.<TextView>findViewById(R.id.main_login_label);
	TextView mLogLabel;// = (TextView) findViewById(R.id.main_log_label);

	TextView m3DInLabel;
	TextView mFioLabel;
	TextView mWikiLabel;

	EditText mLogArea;
	LinearLayout mInputContainer;
	LinearLayout mOutputContainer;
	LinearLayout mLoginContainer;
	LinearLayout mBasicContainer;
	LinearLayout mTwitterMainContainer;
	LinearLayout mTweetContainer;// = findViewById(R.id.tweet);
	LinearLayout mTwitterLoginContainer;// = (LinearLayout) findViewById(R.id.tweetlogin);
	LinearLayout mLogContainer;// = this.<LinearLayout>findViewById(R.id.log);
	LinearLayout mWikiContainer;
	LinearLayout mDebugContainer;

//	android.support.constraint.ConstraintLayout m3DInputContainer;
	View m3DInputContainer;
	View mFileIOContainer;


	Drawable mFocusedTabImage;
	Drawable mNormalTabImage;
	InputController mInputController;
	OutputController mOutputController;
	TwitterControllerGUI mTwitterController;
	TwitterLoginControllerGUI mTwitterLoginController;
	TweetGUI mTweet;
	ThreeDimensionInputController mThreeDimensionInputController;
	FileIOController mFileIOController;
	public Properties setting;
	TweetGUI connector;
//	public Hashtable properties;
	AdkService adkService;
	boolean mBound=false;
	boolean emulatorDebug=false; // true for debugging

    public AdkTwitterActivity() {
//        mLogArea = (EditText) findViewById(R.id.logArea);
    }

    /* */
	// comment the following onCreate and onResume for eclipse debugging
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"onCreate");

		if(emulatorDebug){
    		mFocusedTabImage = getResources().getDrawable(
    				R.drawable.tab_focused_holo_dark);
    		mNormalTabImage = getResources().getDrawable(
    				R.drawable.tab_normal_holo_dark);
    		Log.d(TAG,"onCreate-after prepareUsbConnection");
    		setContentView(R.layout.main);
    		this.enableControls(true);
    		return;
        }
		
		mFocusedTabImage = getResources().getDrawable(
				R.drawable.tab_focused_holo_dark);
		mNormalTabImage = getResources().getDrawable(
				R.drawable.tab_normal_holo_dark);
		Log.d(TAG, "onCreate-after prepareUsbConnection");
		
		if (mAccessory != null) {        // debug 2012/7/26  do not comment out, eclipse
		
			this.enableControls(true);
		
		} else {                           // debug 2012/7/26 do not comment out, eclipse
			this.enableControls(false);    // debug 2012/7/26 do not comment out, eclipse
		}                                  // debug 2012/7/26 do not comment out, eclipse
	}
	
	public void onResume(){
		super.onResume();
		Log.d(TAG,"onResume");
		this.loadProperties();

		if(emulatorDebug){
		    startService();         
			this.doBindService();  		
			return;
		}
		
		// comment out when running at eclipse. from here.
		/* */
		prepareUsbConnection();
		if (mInputStream != null && mOutputStream != null) {
			Log.d(TAG,"onResume - mInputStream!=null");
			this.doBindService();
			return;
		}

		Log.d(TAG,"onResume - mInputStream==null");
		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);

		if (accessory != null) {
			if (mUsbManager.hasPermission(accessory)) {
				openAccessory(accessory);
		        startService();         // do not comment out when running at eclipse.
				this.doBindService();   // do not comment out when running at eclipse.
			} else {
				synchronized (mUsbReceiver) {
					if (!mPermissionRequestPending) {
						mUsbManager.requestPermission(accessory,
								mPermissionIntent);
						mPermissionRequestPending = true;
					}
				}
			}
		} else {
			Log.d(TAG, "mAccessory is null");
		}
           //comment out when running at eclipse until here.
	}

	protected AdkThread mHostActivity;

	public void showControls() {
		Log.d(TAG,"showControls");
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main);
//		setContentView(R.layout.three_dimension_input);
		mInputController = new InputController(this);
		mOutputController = new OutputController(this, false);
		mTwitterController = new TwitterControllerGUI(this);
		mTwitterLoginController = new TwitterLoginControllerGUI(this,mTwitterController);
		mTweet = new TweetGUI(this,mTwitterController);
		mThreeDimensionInputController = new ThreeDimensionInputController(this);
		mThreeDimensionInputController.init();
		mFileIOController = new FileIOController(this);

		mFileIOController.set3DEditor(mThreeDimensionInputController);
//		mWikiLabel =   (TextView) findViewById(R.id.main_wiki_label);

		/* */
		Log.d(TAG,"showControls-1");
		mInputContainer = (LinearLayout) findViewById(R.id.inputContainer);
		mOutputContainer = (LinearLayout) findViewById(R.id.outputContainer);
		mTwitterMainContainer=(LinearLayout) findViewById(R.id.twitter_main);
		mTweetContainer = findViewById(R.id.tweet);
		mTwitterLoginContainer = (LinearLayout) findViewById(R.id.tweetlogin);
		mLogContainer = findViewById(R.id.log);
		m3DInputContainer=findViewById(R.id.three_dimension_input);
		mFileIOContainer=findViewById(R.id.file_io_container);
		mWikiContainer = (LinearLayout) findViewById(R.id.connector_container);
//		mDebugContainer = (LinearLayout) findViewById(R.id.pukiwiki_connector_debugger);

/*
		// レイアウトインフレーターを取得
		final android.view.LayoutInflater inflater1 = android.view.LayoutInflater.from(this);

// レイアウトXMLからビューを取得
		runOnUiThread(new Runnable() {
						 @Override
						 public void run() {
						 	if(m3DInputContainer!=null)
							 m3DInputContainer = inflater1.inflate(R.layout.three_dimension_input, null);
							 // 次回のメッセージを作る
						 }

						 ;
					 });
*/
/*
		// レイアウトインフレーターを取得
		final android.view.LayoutInflater inflater1 = android.view.LayoutInflater.from(this);

// レイアウトXMLからビューを取得
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(mFileIOContainer==null)
					mFileIOContainer = inflater1.inflate(R.layout.file_io_container, null);
				// 次回のメッセージを作る
			}

			;
		});
*/
		Log.d(TAG,"showControls-2");
		mInputLabel = findViewById(R.id.main_input_label);
		mOutputLabel = findViewById(R.id.main_output_label);
		mInOutLabel = findViewById(R.id.main_inout_label);
		mStopLabel = findViewById(R.id.main_stop_label);
		m3DInLabel = findViewById(R.id.main_3D_In_label);
		mFioLabel = findViewById(R.id.main_fio_label);
		/* */
		mTwitterLabel = findViewById(R.id.main_twitter_label);
		mTweetLabel = findViewById(R.id.main_tweet_label);
		mTwitterLoginLabel = findViewById(R.id.main_login_label);
		mLogLabel = findViewById(R.id.main_log_label);
		mLogArea =  (EditText)findViewById(R.id.logArea);
		mWikiLabel=findViewById(R.id.wiki_connector_label);
/* */
		/* */
		Log.d(TAG,"showControls-3");
		this.connector=this.mTweet;
		this.loadProperties();
		Log.d(TAG,"showControls-4");
		
		/* */
//		mInputLabel.setOnClickListener(this);
		mOutputLabel.setOnClickListener(this);
		mInOutLabel.setOnClickListener(this);
		mStopLabel.setOnClickListener(this);
		mTwitterLabel.setOnClickListener(this);
		mTweetLabel.setOnClickListener(this);
		mTwitterLoginLabel.setOnClickListener(this);
		mLogLabel.setOnClickListener(this);
		m3DInLabel.setOnClickListener(this);
		mFioLabel.setOnClickListener(this);
		showTabContents(R.id.main_input_label);
//		mTweet.setSetting(this.setting);

	}

	protected void hideControls() {
		Log.d(TAG,"hideControls");
		setContentView(R.layout.no_device);
		mInputController = null;
		mOutputController = null;
		mTwitterController = null;
		mTwitterLoginController = null;
		mTweet = null;
		m3DInputContainer=null;
		mFileIOContainer=null;
		mWikiContainer=null;
	}

	public void showTabContents(int id) {
		Log.d(TAG,"showTabContents id="+id);

		mInputContainer.setVisibility(View.GONE);
//		mInputLabel.setBackgroundDrawable(mNormalTabImage);
		mOutputContainer.setVisibility(View.GONE);
//		mOutputLabel.setBackgroundDrawable(mNormalTabImage);
		mInOutLabel.setBackgroundDrawable(mNormalTabImage);
		mFileIOContainer.setVisibility(View.GONE);
		mFioLabel.setBackgroundDrawable(mNormalTabImage);
		mTwitterMainContainer.setVisibility(View.GONE);
		mTwitterLabel.setBackgroundDrawable(mNormalTabImage);
		mTweetContainer.setVisibility(View.GONE);
		mTweetLabel.setBackgroundDrawable(mNormalTabImage);
		mTwitterLoginContainer.setVisibility(View.GONE);
		mTwitterLoginLabel.setBackgroundDrawable(mNormalTabImage);
		mLogContainer.setVisibility(View.GONE);
		mLogLabel.setBackgroundDrawable(mNormalTabImage);
		m3DInputContainer.setVisibility(View.GONE);
		m3DInLabel.setBackgroundDrawable(mNormalTabImage);
		mWikiContainer.setVisibility(View.GONE);
		mWikiLabel.setBackgroundDrawable(mNormalTabImage);

		if (id==R.id.main_inout_label) {
			Log.d(TAG,"showTabContents -main_inout_label id="+id);
			mInputContainer.setVisibility(View.VISIBLE);
			mInOutLabel.setBackgroundDrawable(mFocusedTabImage);
		}
		else
		if(id==R.id.main_output_label){
			Log.d(TAG,"showTabContents -main_output_label id="+id);
			mOutputContainer.setVisibility(View.VISIBLE);
			mOutputLabel.setBackgroundDrawable(mFocusedTabImage);
		}		/* */
		if (id==R.id.main_input_label) {
			Log.d(TAG,"showTabContents -main_input_label id="+id);
			mInputContainer.setVisibility(View.VISIBLE);
			mInputLabel.setBackgroundDrawable(mFocusedTabImage);
		}
		else
		if(id==R.id.main_3D_In_label){
			Log.d(TAG,"showTabContents -main_3D_In_label id="+id);
			m3DInputContainer.setVisibility(View.VISIBLE);
//			setContentView(R.layout.three_dimension_input);
//			mThreeDimensionInputController.init();
			m3DInLabel.setBackgroundDrawable(mFocusedTabImage);
		}
		else
		if(id==R.id.main_fio_label){
			Log.d(TAG,"showTabContents -main_fil_label id="+id);
			mFileIOContainer.setVisibility(View.VISIBLE);
			mFioLabel.setBackgroundDrawable(mFocusedTabImage);
		}
		else
		if(id==R.id.main_twitter_label)
		{
			Log.d(TAG,"showTabContents -twitter_main_label id="+id);
			mTwitterMainContainer.setVisibility(View.VISIBLE);
			mTwitterLabel.setBackgroundDrawable(mFocusedTabImage);
		}
		else
		if(id==R.id.main_tweet_label)
		{
			Log.d(TAG,"showTabContents -twitter_main_label id="+id);
			mTweetContainer.setVisibility(View.VISIBLE);
			mTweetLabel.setBackgroundDrawable(mFocusedTabImage);
		}
		else
		if(id==R.id.main_login_label)
		{
			Log.d(TAG,"showTabContents -twitter_main_label id="+id);
			mTwitterLoginContainer.setVisibility(View.VISIBLE);
			mTwitterLoginLabel.setBackgroundDrawable(mFocusedTabImage);
		}
		else
		if(id==R.id.main_log_label)
		{
			Log.d(TAG,"showTabContents -twitter_main_label id="+id);
			mLogContainer.setVisibility(View.VISIBLE);
			mLogLabel.setBackgroundDrawable(mFocusedTabImage);
		}
		else
		if(id==R.id.wiki_connector_label)
		{
			Log.d(TAG,"showTabContents -twitter_main_label id="+id);
			mWikiContainer.setVisibility(View.VISIBLE);
			mWikiLabel.setBackgroundDrawable(mFocusedTabImage);
		}
	}

	public void onClick(View v) {
		int vId = v.getId();
		Log.d(TAG,"onClick("+vId+")");
		if(vId==R.id.main_stop_label){
			stopService();
			finish();
		}
		else
		showTabContents(vId);

	}
	/*
	public void loadProperties(){
		Log.d(TAG,"loadProperties");
		this.setting=new Properties();
		SharedPreferences pref =
				getSharedPreferences("pref",MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
		if(pref!=null){
			Log.d(TAG,"loadProperties- pref!=null,setting");
			Map m=pref.getAll();
			Set keys = m.keySet();  
			Iterator itr=keys.iterator();
			while(itr.hasNext()){
				String key=(String)(itr.next());
				String info=(String)(m.get(key));
				setting.put(key, info);
			}
		}
		else{
			Log.d(TAG,"loadProperties- pref==null");			
		}
	}
	public void saveProperties(){
		Log.d(TAG,"saveProperties");
        //add "returnKey" as a key and assign it the value
        //in the textbox...
		if(setting!=null){
			SharedPreferences pref =
				getSharedPreferences("pref", MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
			Editor e = pref.edit();
			Enumeration keys=setting.keys();
			while(keys.hasMoreElements()){
				String key=(String)(keys.nextElement());
				String info=(String)(this.setting.getProperty(key));
				e.putString(key, info);
			}
			e.commit();
		}
		else{
			
		}
	}
	*/
	public void onPause(){
		Log.d(TAG,"onPause");
//		this.saveProperties();
		this.doUnbindService();
		super.onPause();
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);  
		Log.d(TAG,"onSaveInstanceState");
//	    saveProperties();
	}
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
	    super.onRestoreInstanceState(savedInstanceState);  
		Log.d(TAG,"onRestoreInstanceState");
	    this.loadProperties();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG,"onCreateOptionsMenu");
		menu.add("Simulate");
		menu.add("Quit");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG,"onOptionItemSelected");
		if (item.getTitle() == "Simulate") {
			showControls();
		} else if (item.getTitle() == "Quit") {
			finish();
			System.exit(0);
		}
		return true;
	}

	protected void enableControls(boolean enable) {
		Log.d(TAG,"enableControls("+enable+")");
		/* x */
		if (enable) {
			showControls();
		} else {
			hideControls();
		}
       /*		*/
		// debug
//		showControls();
	}
	
	protected void handleDigitalMessage(AdkThread.DigitalMsg m){
		if(mInputController==null) return;
		if(m==null) return;
		mInputController.setDigitalValues(m.getState());
	}
	protected void handleAnalogMessage(AdkThread.AnalogMsg m){
		if(mInputController==null) return;
		if(m==null) return;
		mInputController.setAnalogValue(m.getPort(), m.getVal());
	}

	protected void handleMessageString(StringMsg m) {
		if(m==null) return;
		String cmd=m.getCommand();
//		Log.d(TAG,"handleMessageString("+cmd+")");
		boolean rtn=parseStringMessage(m);
	}
	protected boolean parseStringMessage(StringMsg m){
		String x=m.getCommand();
		String cmd=Util.skipSpace(x);
		String[] rest=new String[1];
		String[] match=new String[1];
		int [] intv = new int[1];
		if(m==null) return true;
		Log.d(TAG,"parseStringMessage cmd="+cmd);
//		this.appendMessage(TAG+"-parseStringMessage cmd="+cmd+" val="+m.getValue());
		if(!(x.startsWith("log "))){
		   this.appendMessage(TAG+"-parseStringMessage cmd="+cmd+" val="+m.getValue());
		}
		if(mInputController==null) return true;
		if(Util.parseKeyWord(x, "twitter ", rest)){
			return mTwitterController.parseCommand(rest[0],m);
		}
		if(Util.parseKeyWord(x,"activity ",rest)){
			String mval="";
			if(m!=null){
				mval=m.getValue();
			}
			mInputController.appendMessage(x+" "+mval);
			return parseActivityMessage(rest[0],m);
		}
		if(Util.parseKeyWord(x,"log ",rest)){
			String mval="";
			if(m!=null){
				mval=m.getValue();
			}
	        appendMessage(mval);
			return true;
		}
		else
		if(Util.parseKeyWord(x,"3de",rest)){
			String subsubsub=Util.skipSpace(rest[0]);
			if(m!=null) {
				this.mThreeDimensionInputController.parseCommand(m.getValue());
			}
			return true;
		}
		return false;
	}

	protected boolean parseActivityMessage(String x, StringMsg m){
		String subcmd=Util.skipSpace(x);
		String[] rest=new String[1];
		String[] match=new String[1];
		Log.d(TAG,"parseActivityMessage("+x+","+m.getValue()+")");
		int [] intv = new int[1];
		if(mInputController==null) return true;
		if(Util.parseKeyWord(subcmd,"set ",rest)){
		   	String subsub=Util.skipSpace(rest[0]);
		   	if(Util.parseKeyWord(subsub,"output",rest)){
//		        mInputController.setAdk2WikiText(m.getValue());
			    return true;
		   	}
		   	else
		   	if(Util.parseKeyWord(subsub,"input",rest)){
//		   		mInputController.setWikiCommand(m.getValue());
		   		return true;
		   	}
		   	else
		   	if(Util.parseKeyWord(subsub,"message",rest)){
		   		mInputController.setMessage(m.getValue());
//		   		connector.setMessage(m.getValue());
		   		return true;
		   	}
		   	else
			if(Util.parseKeyWord(subsub,"show",rest)){
			   	String subsubsub=Util.skipSpace(rest[0]);
			 	if(subsubsub.startsWith("connector")){
			 		this.showTabContents(R.id.main_twitter_label);
			 	}
			   	return true;
			}
		   	else
			if(Util.parseKeyWord(subsub,"device",rest)){
			   	String subsubsub=Util.skipSpace(rest[0]);
			 	this.mOutputController.setValue(subsubsub);
			   	return true;
			}

		   		
		}
		else
		if(Util.parseKeyWord(subcmd,"append ",rest)){
		   	String subsub=Util.skipSpace(rest[0]);
		   	if(Util.parseKeyWord(subsub,"output",rest)){
//		        mInputController.appendAdk2WikiText(m.getValue());
			    return true;
		   	}
		   	else
		   	if(Util.parseKeyWord(subsub,"input",rest)){
//			    mInputController.appendWikiCommand(m.getValue());
		   	}
		   	else
		   	if(Util.parseKeyWord(subsub,"message",rest)){
			    mInputController.appendMessage(m.getValue());
		   	}
		}
		else
		if(Util.parseKeyWord(subcmd,"request ",rest)){
		   	String w=Util.skipSpace(rest[0]);
		   	if(Util.parseKeyWord(w,"setting",rest)){
//		 	   this.sendSetting();
			}
		}	    
		return false;
	}

	protected void handleMessageConnect(String x) {
		Log.d(TAG,"handleMessageConnect("+x+")");
		if(x==null) return;
		StringTokenizer st=new StringTokenizer(x," ");
		String c1=st.nextToken();
		if(c1==null) return;
		if(c1.equals("display")){
			String c2=st.nextToken();
			if(c2==null) return;
			if(c2.equals("disconnect")){
				this.hideControls();
			}
		}
	}
	
	
	protected InputController getInputController(){
		return this.mInputController;
	}
	
    /** Messenger for communicating with service. */
	Messenger mService = null;
	/** Flag indicating whether we have called bind on the service. */
	boolean mIsBound;
	/** Some text view we are using to show state information. */
	TextView mCallbackText;
	/**         
	 * Handler of incoming messages from service.
	 */

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AdkThread.MESSAGE_DIGITAL:
				AdkThread.DigitalMsg o = (AdkThread.DigitalMsg) msg.obj;
				handleDigitalMessage(o);
				break;

			case AdkThread.MESSAGE_ANALOG:
				AdkThread.AnalogMsg t = (AdkThread.AnalogMsg) msg.obj;
				handleAnalogMessage(t);
				break;

			case AdkThread.MESSAGE_CONNECT:
				String x = (String) msg.obj;
				handleMessageConnect(x);
				break;
				
			case AdkThread.MESSAGE_STRING:
				Object m = msg.obj;
				handleMessageString((StringMsg)m);
				break;
				
			}
		}
	};
	/*
	 * Target we publish for clients to send messages to IncomingHandler.
	 *         */
	final Messenger mMessenger = new Messenger(new IncomingHandler());
	/**         
	 * * Class for interacting with the main interface of the service.
	 *          */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className,
				IBinder service) {
			Log.d(TAG,"-ServiceConnection-onServiceConnected");
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service.  We are communicating with our
			// service through an IDL interface, so get a client-side
			// representation of that from the raw service object.
			mService = new Messenger(service);
//			mCallbackText.setText("Attached.");
			// We want to monitor the service for as long as we are
			// connected to it.
			try {
				Message msg = Message.obtain(null,
						AdkService.MSG_REGISTER_CLIENT);
				msg.replyTo = mMessenger;
				mService.send(msg);
				// Give it some value as an example.
//				msg = Message.obtain(null,
//						AdkService.MSG_SET_VALUE, this.hashCode(), 0);
//				mService.send(msg);
			} catch (RemoteException e) {
				// In this case the service has crashed before we could even
				// do anything with it; we can count on soon being
				// disconnected (and then reconnected if it can be restarted)
				// so there is no need to do anything here.
			}
			sendUsbFileDescriptor();
			// As part of the sample, tell the user what happened.
			Toast.makeText(AdkTwitterActivity.this, "AdkService Connected",
					Toast.LENGTH_SHORT).show();

		}

		public void onServiceDisconnected(ComponentName className) {
			Log.d(TAG,"onServiceDisconnected("+className+")");
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			mService = null;
//			mCallbackText.setText("Disconnected.");
			// As part of the sample, tell the user what happened.
			Toast.makeText(AdkTwitterActivity.this, "AdkService disconnected",
					Toast.LENGTH_SHORT).show();
		}
	};
	void doBindService() {
		Log.d(TAG,"dobindService");
		// Establish a connection with the service.  We use an explicit
		// class name because there is no reason to be able to let other
		// applications replace our component.
		bindService(new Intent(AdkTwitterActivity.this,
				AdkService.class), mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;

	}
	void doUnbindService() {
		Log.d(TAG,"doUnbindService..mIsBound="+mIsBound);
		if (mIsBound) {
			// If we have received the service, and hence registered with
			// it, then now is the time to unregister.
			if (mService != null) {
				try {
					Message msg = Message.obtain(null,
							AdkService.MSG_UNREGISTER_CLIENT);
					msg.replyTo = mMessenger;
					mService.send(msg);
				} catch (RemoteException e) {
					// There is nothing special we need to do if the service
					// has crashed.
				}
				mService=null;
			}
			Log.d(TAG,"doUnbindService..2");
			/* */  
			if(mUsbReceiver!=null)
	    	    unregisterReceiver(mUsbReceiver);
	    	/*  */
			if(mConnection!=null)
		    	unbindService(mConnection);
			Log.d(TAG,"doUnbindService..3");
			mIsBound = false;
		}
	}
	public void onDestroy(){
		Log.d(TAG,"onDestroy..1");
		this.doUnbindService();
		Log.d(TAG,"onDestroy..2");
		super.onDestroy();
	}
	private static final String mServiceName = AdkService.class.getCanonicalName();
	public boolean isServiceRunning() {
		Log.d(TAG,"isServiceRunning");
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningApp = activityManager.getRunningServices(Integer.MAX_VALUE);
		for (RunningServiceInfo info : runningApp) {
			if (mServiceName.equals(info.service.getClassName())) {
				Log.d(TAG,"isServiceRunning==true");
				return true;
			}
		}
		Log.d(TAG,"isServiceRunning==false");
		return false;
	}
	private void stopService(){
		Log.d(TAG,"stopService");
		try{
		   Message msg = Message.obtain(null,AdkService.MSG_STOP);
		   mService.send(msg);
		}
		catch(Exception e){
			Log.d(TAG, "stopService:Exception:"+e.toString());
		}
		this.doUnbindService();
		closeAccessory();
	}
	private void sendUsbFileDescriptor(){
		Log.d(TAG,"sendUsbFileDescriptor");
		try{
		   Message msg = Message.obtain(null,AdkService.MSG_SET_USBFILEDESCRIPTOR);
		   msg.obj=this.mFileDescriptor;
		   mService.send(msg);
		}
		catch(Exception e){
			
		}
	}
	
	public void sendCommandToService(String cmd, String val){
		Log.d(TAG,"sendCommandToService("+cmd+","+val+")");
		try{
		   Message msg = Message.obtain(null,AdkService.MSG_EXEC_COMMAND);
		   msg.obj=new StringMsg(cmd,val);
		   mService.send(msg);
		}
		catch(Exception e){
			
		}
	}	/* */
	private UsbManager mUsbManager;
	private PendingIntent mPermissionIntent;
	private boolean mPermissionRequestPending;

//    public static final String ACTION_USB_PERMISSION = "com.google.android.DemoKit.action.USB_PERMISSION";
	public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

	UsbAccessory mAccessory;
	ParcelFileDescriptor mFileDescriptor;
	public FileInputStream mInputStream;
	public FileOutputStream mOutputStream;
	
    private final BroadcastReceiver mUsbReceiver;

	{
		mUsbReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.i(TAG, "BroadcastReceiver-onRecieve");
				String action = intent.getAction();
				if (ACTION_USB_PERMISSION.equals(action)) {
					Log.i(TAG, "BroadcastReceiver-onRecieve- ACTION_USB_PERMISSION");
					synchronized (this) {
						UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
//						UsbAccessory[] accessories = mUsbManager.getAccessoryList();
//						UsbAccessory accessory = (accessories == null ? null : accessories[0]);
						if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
							openAccessory(accessory);
							startService();
							doBindService();
						} else {
							Log.d(TAG, "permission denied for accessory "
									+ accessory);
						}
						mPermissionRequestPending = false;
					}
				} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
					Log.i(TAG, "BroadcastReceiver-onRecieve- ACTION_USB_ACCESSORY_DETACHED");
//					UsbAccessory[] accessories = mUsbManager.getAccessoryList();
//					UsbAccessory accessory = (accessories == null ? null : accessories[0]);
//					if (accessory != null && accessory.equals(mAccessory)) {
					UsbAccessory accessory = (UsbAccessory)intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
					if(accessory !=null){
						closeAccessory();
					}
				}
			}
		};
	}

	private void openAccessory(UsbAccessory accessory){
 		Log.d(TAG, "openAccessory");
      	mFileDescriptor = mUsbManager.openAccessory(accessory);
//		mFileDescriptor = mUsbManager.openDevice(accessory);
      	if(mFileDescriptor !=null){
     		Log.d(TAG, "openAccessory- mFileDescriptor!=null");
      		mAccessory = accessory;
      		/* */
      		FileDescriptor fd = mFileDescriptor.getFileDescriptor();
      		mInputStream = new FileInputStream(fd);
      		mOutputStream = new FileOutputStream(fd);
      		/* */
			enableControls(true);
	 		Log.d(TAG, "openAccessory -2 ");
			if(mInputController!=null)
				   mInputController.accessoryAttached();
			if(mOutputController!=null)
				  mOutputController.accessoryAttached();
			this.enableControls(true);
		}
      	else{
      		Log.d(TAG, "accessory open fail");
      	}
      }
    public void startService(){
		if(!this.isServiceRunning()){
			Log.d(TAG,"startService .. startService");
			// Service??????Intent?????????N??????????????????
		    Intent intent = new Intent(AdkTwitterActivity.this, AdkService.class);
		    startService(intent);
		}
		else{
			Log.d(TAG,"openAccessory .. service is already running?");
		}    	
    }
  	private void closeAccessory() {
		Log.d(TAG, "closeAccessory");
 		try {
 			if (mFileDescriptor != null) {
 				mFileDescriptor.close();
 			}
 		} catch (IOException e) {
 			Log.d(TAG,"closeAccessory:Exception:"+e.toString());
 		} finally {
 			mFileDescriptor = null;
 			mAccessory = null;
 		}
     }
/* */  	
	public void prepareUsbConnection(){
 		Log.d(TAG, "prepareUsbConnection");
//        mUsbManager = UsbManager.getInstance(this);
		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		mPermissionIntent =
     		PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        registerReceiver(mUsbReceiver, filter); 

		if (getLastNonConfigurationInstance() != null) {
	 		Log.d(TAG, "getLastnonConfigurationInstance()!=null");
			mAccessory = (UsbAccessory) getLastNonConfigurationInstance();
			openAccessory(mAccessory);
	        startService();
	        this.doBindService();
		}
	}
	
   /* */ 
	Vector<String> messageLines=new Vector();
	int maxMessageLines=60;
	public void appendMessage(String x){
		if(mLogArea==null) return;
	    Date date = new Date();
		messageLines.add(date.toString()+":"+x);
		while(messageLines.size()>maxMessageLines){
			messageLines.remove(0);
		}
//        ((Activity)mHostActivity).runOnUiThread(new Runnable() {
		this.runOnUiThread(new Runnable(){
        	@Override
        	public void run(){
		        mLogArea.setText("");
		        for(int i=0;i<messageLines.size();i++){
			       mLogArea.append(messageLines.elementAt(i)+"\n");
		        }
        	}
        });
	}	
	public void loadProperties(){
		Log.d(TAG,"loadProperties");
		Properties s=new Properties();
		SharedPreferences pref =
				getSharedPreferences("pref",MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
		if(pref!=null){
			Log.d(TAG,"loadProperties- pref!=null,setting");
			Map m=pref.getAll();
			Set keys = m.keySet();
			Iterator itr=keys.iterator();
			while(itr.hasNext()){
				String key=(String)(itr.next());
				String info=(String)(m.get(key));
				s.put(key, info);
				Log.d(TAG,"loading property ...key="+key+" - info="+info);
			}
		}
		else{
			Log.d(TAG,"loadProperties- pref==null");
		}
		if(connector!=null)
		   connector.setSetting(s);
	}
	public void saveProperties(Properties s){
		Log.d(TAG,"saveProperties");
        //add "returnKey" as a key and assign it the value
        //in the textbox...
		if(s!=null){
			SharedPreferences pref =
				getSharedPreferences("pref", MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
			Editor e = pref.edit();
			Enumeration keys=s.keys();
			while(keys.hasMoreElements()){
				String key=(String)(keys.nextElement());
				String info= s.getProperty(key);
				e.putString(key, info);
				Log.d(TAG,"saving property ...key="+key+" - info="+info);
			}
			e.apply();
		}
		else{
			
		}
	}
	public void saveProperties(){
		Log.d(TAG,"saveProperties");
		//add "returnKey" as a key and assign it the value
		//in the textbox...
		if(setting!=null){
			SharedPreferences pref =
					getSharedPreferences("pref", MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
			Editor e = pref.edit();
			Enumeration keys=setting.keys();
			while(keys.hasMoreElements()){
				String key=(String)(keys.nextElement());
				String info=(String)(this.setting.getProperty(key));
				e.putString(key, info);
			}
			e.commit();
		}
		else{

		}
	}
	
}