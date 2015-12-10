package com.captncript.dadsRadio;

import android.app.*;
import android.os.*;

import java.io.*;
import android.util.*;

import java.net.URL;
import android.widget.*;
import android.content.*;
import android.media.*;

public class MainActivity extends Activity 
{
	//This is the output stream for development uses
	public PrintStream pss = null;
	
	//This is the object used to
	//interact with the service
	public DadsPlayer player = null;
	
	public static int ARTIST = 1;
	public static int SONG = 2;
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		//Runs a setup for outputting
		//data during development.
		//This works specifically for me
		//you will probably want to design
		//your own.
		outputSetup();
		System.out.println("Main: findSongs()");
		findSongs("testing",0);
    }

	@Override
	protected void onRestart() {
		super.onRestart();

		outputSetup();
	}
	
	@Override
	protected void onStop()
	{
		System.out.println("Closing stream");
		pss.close();
		
		super.onStop();
	}
	
	private void outputSetup() {
		//This just sets up an area outside the app to view output
		try
		{
			File mFile = new File("/storage/emulated/0/AppProjects/dadsRadio-main/dadsradio/dadsRadio/app/output");
			pss = new PrintStream(mFile);

			//This makes System.out.println(string)
			//Send output to our file
			System.setOut(pss);
		}
		catch (FileNotFoundException e)
		{
			Log.v("Output setup", e.toString());
		}

	}
	
	public void startPlaying(File mSongs[]) {
		System.out.println("Start Playing");
		
		DadsPlayer mPlayer = new DadsPlayer(mSongs);
		Intent mIntent = new Intent(getApplicationContext(),com.captncript.dadsRadio.DadsPlayer.class);
		
		try {
			startService(mIntent);
		} catch(Exception e) {
			System.out.println(e);
		}
		
	}
	
	public void findSongs(String mDescriptor, int mFlag) {
		//This is for only a single descriptor
		System.out.println("FindSongs: start");
		
		File mSongs[] = new File[10];
		File mSong = new File("/storage/emulated/0/Ringtones/hangouts_incoming_call.ogg");
		
		if(mFlag == ARTIST) {
			//TODO: make this find songs by artist
			//name
		} else if(mFlag == SONG) {
			//TODO: make this find songs by
			//song name
		}
		
		mSongs[0] = mSong;
		
		startPlaying(mSongs);
	}
	
	public void findSongs(String mDescriptors[]) {
		File mSongs[] = null;
		
		startPlaying(mSongs);
	}
}
