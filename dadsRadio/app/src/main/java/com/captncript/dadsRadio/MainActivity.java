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
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;

/*
	TODO:
    06-09-16
    17:00

   -Add testing control to improve performance
   -Add voice control
   -Make sure only one player can be active at a time
   -Test against sound interruptions
   -Correct active playlist management(nextSong crashes if playlist is changed)
*/

public class MainActivity extends Activity {
	public static final boolean debug = true;
    
    private SteadyVariables pSV;

	public PrintStream ps;
	
    public static final String TAG = "DadsRadio";
    
	public static final int ARTIST = 1;
	public static final int SONG = 2;
	public static final int REQUEST_MEDIA = 3;
    
    public static final int PAUSE = 0;
    public static final int SONG_NAME = 1;
	
	private static final String OUT_FILE_PATH = "/storage/emulated/0/AppProjects/DadsRadio/dadsradio/app/output";
    
    //playAllHandler Case(s)
    public static final int PLAY_ALL_RANDOM = 0;
	
	//DAD NOTE: This variable will hold the text box
	EditText et = null;
	private Button pPauseButton = null;
	
	DadsPlayer mDadsPlayer;
    DadsPlayer.LocalBinder binder;
	Button mButton = null;
	
	//DAD NOTE: This variable being true means that
	//we have a connection to the service(DadsPlayer.java)
	boolean mBound = false;
	boolean mIsPaused = false;
	boolean mFragExists = false;
	boolean mReOpened = false;
    
    String songName = new String(); // Remove?
    
	private Handler mHandler = null;
	private ArrayList<String> pDirs = new ArrayList<String>();
	private Playlist pPlaylist;
    
    FilenameFilter musicFilter = new FilenameFilter() {
        @Override
        public boolean accept(File p1, String name) {
            //Add other sound files
            if(debug) {
                System.out.println("MainActivity:musicFilter:accept");
            }
            
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
		public void onServiceConnected(ComponentName DadsPlayer, IBinder service) {
			if(debug) {
                System.out.println("MainActivity:mConnection:onServiceConnected");
			}
            if(binder == null) {
                Log.d(TAG, "New binder");
                binder = (DadsPlayer.LocalBinder) service;
			}
            mDadsPlayer = binder.getService();
            Log.d(TAG, "Player made");
			mBound = true;
			mDadsPlayer.setHandler(mHandler);
			
			pSV.setPDadsPlayer(mDadsPlayer);
			
			String mErr = mDadsPlayer.getASyncError();
			if(mErr != null) {
				System.out.println("Async errors: " + mErr);
			}
		}
        
        @Override
        public void onServiceDisconnected(ComponentName p1) {
            if(debug) {
                System.out.println("MainActivity:mConnection:service disconnected");
            }
            mBound = false;
        }
	};

    public void cleanUp(View v) {
        //This doesn't display and
        //locks down the program
        //Might remove
        // TODO: I dont believe this is running check before merge
        if(debug) {
            System.out.println("Main:cleanUp");
        }
       
        //This releases all remaining
        //media players
        mDadsPlayer.cleanUp();

        System.out.println("Main:Clean");
    }
        
    public void findSongs(String mDescriptor, int mFlag) {
        //This is for only a single descriptor
        //mDescriptor is artist or song name
        //mFlag indicates, which was sent
        if(debug) {
            System.out.println("MainActivity:findSongs");
        }
            
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
        if(debug) {
            System.out.println("MainActivity:findSongs:2");
        }
        startPlaying();
    }
    
    public String getOutput() {
        /* May be causing a delay.
        grabs up to 500 characters 
        from the output file and saves
        to be written when file is
        recreated. Called from 
        onSaveInstanceState */
        if(debug) {
            System.out.println("MainActivity:getOutput");
        }
        
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

    public void nextSong(View v) {
        if(debug) {
            System.out.println("MainActivity:nextSong");
        }
        
        if(mBound) {
            mDadsPlayer.nextSong();
        } else {
            Toast.makeText(this,"Player no longer connected restart", Toast.LENGTH_LONG).show();
        }
    }
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		
		FragmentManager fm = getFragmentManager();
		pSV = (SteadyVariables)fm.findFragmentByTag("ps");
		
		et =  (EditText)findViewById(R.id.display);
		pPauseButton = (Button)findViewById(R.id.pause);
		
        if(pSV == null) { //Only functions on state change does not work across activities
            pSV = new SteadyVariables();
            if(pSV.getOutput() != null) {
                Log.e("DadsRadio", "has output");
            }
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
            outputSetup();
            if(debug) {
                System.out.println("onCreate");
            }
            Log.e("DadsRadio","Binding");
			startBinding();
		} else {
			mBound = true;
		}
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        outputSetup();
        if(debug) {
            System.out.println("onRestart");
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("paused", mIsPaused);
        pSV.setOutput(getOutput());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(debug) {
            System.out.println("MainActivity:onStart");
        }
        
        Intent intent = getIntent();
        Bundle passedVals = intent.getBundleExtra("all");
        
        if(intent.getFlags() == 0) {      //Causes this to only run on activity change
            pPlaylist = passedVals.getParcelable("playlist");
            binder = (DadsPlayer.LocalBinder)passedVals.getBinder("binder");
            Log.d("DadsRadio", "Binder set");
        }
        
        if(mConnection == null) {
            System.out.println("connection null");
            mConnection = pSV.getPConnection();
        }
    }
	
	private void outputSetup() {
		//DAD NOTE: This just sets up an area outside the app to view output
		try {
			File mFile = new File(OUT_FILE_PATH);
			
			ps = new PrintStream(mFile);

			//DAD NOTE:
			//This makes System.out.println(string)
			//Send output to our file
			System.setOut(ps);
			
			et.setText("Successful setup");
		} catch (FileNotFoundException e) {
			Log.v("Output setup", e.toString());
			et.setText("File not found");
		}

	}
	
    public void pause(View v) {
        if(debug) {
            System.out.println("MainActivity:pause");
        }
        mDadsPlayer.pause();
    }
    
    public void play(View v) {
        // TODO: if songs already playing do nothing
        if(debug) {
            System.out.println("MainActivity:Play");
        }
        
        if(mHandler == null) {
            mHandler = new Handler() { //Sets up handler to adjust ui when the player wants
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
                        
                        case SONG_NAME: //Called to change the name displayed for the song
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
            if(pPlaylist != null) {
                mDadsPlayer.setPlaylist(pPlaylist);
            }
            startPlaying();
        } else {
            mIsPaused = false;
            mDadsPlayer.pause();
        }
	}
    
    public void playAll(View v) {
        //Grabs all music and plays in a random order
        if(debug) {
            System.out.println("MainActivity: playAll");
        }
        
        final Playlist allSongs = new Playlist(this); //Creates playlist object to get all songs
        
        Handler playAllHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case PLAY_ALL_RANDOM: //Called by loaderFinished in playlist class
                        if(debug) {
                            System.out.println("Mainactivity:playAllHandler:playAllRandom");
                        }
                        
                        allSongs.randomize(); //changes order of songs in the playlist
                        pPlaylist = allSongs;
                        mDadsPlayer.setPlaylist(pPlaylist);
                       
                        //starts player
                        play(null); //sends null to satisfy the view that isn't used
                        break;
                }
            }
        };

        allSongs.setHandler(playAllHandler);
        allSongs.setHandlerCode(PLAY_ALL_RANDOM);
        getLoaderManager().initLoader(0,null,allSongs); //Starts the cursor loader finding music
    }
    
