package com.captncript.dadsRadio;

import android.app.*;
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
   -Still required play songs back to back
   -Add basic radio playing functions
   -Find music in system
   -Decide if music should be found in its own thread
*/

public class MainActivity extends Activity 
{
	//This is the output stream for development uses
	public PrintStream pss = null;
	
	public static int ARTIST = 1;
	public static int SONG = 2;
	
	/*
	. Below this you will see 2 of the same variable
	  mine should be commented out and yours uncommented
	  when you are developing. This changes the destination of 
	  all System.out.println() calls
	*/
	private static final String OUT_FILE_PATH = "/storage/emulated/0/AppProjects/dadsRadio-main/dadsradio/dadsRadio/app/output";
	//private static final String OUT_FILE_PATH = "";
	
	
	EditText et = null;
	
	DadsPlayer mDadsPlayer;
	Button mButton = null;
	
	boolean mBound = false;
	
	
	private ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceConnected(ComponentName DadsPlayer, IBinder service)
		{
			System.out.println("connected");
			DadsPlayer.LocalBinder binder = (DadsPlayer.LocalBinder) service;
			mDadsPlayer = binder.getService();
			mBound = true;

			new Thread(new Runnable() {
				@Override
				public void run() {
					try{
						String foo = mDadsPlayer.testing();
						System.out.println("Errors with prep: " + foo);
					} catch(Exception e) {
						System.out.println(e);
					}
				}
			}).start();
				
			String mErr = mDadsPlayer.getASyncError();
			System.out.println("Async errors: " + mErr);
			System.out.println("bound: " + mBound);
		}

		@Override
		public void onServiceDisconnected(ComponentName p1)
		{
			System.out.println("service disconnected");
			mBound = false;
		}

	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		et =  (EditText)findViewById(R.id.display);
		mButton = (Button)findViewById(R.id.button1);
		
		/*
		    Runs a setup for outputting
		    data during development.
		    This works specifically for me
		    you will probably want to design
		    your own.
		*/
		outputSetup();
		
		System.out.println("Main: findSongs()");
    }

	@Override
	protected void onStart()
	{
		super.onStart();
		
		System.out.println("Main: onStart");
		
		findSongs("testing",0);
	}

	
	
	@Override
	protected void onRestart() {
		super.onRestart();

		outputSetup();
	}
	
	@Override
	protected void onStop()
	{
		System.out.println("Closing stream");
		pss.close();
		
		System.out.println("Unbinding service");
		if(mBound) {
			unbindService(mConnection);
		}
		
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
	
	public void update(View v) {
		System.out.println("Song complete: " + mDadsPlayer.getPIsComplete());
	}
}
