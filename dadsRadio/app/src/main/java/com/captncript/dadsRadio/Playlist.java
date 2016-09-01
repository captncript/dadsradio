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
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import com.captncript.dadsRadio.MainActivity;
import java.util.ArrayList;
import java.util.Collections;

public class Playlist implements LoaderManager.LoaderCallbacks<Cursor>, Parcelable {
    /*
        Holds a name for the Playlist
        Holds arrayList of song variables.
        Allows adding and removing songs.
        Allows returning a song from a given position
        Activates a handler when loader is finished if given
        Manages a count of all songs in the list.
        An object of type Playlist will functiin as parcelable
        
        TODO: Manage playlist database interaction(adding, removing, indexing)
              
    */
    
    //For parcel flattening and building *****************
    private final static String NAME = "Name"; 
    private final static String CODE = "HandlerCode";
    private final static String SONGCOUNT = "SongCount";
    public final static String SONGS = "Songs";
    //****************************************************
    
    
    private String pName;
    private int pCount;
    private ArrayList<Song> pSongs = new ArrayList<Song>();
    private Context pApplicationContext;
    
    private Handler pHandler; //Should be set with setHandler
    private int pCode; //This will be used in combination with the handler
    
    private Cursor cursor; //This will be used to return a cursor when requested
    
    public void addSong(Song song) {
        //song is the Song variable to be added to the pSongs ArrayList
        //pCount is updated by adding 1
        pSongs.add(song);
        pCount++;
    }
    
    public void addSong(ArrayList<Song> mSongs) {
        //Allows arraylists of songs to be added to a playlist at once
        for(Song s : mSongs) {
            pSongs.add(s);
            pCount++;
        }
    }
    
    public void clear() {
        //Leaves the playlist at a blank slate
        pCount = 0;
        pSongs.clear();
        pName = "";
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
        String[] projection = null;  // TODO: update this to the pieces I need
        String selection = "is_music = 1"; //This has the query only return music instead of all audii files
        String[] selectionArgs = null;
        String sort = null;
        
        return new CursorLoader(pApplicationContext,mUri,projection,selection,selectionArgs,sort);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.cursor = data;
        
        Song song;
        data.moveToFirst(); //Must be called before iterating over data or the cursor is pointing at nothing
        for(int i=0;i<data.getCount();i++) {
            song = new Song();  // TODO:write a different way maybe a clear function?

            song.setSource(data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.DATA))); //gets the file path used to play the song
            song.setName(data.getString(data.getColumnIndex("_display_name")));
            if(song != null) {
                //Make list here
                addSong(song);  // TODO: This could be an army of function calls change to bulk process
            }
            data.moveToNext();
        }
        //addSongs here
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
    
    public Playlist(Parcel parcel) {
        //Constructor
        Bundle imported = parcel.readBundle();
        //pSongs = new ArrayList<Song>();
        
        imported.setClassLoader(Playlist.class.getClassLoader());
        
        pName = imported.getString(NAME);
        pCode = imported.getInt(CODE);
        pCount = imported.getInt(SONGCOUNT);
        pSongs = imported.getParcelableArrayList(SONGS);
        //pSongs = parcel.readArrayList(Song.class.getClassLoader());
    }
    
    public void randomize() {
        //Call this to have the songs in the playlist shuffled
        Collections.shuffle(pSongs);
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
        //TODO: implement this
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        //Doesn't store context handler or handler code
        //These should be sent from the activity using a playlist
        Bundle toSend = new Bundle();
        
        toSend.putString(NAME,pName);
        toSend.putInt(CODE, pCode);
        toSend.putInt(SONGCOUNT,pCount);
        toSend.putParcelableArrayList(SONGS, pSongs);
        
        parcel.writeBundle(toSend);
    }

    public static final Parcelable.Creator<Playlist> CREATOR
    = new Parcelable.Creator<Playlist>() {
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };
    
    public void debugSongsOutput() {
        // TODO: change name
        Log.d(MainActivity.TAG, "Song count: " + pCount);
        
        for(Song s : pSongs) {
            Log.d(MainActivity.TAG, s.getName());
        }
    }
}
