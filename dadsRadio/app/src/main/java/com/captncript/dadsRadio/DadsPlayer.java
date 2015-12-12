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

public class DadsPlayer extends Service implements MediaPlayer.OnPreparedListener, 
MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
	
	private final IBinder mIBinder = new LocalBinder();
	
	MediaPlayer mp = null;
	File mSongs[] = new File[10];
	TextView tv = null;
	
	
	
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
		File mSong = new File("/storage/emulated/0/Ringtones/hangouts_incoming_call.ogg");
		
		mSongs[0] = mSong;
		mp = MediaPlayer.create(getApplicationContext(),android.net.Uri.parse(mSongs[0].toString()));
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
		
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer p1)
	{
		// TODO: Implement this method
		//This will play the next song in the succession
	}
	
	public String testing(){
		return ("on the inside");
	}
	
}
