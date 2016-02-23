package com.captncript.dadsRadio;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

/*
	TODO:
    02-19-16
    19:45

   -Change buttons to symbols
   -Clean out displaying .mp3 in name
   -Add playlist support
   -Add caching
   -Add voice control
*/

public class MainActivity extends Activity
{
	private SteadyVariables pSV;
	
	//DAD NOTE: This is the output stream for development uses
	public PrintStream ps;
	
	public static final int ARTIST = 1;
	public static final int SONG = 2;
	public static final int REQUEST_MEDIA = 3;
    
    public static final int PAUSE = 0;
    public static final int SONG_NAME = 1;
	
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
	boolean mFragExists = false;
	boolean mReOpened = false;
    
    String songName = new String();
    
	private Handler mHandler = null;
	private ArrayList<String> pDirs = new ArrayList<String>();
	
    FilenameFilter musicFilter = new FilenameFilter() {
        @Override
        public boolean accept(File p1, String name) {
            //Add other sound files
            String lowerName = name.toLowerCase();

            if(lowerName.endsWith(".mp3") && !lowerName.startsWith("com.") && !lowerName.contains("Legacy")) {
                return true;
            } else {
                return false;
            }
        }
	};
    
	private ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceConnected(ComponentName DadsPlayer, IBinder service)
		{
			System.out.println("connected");
			DadsPlayer.LocalBinder binder = (DadsPlayer.LocalBinder) service;
			mDadsPlayer = binder.getService();
			mBound = true;
			mDadsPlayer.setHandler(mHandler);
			
			pSV.setPDadsPlayer(mDadsPlayer);
			
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

    public void cleanUp(View v) {
        //This doesn't display and
        //locks down the program
        //Might remove
        System.out.println("Main:cleanUp");
        try {
            et.setText("Cleaning up");
            System.out.println(et.getText());
        } catch(Exception e) {
            System.out.println(e);
        }
        //This releases all remaining
        //media players
        mDadsPlayer.cleanUp();

        System.out.println("Main:Clean");

        et.setText("Clean");
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
    
    public String getOutput() {
        /* May be causing a delay.
        grabs up to 500 characters 
        from the output file and saves
        to be written when file is
        recreated. Called from 
        onSaveInstanceState */
        
        String output = new String();
        char[] buffer = new char[500];
        File file = new File(OUT_FILE_PATH);
        
        try {
            FileReader fr = new FileReader(file);
            fr.read(buffer);
        } catch (FileNotFoundException e) {
            Log.e("File Read", "Can't find the file");
        } catch (IOException e) {
            Log.e("File Read", "io exception");
        }

        if(buffer != null) {
            for(char c : buffer) {
                output += c;
            }
        }
        
        return output;
    }
    
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
                            pDirs.clear();
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

                    try {
                        //This try needs to be removed
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
                    try {
                        Thread.currentThread().join();
                    }
                    catch (InterruptedException e) {
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
                            if(!mPathToFile.equals(lastDir) && !mPathToFile.toLowerCase().contains("legacy")) {
                                mIndexDirs.add(mPathToFile);
                                lastDir = mPathToFile;
                            }
                        }

                    }
                }
            }).start();
	}

    public void nextSong(View v) {
        if(mBound) {
            mDadsPlayer.nextSong();
        }
    }
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		
		FragmentManager fm = getFragmentManager();
		pSV = (SteadyVariables)fm.findFragmentByTag("ps");
		
		et =  (EditText)findViewById(R.id.display);
		pPauseButton = (Button)findViewById(R.id.pause);
		/*
		 DAD NOTE:
		 Runs a setup for outputting
		 data during development.
		 This works specifically for me
		 you will probably want to design
		 your own.
		 */
		outputSetup();
		
		if(pSV == null) {
			pSV = new SteadyVariables();
			
			fm.beginTransaction().add(pSV, "ps").commit();
			pSV.setPConnection(mConnection);
		} else {
			mDadsPlayer = pSV.getPDadsPlayer();
			mHandler = pSV.getPHandler();
			mConnection = pSV.getPConnection();
            mIsPaused = savedInstanceState.getBoolean("paused");
            System.out.print(pSV.getOutput());
		}
		
		if(mDadsPlayer == null) {
			startBinding();
		} else {
			mBound = true;
		}
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        startBinding();
        outputSetup();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putBoolean("paused", mIsPaused);
        pSV.setOutput(getOutput());
        
        super.onSaveInstanceState(outState);
    }

	@Override
	protected void onStop()
	{
		super.onStop();
	}
	
	private void outputSetup() {
		//DAD NOTE: This just sets up an area outside the app to view output
		try
		{
			File mFile = new File(OUT_FILE_PATH);
			
			ps = new PrintStream(mFile);

			//DAD NOTE:
			//This makes System.out.println(string)
			//Send output to our file
			System.setOut(ps);
			
			et.setText("Successful setup");
		}
		catch (FileNotFoundException e)
		{
			Log.v("Output setup", e.toString());
			et.setText("File not found");
		}

	}
	
    public void pause(View v) {
        mDadsPlayer.pause();
    }
    
    public void play(View v) {

        if(mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch(msg.what) {
                        case PAUSE:
                            if(mIsPaused == false) {
                                et.setText("Paused");
                                mIsPaused = true;
                            } else {
                                et.setText(songName);
                                mIsPaused = false;
                            }
                        break;
                        
                        case SONG_NAME:
                            Bundle mBundle = msg.getData();
                            songName = mBundle.getString("name");
                            et.setText(songName);
                        break;
                    }
                }
            };
        }
        pSV.setPHandler(mHandler);
        mDadsPlayer.setHandler(mHandler);
        
        if(mIsPaused == false) {
            startPlaying();
        } else {
            mIsPaused = false;
            mDadsPlayer.pause();
        }
	}
    
    public void playlist(View v) {
        System.out.println("MainActivity: playlist");
        Log.e("Dadsradio","Building intent");
        Intent mIntent = new Intent(this,PlaylistEditor.class);
        try {
            startActivity(mIntent);
        } catch(Exception e) {
            System.out.println(e);
        }
    }
    
    public void prevSong(View v) {
        mDadsPlayer.prevSong();
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
						String foo = mDadsPlayer.dadPlay();
						if(foo != null) {
							System.out.println("Errors with prep: " + foo);
						}
					} catch(Exception e) {
						System.out.println(e);
					}
				}
			}).start();
	}
	
	public void songPicker(View v) {
        //temporary
        //indexMusic needs to be moved to the background
		System.out.println("Main:songPicker");
		indexMusic();
	}
	
	public void songDisplay() {
		final Dialog dialog = new Dialog(this);
		final ArrayList<String> mSongs = new ArrayList<String>();
        final ArrayList<Song> mSongss = new ArrayList<Song>();
        final ArrayList<Song> mSelectedSongs = new ArrayList<Song>();
        Song mSong = new Song();
        
		dialog.setContentView(R.layout.songpicker);
		dialog.setTitle("Song Selection");
		dialog.getWindow().getAttributes().width = WindowManager.LayoutParams.FILL_PARENT;
		dialog.getWindow().getAttributes().height = WindowManager.LayoutParams.FILL_PARENT;
		
		
		final ListView lv = (ListView)dialog.findViewById(R.id.Songs);
		
		final Button mBOK = (Button)dialog.findViewById(R.id.OK);
		final Button mCancel = (Button)dialog.findViewById(R.id.Cancel);
		
		mSongs.clear();
        mSongss.clear();
		if(pDirs != null) {
			for(String s : pDirs) {
				for(String t : new File(s).list(musicFilter)) {
					mSongs.add(t);
                    mSong.setName(s + File.separator + t);
                    mSong.setFile(new File(mSong.getName()));
                    mSongss.add(mSong);
                    mSong = new Song();
				}
			}
		}
		
		final ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,mSongs);
		
		if(lv != null) {
		    lv.setAdapter(mAdapter);
		}
		//Ok can't be picked without making a selection in
		//the ListView
		mBOK.setEnabled(false);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView < ? > adapter, View view, int position, long l) {
                mSelectedSongs.add(mSongss.get(position));
				mBOK.setEnabled(true);
			}
		});
		
		mBOK.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
                mDadsPlayer.setMSongs(mSelectedSongs);
                mSelectedSongs.clear();
				dialog.dismiss();
            }
		});
		
		mCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
                mSelectedSongs.clear();
			    dialog.dismiss();
		    }
		});
			
		dialog.show();
	}
    
    public void test(View v) {
        //This responds to test button
        //and should be removed in production
        Playlist mPlaylist = new Playlist(this);
        
        System.out.println("Calling test");
        Log.d("DadsRadio","Calling test");
        mPlaylist.test();
        System.out.println("Finished");
        Log.d("DadsRadio","Finished");
        
    }
    
}