    public void playlist(View v) { //name of class consider renaming
        // TODO: loses dadsPlayer object
        // Starts a second player on return
        if(debug) {
            System.out.println("MainActivity: playlist");
        }
        
        Intent mIntent = new Intent(this,PlaylistEditor.class);
        Bundle toPass = new Bundle();
        
        toPass.putBinder("binder", binder);
        toPass.putParcelable("playlist",pPlaylist);
        
        mIntent.putExtra("all",toPass);
        
        if(pPlaylist != null && pPlaylist.getCount() > 0) {
            toPass.putBoolean("activePlaylist", true);
            toPass.putString("playlistName", pPlaylist.getName());
        }
        
        try {
            startActivity(mIntent);
        } catch(Exception e) {
            System.out.println(e);
        }
    }
    
    public void prevSong(View v) {
        if(debug) {
            System.out.println("MainActivity:prevSong");
        }
        mDadsPlayer.prevSong();
    }
	
	public void startBinding() {
		//This should be called when starting 
		//to bind the service for general use
		if(debug) {
            System.out.println("MainActivity:StartBinding");
		}
        
        Intent mIntent = new Intent(this,com.captncript.dadsRadio.DadsPlayer.class);

		try {
			bindService(mIntent,mConnection,Context.BIND_AUTO_CREATE);
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public void startPlaying() {
        //play() MUST have been called first
		if(debug) {
            System.out.println("MainActivity:Start Playing");
		}
        
		new Thread(new Runnable() { //Creates new thread to host the player
				@Override
				public void run() {
					try{
						String foo = mDadsPlayer.dadPlay(); //foo receives error messages TODO: change the name
						if(foo != null) {
							System.out.println("Errors with prep: " + foo);
						}
					} catch(Exception e) {
						System.out.println(e);
					}
				}
			}).start();
	}
	
	public void songDisplay() {
        if(debug) {
            System.out.println("MainActivity:songDisplay");
		}
        
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
                    //mSong.setFile(new File(mSong.getName()));
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
                //mDadsPlayer.setMSongs(mSelectedSongs);
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
        if(debug) {
            System.out.println("MainActivity:Test");
        }
        //This responds to test button
        //and should be removed in production
        
        pPlaylist.debugSongsOutput();
    }
    
    public void test2(Playlist mPlaylist) {
        //Test run by hitting playAll
        if(debug){
            System.out.println("MainActivity: test2");
        }
        
        Intent mIntent = new Intent(this,PlaylistEditor.class);
        
        mIntent.putExtra("playlist", mPlaylist);
        mIntent.putExtra("activePlaylist", true);
        mIntent.putExtra("playlistName", "Test Playlist");
        
        try {
            startActivity(mIntent);
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}
