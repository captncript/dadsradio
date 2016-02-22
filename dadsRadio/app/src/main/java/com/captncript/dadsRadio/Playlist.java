package com.captncript.dadsRadio;

import android.content.Context;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Playlist {
    private String pName;
    private int pCount;
    private ArrayList<Song> pSongs;
    
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
    
    public Playlist() {
        pCount = 0;
    }
    
    public Playlist(String name) {
        this.pName = name;
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
        //OutputStream os = new BufferedOutputStream();
    }
}
