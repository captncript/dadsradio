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
	
	MediaPlayer mp1 = null;
	MediaPlayer mp2 = null;
	TextView tv = null;
	
	File mSongs[] = new File[10];
	
	private boolean pIsPrepared = false;
	private boolean pIsComplete = false;
	private String aSyncError = null;
	
	//Switch which is commented for your dev
	private static final String SONG_URI="/storage/emulated/0/Ringtones/hangouts_incoming_call.ogg";
	private static final String SONG_URI2="/storage/emulated/0/Ringtones/hangouts_message.ogg";
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
	
	@Override
	public IBinder onBind(Intent mIntent)
	{
		//This returns an iBinder to 
		//communicate with the main activity
		return mIBinder;
	}
	
	public DadsPlayer() {
	}
	
	@Override
	public void onPrepared(MediaPlayer mp)
	{
		pIsPrepared = true;
		System.out.println("onPrepared");
		//Calls method/function mp not service mp
		if(mp != null && mp.equals(mp1)) {
			System.out.println("mp equals mp1");
		} else if(mp != null && mp.equals(mp2)) {
			System.out.println("mp equals mp2");
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
		// TODO: Fix this
		//This will play the next song in the succession
		//Probably use a second media player to chain songs
		System.out.println("onCompletion");
		
		try {
			//SystemClock.sleep(1000);
		}
		catch (Exception e) {
			System.out.println(e);
		}
		
		try {
			if(pIsComplete) {
			//Implement this function to release media players
			//also consider unbinding from
			//cleanup();
			} else {
				if(mp.equals(mp1)) {
					//Make this run mp2s song0
					System.out.println("starting second sound");
					mp2.prepare();
					mp1.stop();
				} else if(mp.equals(mp2)) {
					//Make this run mp1s song
					System.out.println("Starting first sound");
					mp1.prepare();
					mp2.stop();
				}
				
			}
				
			} catch(Exception e) {
				System.out.println(e);
			}
		}
	
	public String testing(){
		File mSong = new File(SONG_URI);
		String mPrepped = null;
		
		
		mSongs[0] = mSong;
		mSongs[1] = new File(SONG_URI2);
		
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
			mp1.setDataSource(this, android.net.Uri.parse(mSongs[0].toString()));
			mp1.prepare();
			mp2.setDataSource(this, android.net.Uri.parse(mSongs[1].toString()));
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

		//TODO:Check if this is thread safe
		return (mPrepped);
	}
	
	public void cleanUp() {
		System.out.println("DadsPlayer: cleanUp");
		if(mp1 != null) {
			mp1.release();
		}
		if(mp2 != null) {
			mp2.release();
		}
		
		try
		{
			Thread.currentThread().join();
		}
		catch (InterruptedException e)
		{
			System.out.println("Interrupted: " + e.toString());
		}

		System.out.println("DadsPlayer: clean");
	}
	
}
