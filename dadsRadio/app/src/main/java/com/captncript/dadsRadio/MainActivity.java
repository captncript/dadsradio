package com.captncript.dadsRadio;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

/*
	TODO:
    07-05-16
    19:00

   -Add testing to improve performance
   -Add voice control
   -Make sure only one player can be active at a time
   -Test against sound interruptions
   -Correct active playlist management(nextSong crashes if playlist is changed)
   -Remove System.out.println and replace with Log statements
*/

public class MainActivity extends Activity {
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
	
	EditText et = null;
    
	DadsPlayer mDadsPlayer;
    DadsPlayer.LocalBinder binder;
	
	boolean mBound = false;
	boolean mIsPaused = false;
	boolean mFragExists = false;
	boolean mReOpened = false;
    
    String songName = new String(); // Remove?
    
	private Handler mHandler = null;
	private Playlist pPlaylist;
    
	private ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceConnected(ComponentName DadsPlayer, IBinder service) {
            if(binder == null) {
                binder = (DadsPlayer.LocalBinder) service;
			}
            mDadsPlayer = binder.getService();
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
            mBound = false;
        }
	};
    
    public String getOutput() {
        /* 
        Grabs up to 500 characters 
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

    public void nextSong(View v) {
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
		
        playerStarter();
        
		FragmentManager fm = getFragmentManager();
		pSV = (SteadyVariables)fm.findFragmentByTag("ps");
		
		et =  (EditText)findViewById(R.id.display);
		
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
		}
        
		if(mDadsPlayer == null) {
            outputSetup();
		} else {
			mBound = true;
		}
        
        mHandler = new Handler() { //Sets up handler to adjust ui when the player wants
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case PAUSE:
                        if(mIsPaused == false) { //TODO: Remove
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

        pSV.setPHandler(mHandler);
    }

    @Override
    protected void onRestart() {
        // TODO: consider if this is useful
        super.onRestart();

        outputSetup();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        pSV.setOutput(getOutput());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        //TODO: move binding to onCreate and save the variables to the fragment
        //TODO: call dadsPlayer to have it set the display text
        super.onStart();
        
        Intent intent = getIntent();
        Bundle passedVals = intent.getBundleExtra("all");
        
        if(intent.getFlags() == 0) {      //Causes this to only run on activity change
            pPlaylist = passedVals.getParcelable("playlist");
        }
        
        startBinding();
        
        if(mConnection == null) {
            System.out.println("connection null");
            mConnection = pSV.getPConnection();
        }
        //TODO: see if asynctask can be used to check variable
    }

    @Override
    protected void onStop() {
        unbindService(mConnection);
        
        super.onStop();
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
		} catch (FileNotFoundException e) {
			Log.v("Output setup", e.toString());
			et.setText("File not found");
		}

	}
	
    public void pause(View v) {
        mDadsPlayer.pause();
    }
    
    public void play(View v) {
        // TODO: if songs already playing do nothing
        if(mIsPaused == false) { //TODO: remove
            if(pPlaylist != null) {
                mDadsPlayer.setPlaylist(pPlaylist);
            }
            startPlaying();
        } else {
            mIsPaused = false; //TODO: remove
            mDadsPlayer.pause();
        }
	}
    
    public void playAll(View v) {
        //Grabs all music and plays in a random order
       
        final Playlist allSongs = new Playlist(this); //Creates playlist object to get all songs
        
        Handler playAllHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case PLAY_ALL_RANDOM: //Called by loaderFinished in playlist class
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
    
    private void playerStarter() {
        Intent intent = new Intent(this, DadsPlayer.class);
        startService(intent);
    }
    
    public void playlist(View v) { //name of class consider renaming
        // TODO: loses dadsPlayer object
        // Starts a second player on return
        Intent mIntent = new Intent(this,PlaylistEditor.class);
        Bundle toPass = new Bundle();
        
        toPass.putParcelable("playlist",pPlaylist);
        
        mIntent.putExtra("all",toPass);
        
        if(pPlaylist != null && pPlaylist.getCount() > 0) {
            toPass.putBoolean("activePlaylist", true);
            toPass.putString("playlistName", pPlaylist.getName());
        }
        
        try {
            startActivity(mIntent);
        } catch(Exception e) {
            Log.e(TAG,e.toString());
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
			Log.e(TAG,e.toString());
		}
	}
	
	private void startPlaying() {
        //play() MUST have been called first
		//TODO: consider handling this a different way
		new Thread(new Runnable() { //Creates new thread to host the player
				@Override
				public void run() {
					try{
						String errorReport = mDadsPlayer.dadPlay(); //errorReport receives error messages TODO: change the name
						if(errorReport != null) {
							Log.e(TAG,errorReport);
						}
					} catch(Exception e) {
						Log.e(TAG,e.toString());
					}
				}
			}).start();
	}
    
    public void test(View v) {
        //This responds to test button
        //and should be removed in production
        
        pPlaylist.debugSongsOutput();
    }
   
}
