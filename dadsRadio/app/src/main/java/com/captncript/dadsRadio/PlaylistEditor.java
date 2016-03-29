package com.captncript.dadsRadio;
import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class PlaylistEditor extends Activity {
    private ArrayList<Song> pSongs = new ArrayList<Song>();
    private String playlistName = "testing"; //This is assigned only for testing
    private TextView pTV;
    private ListView pLV1,pLV2;
    private Playlist pPlaylist = new Playlist(playlistName, this);
    
    @Override
    protected void onStart()
    {
        super.onStart();
        System.out.println("playlistEditor:onStart");
        setContentView(R.layout.playlist);
        
        pTV = (TextView)findViewById(R.id.playlistName);
        pLV1 = (ListView)findViewById(R.id.allSongs);
        pLV2 = (ListView)findViewById(R.id.playlistSongs);
        
        Intent receivedIntent = getIntent();
        System.out.println("Loading manager");
        getLoaderManager().initLoader(0,null,pPlaylist);
       // playlistName = receivedIntent.getStringExtra("playlistName");
        
        //pTV.setText(playlistName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // TODO: Implement this method
        getMenuInflater().inflate(R.menu.options,menu);
        
        return super.onCreateOptionsMenu(menu);
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
