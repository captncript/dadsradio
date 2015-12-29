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
import android.provider.MediaStore;
import java.util.*;
import android.widget.AdapterView.*;

/*
	TODO:
	
   -Add basic radio playing functions(seek, next song, previous song, volume)
   -Find music in system
   -Decide if music should be found in its own thread
   -Make play button resume songs instead of pause button
   -Change buttons to symbols
   -Add voice control
*/

/*
    Going to attempt a folder indexer
    for faster searching of music
*/

public class MainActivity extends Activity
{
	//DAD NOTE: This is the output stream for development uses
	public PrintStream pss = null;
	
	public static final int ARTIST = 1;
	public static final int SONG = 2;
	public static final int PAUSE = 0;
	public static final int REQUEST_MEDIA = 3;
	
	/*
	  DAD NOTE:
	  Below this you will see 2 of the same variable
	  Nick's should be commented out and Chris' uncommented
	  when you are developing. This changes the destination of 
	  all System.out.println() calls
	*/
	private static final String OUT_FILE_PATH = "/storage/emulated/0/AppProjects/DadsRadio/dadsradio/app/output";
	
	//DAD NOTE: This variable will hold the text box
	EditText et = null;
	private Button pPauseButton = null;
	
	DadsPlayer mDadsPlayer;
	Button mButton = null;
	
	//DAD NOTE: This variable being true means that
	//we have a connection to the service(DadsPlayer.java)
	boolean mBound = false;
	boolean mIsPaused = false;
	
	private Handler mHandler = null;
	private ArrayList<String> pDirs = new ArrayList<String>();
	
	private ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceConnected(ComponentName DadsPlayer, IBinder service)
		{
			System.out.println("connected");
			DadsPlayer.LocalBinder binder = (DadsPlayer.LocalBinder) service;
			mDadsPlayer = binder.getService();
			mBound = true;
			mDadsPlayer.setHandler(mHandler);
				
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
		pPauseButton = (Button)findViewById(R.id.pause);
		
		//pulled binding to creation
		startBinding();
		
		/*
		    DAD NOTE:
		    Runs a setup for outputting
		    data during development.
		    This works specifically for me
		    you will probably want to design
		    your own.
		*/
		outputSetup();

    }

