package com.captncript.dadsRadio;
import android.app.*;
import android.media.*;
import android.os.*;
import android.content.*;

import java.io.File;
import java.io.PrintStream;
import java.net.URI;
import java.io.FileNotFoundException;
import android.widget.*;

public class DadsPlayer extends Service implements MediaPlayer.OnPreparedListener, 
MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
	
	MediaPlayer mp = null;
	File mSongs[] = new File[10];
	
	public DadsPlayer(File mSongs[]) {
		this.mSongs = mSongs;
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		System.out.println("onStart");
		// TODO: Implement this method
		super.onStart(intent, startId);
		File mSong = new File("/storage/emulated/0/Ringtones/hangouts_incoming_call.ogg");
		
		mSongs[0] = mSong;
		mp = MediaPlayer.create(getApplicationContext(),android.net.Uri.parse(mSongs[0].toString()));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		outputSetup();
		
		System.out.println("onStartCommand");
		
		// TODO: Implement this method
		return super.onStartCommand(intent, flags, startId);
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
	
	private void outputSetup() {
		//This just sets up an area outside the app to view output
		try
		{
			File mFile = new File("/storage/emulated/0/AppProjects/dadsRadio-main/dadsradio/dadsRadio/app/output");
			PrintStream pss = new PrintStream(mFile);

			//This makes System.out.println(string)
			//Send output to our file
			System.setOut(pss);
		}
		catch (FileNotFoundException e)
		{
			
		}

	}
}
