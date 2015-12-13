package com.captncript.dadsRadio;

import android.app.*;
import android.media.*;
import android.os.*;
import android.content.*;
import android.widget.*;
import android.view.*;

import java.io.File;
import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.io.*;


public class DadsPlayer extends Service implements MediaPlayer.OnPreparedListener, 
MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
	
	private final IBinder mIBinder = new LocalBinder();
	
	MediaPlayer mp = null;
	TextView tv = null;
	
	File mSongs[] = new File[10];
	
	private boolean pIsPrepared = false;
	private boolean pIsComplete = false;
	private String aSyncError = null;
	
	//Switch which is commented for your dev
	private static final String SONG_URI="/storage/emulated/0/Ringtones/hangouts_incoming_call.ogg";
	//private static final String SONG_URI="";
	
	
	public boolean getPIsPrepared() {
		return pIsPrepared;
	}
	
	public boolean getPIsComplete() {
		return pIsComplete;
	}
	
	public String getASyncError() {
		return aSyncError;
	}
	
	public class LocalBinder extends Binder {
		DadsPlayer getService() {
			//This is part of a messaging with the
			//main activity technique
			return DadsPlayer.this;
		}
	}
	
	public DadsPlayer() {
	}

	@Override
	public void onPrepared(MediaPlayer mp)
	{
		pIsPrepared = true;
		
		//Calls method/function mp not service mp
		mp.start();
	}

	@Override
	public IBinder onBind(Intent mIntent)
	{
		//This returns an iBinder to 
		//communicate with the main activity
		return mIBinder;
	}

	@Override
	public boolean onError(MediaPlayer p1, int p2, int p3)
	{
		// TODO: Implement this method
		aSyncError = "onError: error caught by listener";
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer p1)
	{
		// TODO: Implement this method
		//This will play the next song in the succession
		//Probably use a second media player to chain songs
		
		pIsComplete = true;
	}
	
	public String testing(){
		File mSong = new File(SONG_URI);
		String mPrepped = null;
		
		
		mSongs[0] = mSong;
		
		//Initialize mp
		mp = new MediaPlayer();
		
		//Setup Listeners
		mp.setOnErrorListener(this);
		mp.setOnPreparedListener(this);
		mp.setOnCompletionListener(this);
		
		try
		{
			mp.setDataSource(this, android.net.Uri.parse(mSongs[0].toString()));
			mp.prepare();
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
	
}
