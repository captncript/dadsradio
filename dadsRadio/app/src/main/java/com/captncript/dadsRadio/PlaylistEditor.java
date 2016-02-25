package com.captncript.dadsRadio;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class PlaylistEditor extends Activity {
    private ArrayList<Song> pSongs = new ArrayList<Song>();
    private String playlistName;
    private TextView pTV;
    private ListView pLV1,pLV2;
    
    
    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d("Playlist","start");
        setContentView(R.layout.playlist);
        
        pTV = (TextView)findViewById(R.id.playlistName);
        pLV1 = (ListView)findViewById(R.id.allSongs);
        pLV2 = (ListView)findViewById(R.id.playlistSongs);
        
        Intent receivedIntent = getIntent();
       // playlistName = receivedIntent.getStringExtra("playlistName");
        
        //pTV.setText(playlistName);
    }
    
    private void loadAllSongs() {
        //This will populate the left layout
        //allSongs with all songs except those in
        //the playlist
    }
    
    private void loadPlaylist() {
        //Populate a playlist to check against allSongs
        //Fill in right panel
    }
}
