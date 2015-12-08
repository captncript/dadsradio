package com.captncript.dadsRadio;
import android.app.*;
import android.media.*;
import android.os.*;
import android.content.*;

import java.io.File;
import java.net.URI;

public class DadsPlayer extends Service implements MediaPlayer.OnPreparedListener, 
MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
	
	MediaPlayer mp = null;
	File mSongs[] = null;
	
	public DadsPlayer(File mSongs[]) {
		this.mSongs = mSongs;
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		// TODO: Implement this method
		super.onStart(intent, startId);
		
		//need to convert java.net.uri to android.net.uri
		//mp = MediaPlayer.create(getApplicationContext(), mSongs[0].toURI());
		
	}
	
	@Override
	public void onPrepared(MediaPlayer p1)
	{
		// TODO: Implement this method
		mp.start();
	}

	@Override
	public IBinder onBind(Intent p1)
	{
		//This function has to exist, but I don't
		//believe we will use it
		return null;
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

}
