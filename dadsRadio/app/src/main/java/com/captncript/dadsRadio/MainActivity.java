package com.captncript.dadsRadio;

import android.app.*;
import android.media.MediaPlayer;
import android.os.*;

import java.io.*;
import android.util.*;

import java.net.URL;
import android.widget.*;
import android.content.*;
import android.media.*;
import android.view.View.*;
import android.view.*;

/*
	For Nick
	
	
   -Still required play songs back to back
   -Add basic radio playing functions
   -Find music in system
   -Decide if music should be found in its own thread
*/

public class MainActivity extends Activity
{
	//DAD NOTE: This is the output stream for development uses
	public PrintStream pss = null;
	
	public static final int ARTIST = 1;
	public static final int SONG = 2;
	public static final int PAUSE = 0;
	
	/*
	  DAD NOTE:
	  Below this you will see 2 of the same variable
	  Nick's should be commented out and Chris' uncommented
	  when you are developing. This changes the destination of 
	  all System.out.println() calls
	*/
	//private static final String OUT_FILE_PATH = "/storage/emulated/0/AppProjects/DadsRadio2/dadsradio/dadsRadio/app/output";
	private static final String OUT_FILE_PATH = "/storage/emulated/0/AppProjects/dadsRadio-main/dadsradio/dadsRadio/app/output";
	
	
	
	//DAD NOTE: This variable will hold the text box
	EditText et = null;
	
	DadsPlayer mDadsPlayer;
	Button mButton = null;
	
	//DAD NOTE: This variable being true means that
	//we have a connection to the service(DadsPlayer.java)
	boolean mBound = false;
	
	private Handler mHandler = null;
	
	
	private ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceConnected(ComponentName DadsPlayer, IBinder service)
		{
			System.out.println("connected");
			DadsPlayer.LocalBinder binder = (DadsPlayer.LocalBinder) service;
			mDadsPlayer = binder.getService();
			mBound = true;
			mDadsPlayer.setHandler(mHandler);
			
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					try{
						String foo = mDadsPlayer.testing();
						if(foo != null) {
							System.out.println("Errors with prep: " + foo);
						}
					} catch(Exception e) {
						System.out.println(e);
					}
				}
			}).start();
				
			String mErr = mDadsPlayer.getASyncError();
			if(mErr != null) {
				System.out.println("Async errors: " + mErr);
			}
			System.out.println("bound: " + mBound);
		}

		@Override
		public void onServiceDisconnected(ComponentName p1)
		{
			System.out.println("service disconnected");
			mBound = false;
			et.setText("Service Disconnected");
		}

	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		
		et =  (EditText)findViewById(R.id.display);
		
		/*
		    Runs a setup for outputting
		    data during development.
		    This works specifically for me
		    you will probably want to design
		    your own.
		*/
		outputSetup();

    }

	@Override
	protected void onStart()
	{
		super.onStart();
		
		System.out.println("Main: onStart");
	}

	
	
	@Override
	protected void onRestart() {
		super.onRestart();

		outputSetup();
	}
	
	@Override
	protected void onStop()
	{
		System.out.println("Unbinding service");
		if(mBound) {
			try{
				unbindService(mConnection);
			} catch(Exception e) {
				System.out.println(e);
			}
		}
		
		System.out.println("Closing stream");
		pss.close();
		
		super.onStop();
	}
	
	private void outputSetup() {
		//This just sets up an area outside the app to view output
		try
		{
			File mFile = new File(OUT_FILE_PATH);
			pss = new PrintStream(mFile);

			//This makes System.out.println(string)
			//Send output to our file
			et.setText("Successful setup");
			System.setOut(pss);
		}
		catch (FileNotFoundException e)
		{
			Log.v("Output setup", e.toString());
			et.setText("File not found");
		}

	}
	
	public void startPlaying() {
		System.out.println("Start Playing");
		
		Intent mIntent = new Intent(this,com.captncript.dadsRadio.DadsPlayer.class);
		
		try {
			bindService(mIntent,mConnection,Context.BIND_AUTO_CREATE);
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public void findSongs(String mDescriptor, int mFlag) {
		//This is for only a single descriptor
		System.out.println("FindSongs: start");
		
		if(mFlag == ARTIST) {
			//TODO: make this find songs by artist
			//name
		} else if(mFlag == SONG) {
			//TODO: make this find songs by
			//song name
		}
		
		startPlaying();
	}
	
	public void findSongs(String mDescriptors[]) {
		
		startPlaying();
	}
	
	public void play(View v) {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what) {
					case PAUSE:
						et.setText("Paused");
				}
			}
		};
		
		findSongs("test",0);
	}
	
	public void cleanUp(View v) {
		System.out.println("Main:cleanUp");
		et.setText("Cleaning up");
		//This releases all remaining
		//media players
		try {
		mDadsPlayer.cleanUp();
		
		if(mBound) {
			unbindService(mConnection);
		}
		System.out.println("Main:Clean");
		}catch(Exception e) {
			System.out.println("cleanup: " + e.toString());
		}
		et.setText("Clean");
	}
	
	public void pause(View v) {
		if(mBound) {
			mDadsPlayer.pause();
		}
	}
}
