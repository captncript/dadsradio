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


//TODO:Move service to its own thread
public class DadsPlayer extends Service implements MediaPlayer.OnPreparedListener, 
MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
	
	private final IBinder mIBinder = new LocalBinder();
	
	MediaPlayer mp = null;
	TextView tv = null;
	
	File mSongs[] = new File[10];
	private boolean pIsPrepared = false;
	private boolean pIsComplete = false;
	private String aSyncError = null;

	public boolean isPIsPrepared() {
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
	public void onStart(Intent intent, int startId)
	{
		// TODO: Implement this method
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// TODO: Implement this method
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onPrepared(MediaPlayer p1)
	{
		// TODO: Implement this method
		pIsPrepared = true;
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
		aSyncError = "There was an error";
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer p1)
	{
		// TODO: Implement this method
		//This will play the next song in the succession
		
		pIsComplete = true;
	}
	
	public String testing(){
		File mSong = new File("/storage/emulated/0/Ringtones/hangouts_incoming_call.ogg");
		String mPrepped = null;
		
		
		mSongs[0] = mSong;
		
		mp = new MediaPlayer();
		
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
