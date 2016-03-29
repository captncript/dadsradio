package com.captncript.dadsRadio;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Playlist implements LoaderManager.LoaderCallbacks<Cursor> {
    private String pName;
    private int pCount;
    private ArrayList<Song> pSongs = new ArrayList<Song>();
    private Context pApplicationContext;
    
    private Handler pHandler; //Should be set with setHandler
    private int pCode; //This will be used in combination with the handler
    
    public void addSong(Song song) {
        //song is the Song variable to be added to the pSongs ArrayList
        //pCount is updated by adding 1
        System.out.println("Playlist:addSong");
        
        pSongs.add(song);
        pCount++;
    }
    
    public int getCount() {
        return this.pCount;
    }

    public String getName() {
        return this.pName;
    }
    
    public Song getSong(int position) {
        return pSongs.get(position);
    }
    
    public ArrayList<Song> getSongs() {
        System.out.println("Playlist:getSongs");
        return this.pSongs;
    }
    
    public Playlist(Context context) {
        //Constructor
        //Initilizes pCount
        //Initilizes pApplicationContext with calling activities Context
        pCount = 0;
        this.pApplicationContext = context;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri mUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;
        String sort = null;  //TODO: see if randomization can be moved to here
        
        return new CursorLoader(pApplicationContext,mUri,projection,selection,selectionArgs,sort);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Song song;
        data.moveToFirst(); // Moves to first row in the table
        for(int i=0;i<data.getCount();i++) {
            song = new Song();
            if(data.getInt(data.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC)) > 0) { //Checks to make sure the row is music not a ringtone, etc.
                song.setFile(new File(data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.DATA)))); //gets the file path used to play the song
                if(song != null) {
                    addSong(song);
                }
            }
            data.moveToNext();
        }
        if(pHandler != null) { //Runs if given a handler
            Message msg = Message.obtain(pHandler,pCode);
            msg.sendToTarget();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Loader doesn't change with this application
        //This will probably remain empty
        //May be useful when searching
    }

    public Playlist(String name, Context context) {
        //Constructor
        this.pName = name;
        this.pApplicationContext = context;
        pCount = 0;
    }
    
    public void removeSong(int position) {
        //Position is the index in the array pSongs
        //Removes a Song from pSongs ArrayList
        //Checks to make sure a song exists before an attemot is made
        //decreases pCount by 1 if Song is removed
        if(pCount >= 1) {
            pSongs.remove(position);
            
            pCount--;
        }
    }
    
    public void setName(String name) {
        this.pName = name;
    }
    
    public void write() {
        /*
            Requires pName to be set
            Writes file with pName as file name
            Appends to masterList all list names
            
            TODO: make a database that's connected to a cursor?
            SQLiteOpenHelper
        */
    }
    
    private void newPlaylist() {
        PlaylistDatabase playlistDB = new PlaylistDatabase(pApplicationContext,pName);
        SQLiteDatabase db = playlistDB.getWritableDatabase();
        
    }
    
    public void setHandler(Handler hand) {
        this.pHandler = hand;
    }
    
    public void setHandlerCode(int code) {
        //Set the code for the "what" variable in the Message to be returned
        this.pCode = code;
    }
}
