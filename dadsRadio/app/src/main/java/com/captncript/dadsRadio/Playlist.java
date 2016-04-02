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

public class Playlist implements LoaderManager.LoaderCallbacks<Cursor> {
    /*
        Holds a name for the Playlist
        Holds arrayList of song variables.
        Allows adding and removing songs.
        Allows returning a song from a given position
        Activates a handler when loader is finished if given
        Manages a count of all songs in the list.
        
        TODO: Manage playlist database interaction(adding, removing, indexing)
              Manage isActive
              Keep track of active song(Possibly);
              Might need to make parcelable
    */
    
    
    private String pName;
    private int pCount;
    private ArrayList<Song> pSongs = new ArrayList<Song>();
    private Context pApplicationContext;
    
    private Handler pHandler; //Should be set with setHandler
    private int pCode; //This will be used in combination with the handler
    
    private Cursor cursor; //This will be used to return a cursor when requested
    
    private boolean isActive; //Used to see if this playlist is being used by dadsPlayer
    
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
        System.out.println("Playlist:onCreateLoader");
        Uri mUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = null;  // TODO: update this to the pieces I need
        String selection = "is_music = 1"; //This has the query only return music instead of all audii files
        String[] selectionArgs = null;
        String sort = null;
        
        return new CursorLoader(pApplicationContext,mUri,projection,selection,selectionArgs,sort);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        System.out.println("Playlist:onLoadFinished");
        this.cursor = data;
        
        Song song;
        data.moveToFirst(); // Moves to first row in the table
        for(int i=0;i<data.getCount();i++) {
            song = new Song();  // TODO:write a different way

            song.setFile(new File(data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.DATA)))); //gets the file path used to play the song
            if(song != null) {
                addSong(song);  // TODO: This could be an army of function calls change to bulk process
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
        System.out.println("Playlist:removeSong");
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
        //TODO: implement this
        System.out.println("Playlist:newPlaylist");
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
    
    public Cursor getCursor() {
        return this.cursor;
    }
}
