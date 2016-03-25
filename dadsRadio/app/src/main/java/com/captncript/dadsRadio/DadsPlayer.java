package com.captncript.dadsRadio;

import android.app.*;
import android.content.*;
import android.media.*;
import android.os.*;
import android.widget.*;
import java.io.*;
import java.util.*;


public class DadsPlayer extends Service implements MediaPlayer.OnPreparedListener, 
MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
	// TODO: this needs some serious documentation
    
	private final IBinder mIBinder = new LocalBinder();
	
	MediaPlayer mp1 = null;
	MediaPlayer mp2 = null;
	TextView tv = null;
	
	ArrayList<File> mSongs = new ArrayList<File>();
	
	private boolean pIsComplete = false;
	private boolean pIsM1Paused = false;
	private boolean pIsM2Paused = false;
	private boolean pIsPrepared = false;
	
	private String aSyncError = null;
	
	private static final int PAUSE = 0;
    public static final int SONG_NAME = 1;
    
    public int songPlaying = 0;
	
	private boolean isM1Playing = false;
	private boolean isM2Playing = false;
	
	private Handler pHandler = null;
	
	public boolean getPIsPrepared() {
		//This is for testing can
		//probably be removed when finished
		return pIsPrepared;
	}
	
	public boolean getPIsComplete() {
		return pIsComplete;
	}
	
	public String getASyncError() {
		return aSyncError;
	}
	
	public void setMSongs(File mSongs[]) {
		//TODO: see if this can be removed
        for(File f : mSongs) {
			this.mSongs.add(f);
		}
	}
    
    public void setMSongs(ArrayList<Song> mSongs) {
        //takes an arraylist of type Song
        //Uses the file to play the song
        for(Song s : mSongs) {
            this.mSongs.add(s.getFile());
        }
    }
	
	public class LocalBinder extends Binder {
		DadsPlayer getService() {
			//This is part of a messaging with the
			//main activity technique
			return DadsPlayer.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent mIntent)
	{
		//This returns an iBinder to 
		//communicate with the main activity
		//mSongs.add(new File(SONG_URI));
		//mSongs.add(new File(SONG_URI2));
		
		return mIBinder;
	}
	
	public DadsPlayer() {
		//This is an empty constructor
	}
	
	@Override
	public void onPrepared(MediaPlayer mp)
	{
		pIsPrepared = true;
		System.out.println("onPrepared");
		//Calls method/function mp not service mp
		if(mp != null && mp.equals(mp1)) {
			System.out.println("mp equals mp1");
			isM1Playing = true;
			isM2Playing = false;
		} else if(mp != null && mp.equals(mp2)) {
			System.out.println("mp equals mp2");
			isM1Playing = false;
			isM2Playing = true;
		} else {
			System.out.println("Media player equation issues");
		}
		
		mp.start();
	}

	@Override
	public boolean onError(MediaPlayer p1, int p2, int p3)
	{
		// TODO: Implement this method
		aSyncError = "onError: error caught by listener";
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp)
	{
		//This will play the next song in the succession
		System.out.println("onCompletion");
		
		nextSong();
	}
	
	public void pause() {
		Message msg = null;
		
		if(isM1Playing) {
			System.out.println("Pausing mp1");
			pIsM1Paused = true;
			isM1Playing = false;
			mp1.pause();
		} else if(isM2Playing) {
			System.out.println("Pausing mp2");
			mp2.pause();
			pIsM2Paused = true;
			isM2Playing = false;
		} else if(pIsM1Paused) {
			mp1.start();
			pIsM1Paused = false;
			isM1Playing = true;
		} else if(pIsM2Paused) {
			mp2.start();
			pIsM2Paused = false;
			isM2Playing = true;
		}
		
		try{
		    msg = Message.obtain(pHandler, PAUSE);
		    msg.sendToTarget();
		}catch(Exception e) {
			System.out.println(e);
		}
	}
		
    public void nextSong() {
        System.out.println("Next song");
        songPlaying++;
        
        songChange();
    }
    
    public void prevSong() {
        System.out.println("Prev song");
        songPlaying--;
        
        songChange();
    }
    
    public void songChange() {
        if(isM1Playing) {
            System.out.println("starting second sound");
            //Repeated make it a function?

            try {
                mp2.reset();
                if(songPlaying < mSongs.size() && songPlaying >= 0) {
                    mp2.setDataSource(this, android.net.Uri.parse(mSongs.get(songPlaying).toString()));
                } else {
                    cleanUp();
                    pIsComplete = true;
                }
            }
            catch (SecurityException e) {
                System.out.println("mp2 set security: " + e.toString());
            }
            catch (IllegalArgumentException e) {
                System.out.println("mp2 set illegalArgument: " + e.toString());
            }
            catch (IllegalStateException e) {
                System.out.println("mp2 set IllegalState: " + e.toString());
            }
            catch (IOException e) {
                System.out.println("mp2 set io: " + e.toString());
            }

            try {
                if(!pIsComplete) {
                    Bundle bundle = new Bundle();
                    bundle.putString("name",mSongs.get(songPlaying).getName());

                    Message msg = Message.obtain(pHandler,SONG_NAME);
                    msg.setData(bundle);
                    msg.sendToTarget();
                    mp2.prepare();
                }
            }
            catch (IllegalStateException e) {
                System.out.println("mp2 prepare: " + e.toString());
            }
            catch (IOException e) {
                System.out.println("mp2 prepare: " + e.toString());
            }
            if(!pIsComplete) {
                mp1.stop();
            }
        } else if(isM2Playing) {
            System.out.println("Starting first sound");

            try {
                mp1.reset();

                if(songPlaying < mSongs.size() && songPlaying >= 0) {
                    mp1.setDataSource(this, android.net.Uri.parse(mSongs.get(songPlaying).toString()));
                } else {
                    cleanUp();
                    pIsComplete = true;
                }
            }
            catch (SecurityException e) {
                System.out.println("mp1 set: " + e.toString());
            }
            catch (IllegalArgumentException e) {
                System.out.println("mp1 set: " + e.toString());
            }
            catch (IllegalStateException e) {
                System.out.println("mp1 set: " + e.toString());
            }
            catch (IOException e) {
                System.out.println("mp1 set: " + e.toString());
            }
            
            
            try {
                if(!pIsComplete) {
                    Bundle bundle = new Bundle();
                    bundle.putString("name",mSongs.get(songPlaying).getName());

                    Message msg = Message.obtain(pHandler, SONG_NAME);
                    msg.setData(bundle);
                    msg.sendToTarget();
                    mp1.prepare();
                }
            }
            catch (IllegalStateException e) {
                System.out.println("mp1 prepare: " + e.toString());
            }
            catch (IOException e) {
                System.out.println("mp1 prepare: " + e.toString());
            }
            if(!pIsComplete) {
                //rewrite this so both are grouped
                //move piscomplete = true to cleanup
                mp2.stop();
            }
        }
    }
    
	public String dadPlay(){
		String mPrepped = null;
		
        pIsComplete = false;
        
		//Initialize mp1
		mp1 = new MediaPlayer();
		
		//Setup mp1 Listeners
		mp1.setOnErrorListener(this);
		mp1.setOnPreparedListener(this);
		mp1.setOnCompletionListener(this);

		//Initialize mp2
		mp2 = new MediaPlayer();

		//Setup mp2 Listeners
		mp2.setOnErrorListener(this);
		mp2.setOnPreparedListener(this);
		mp2.setOnCompletionListener(this);


		try
		{
			mp1.setDataSource(this, android.net.Uri.parse(mSongs.get(0).toString()));
			Bundle bundle = new Bundle();
            bundle.putString("name",mSongs.get(0).getName());
            
            Message msg = Message.obtain(pHandler,SONG_NAME);
            msg.setData(bundle);
            msg.sendToTarget();
            mp1.prepare();
		}
		catch (SecurityException e)
		{
			mPrepped = "Security: " + e.toString();
		}
		catch (IllegalArgumentException e)
		{
			mPrepped = "IllegalArgument: " + e.toString();
		}
		catch (IllegalStateException e)
		{
			mPrepped = "IllegalState: " + e.toString();
		}
		catch (IOException e)
		{
			mPrepped = "IO: " + e.toString();
		}
        
		return (mPrepped);
	}
	
	public void setHandler(Handler mHandler) {
		this.pHandler = mHandler;
	}
	
	public void cleanUp() {
		System.out.println("DadsPlayer: cleanUp");
		if(mp1 != null) {
			mp1.release();
			mp1 = null;
		}
		if(mp2 != null) {
			mp2.release();
			mp2 = null;
		}
        isM1Playing = false;
        isM2Playing = false;
        pIsM1Paused = false;
        pIsM2Paused = false;
        songPlaying = 0;
        mSongs.clear();
		System.out.println("DadsPlayer: clean");
		
		try {
			Thread.currentThread().join(100);
		} catch (InterruptedException e) {
			System.out.println("Interrupted: " + e.toString());
		}
		
	}
}