	@Override
	protected void onRestart() {
		super.onRestart();

		startBinding();
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
				//This will need to be changed to a specific
				//type of exception or removed
				System.out.println(e);
			}
		}
		
		System.out.println("Closing stream");
		pss.close();
		
		super.onStop();
	}
	
	private void outputSetup() {
		//DAD NOTE: This just sets up an area outside the app to view output
		try
		{
			File mFile = new File(OUT_FILE_PATH);
			pss = new PrintStream(mFile);

			//DAD NOTE:
			//This makes System.out.println(string)
			//Send output to our file
			System.setOut(pss);
			
			et.setText("Successful setup");
		}
		catch (FileNotFoundException e)
		{
			Log.v("Output setup", e.toString());
			et.setText("File not found");
		}

	}
	
	public void startBinding() {
		//This should be called when starting 
		//to bind the service for general use
		
		Intent mIntent = new Intent(this,com.captncript.dadsRadio.DadsPlayer.class);

		try {
			bindService(mIntent,mConnection,Context.BIND_AUTO_CREATE);
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public void startPlaying() {
		System.out.println("Start Playing");
		
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
	}
	
	public void findSongs(String mDescriptor, int mFlag) {
		//This is for only a single descriptor
		//mDescriptor is artist or song name
		//mFlag indicates, which was sent
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
		//mDescriptors comes in with both artist and song name
		startPlaying();
	}
	
	public void play(View v) {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what) {
					case PAUSE:
						if(!mIsPaused) {
							et.setText("Paused");
							pPauseButton.setText("Resume Playing");
							mIsPaused = true;
						} else {
							et.setText("Playing");
							pPauseButton.setText("Pause");
							mIsPaused = false;
						}
				}
			}
		};
		mDadsPlayer.setHandler(mHandler);
		
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
	
	//*************************************************
	//Below is a grouping of test functions
	public void songPicker(View v) {
		System.out.println("Main:songPicker");
		indexMusic();
	}
	
	public void songDisplay() {
		final Dialog dialog = new Dialog(this);
		ArrayList<String> mSongs = new ArrayList<String>();
		dialog.setContentView(R.layout.songpicker);
		dialog.setTitle("Song Selection");
		dialog.getWindow().getAttributes().width = WindowManager.LayoutParams.FILL_PARENT;
		dialog.getWindow().getAttributes().height = WindowManager.LayoutParams.FILL_PARENT;
		
		
		final ListView lv = (ListView)dialog.findViewById(R.id.Songs);
		
		final Button mBOK = (Button)dialog.findViewById(R.id.OK);
		final Button mCancel = (Button)dialog.findViewById(R.id.Cancel);
		
		
		if(pDirs != null) {
			for(String s : pDirs) {
				for(String t : new File(s).list(musicFilter)) {
					mSongs.add(t);
				}
			}
		}
		
		final ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mSongs);
		
			if(lv != null) {
				lv.setAdapter(mAdapter);
			}
		//Ok can't be picked without making a selection in
		//the ListView
		mBOK.setEnabled(false);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView < ? > adapter, View view, int position, long l) {
				mBOK.setEnabled(true);
			}
		});
		
		mBOK.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//TODO: Handle Playlist
				dialog.dismiss();
			}
		});
		
		mCancel.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					//TODO: Handle Playlist
					dialog.dismiss();
				}
			});
			
		dialog.show();
	}
	
	FilenameFilter musicFilter = new FilenameFilter() {
		@Override
		public boolean accept(File p1, String name) {
			//Add other sound files
			String lowerName = name.toLowerCase();

			if(lowerName.endsWith(".mp3")) {
				return true;
			} else {
				return false;
			}
		}
	};
	
	private void indexMusic() {
		System.out.println("Indexing");
		et.setText("Finding Songs");
		//TODO: Make this store data somewhere
		//      Come up with conditions to run this
		final Handler mIndexHandler = new Handler() {
			public void handleMessage(Message msg) {
				Bundle mBundle = null;
				ArrayList<String> mIndex = null;
				
				try {
				switch(msg.what) {
					case 0:
						mBundle = msg.getData();
						 mIndex = mBundle.getStringArrayList("Files");
						for(String s: mIndex) {
							//System.out.println(s);
							pDirs.add(s);
						}
						et.setText("index complete");
						
						songDisplay();		
					break;
					case 1:
						//For Updates
						System.out.println((String)msg.obj);
						//et.setText((String)msg.obj);
					break;
					case 2:
						mBundle = msg.getData();
						mIndex = mBundle.getStringArrayList("Files");
						mDadsPlayer.setSongs(mIndex.get(0),mIndex.get(1));
					break;
				}
				}catch(Exception e){
					System.out.println(e);
				}
			}
		};
		System.out.println("Starting new thread");
		
		new Thread(new Runnable() {
			ArrayList<String> mIndexes = new ArrayList<String>();
			ArrayList<String> mIndexDirs = new ArrayList<String>();
			ArrayList<String> mDirs = new ArrayList<String>();
			
			Bundle mBundle = new Bundle();
			
			int mCounter = 0;
			int mDirCount = 0;
			
			String lastDir = new String();
			
			boolean once = true;
			
			@Override
			public void run() {
				File file = new File("/storage");
				Message m = Message.obtain(mIndexHandler,1,"Building");
				m.sendToTarget();

				try{
					fileCrawler(file);
				} catch(Exception e) {
					System.out.println(e);
				}
				System.out.println("crawled");
				try {
					mBundle.putStringArrayList("Files",mIndexDirs);
					Message mMessage = Message.obtain(mIndexHandler, 0);
					mMessage.arg1 = mCounter;
					mMessage.setData(mBundle);
					mMessage.sendToTarget();
				}catch(Exception e) {
					System.out.println(e);
				}
				try
				{
					Thread.currentThread().join();
				}
				catch (InterruptedException e)
				{
					System.out.println(e);
				}
			}
			
			public void fileCrawler(File mFile) {
				/*
				    Recursive function drills down
					through all files looking for music 
					files.
				*/
				File mFiles[] = null;
				File indexFiles[] = null;
				
				if(mFile != null) {
					mFiles = mFile.listFiles();
					indexFiles = mFile.listFiles(musicFilter);
				}
				
				if (mFiles != null) {
					for(File f: mFiles) {
						if(f.isDirectory()) {
							mDirs.add(f.toString());
							mDirCount++;
						}
						mIndexes.add(f.toString());
						mCounter++;
						fileCrawler(f);
					}
				}
				if(indexFiles != null) {
					for(File f : indexFiles) {
						String mPathToFile = f.toString().substring(0,f.toString().lastIndexOf(File.separatorChar));
						if(!mPathToFile.equals(lastDir)) {
							mIndexDirs.add(mPathToFile);
							lastDir = mPathToFile;
						}
					}

				}
			}
		}).start();
	}
	
}
