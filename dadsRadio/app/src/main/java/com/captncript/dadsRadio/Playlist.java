package com.captncript.dadsRadio;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;
import java.util.ArrayList;

public class Playlist implements LoaderManager.LoaderCallbacks<Cursor> {
    private String pName;
    private int pCount;
    private ArrayList<Song> pSongs;
    private Context pApplicationContext;
    public void addSong(Song song) {
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
        return this.pSongs;
    }
    
    public Playlist(Context context) {
        pCount = 0;
        this.pApplicationContext = context;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public Playlist(String name, Context context) {
        this.pName = name;
        this.pApplicationContext = context;
        pCount = 0;
    }
    
    public void removeSong(int position) {
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
            
            TODO: make a database thats connected to a cursor?
            SQLiteOpenHelper
        */
    }
    
    public void test() {
        //Learn to use a cursor loader
        Uri mUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;
        
        Cursor mCursor = pApplicationContext.getContentResolver().query(mUri,projection,selection,selectionArgs,null);
        
        if(mCursor != null) {
            mCursor.moveToFirst();
            
            for(String s : mCursor.getColumnNames()) {
                System.out.println(s);
            }
        } else {
            Toast.makeText(pApplicationContext,"no data",Toast.LENGTH_SHORT).show();
        }
        
        CursorLoader mCursorLoader = new CursorLoader(pApplicationContext);
        mCursorLoader.setUri(mUri);
        mCursorLoader.setProjection(projection);
        mCursorLoader.setSelection(selection);
        mCursorLoader.setSelectionArgs(selectionArgs);
        mCursorLoader.setSortOrder(null);
        mCursor = mCursorLoader.loadInBackground();
    }
}
