package com.captncript.dadsRadio;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import android.database.DatabaseUtils;

public class PlaylistEditor extends Activity {
    public final static int ALL_SONGS = 0;
    
    
    private ArrayList<Song> pSongs = new ArrayList<Song>();
    private String playlistName = "testing"; //This is assigned only for testing
    private TextView pTV;
    private ListView pLV1,pLV2;
    private Playlist pPlaylist = new Playlist(playlistName, this);
    private Context context = this;
    
    private Handler pHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case ALL_SONGS:
                    Cursor mCursor = pPlaylist.getCursor();
                    DatabaseUtils.dumpCursor(mCursor,System.out);
                    String[] from = {"_display_name"};
                    int[] to = {android.R.id.text1};
                    SimpleCursorAdapter sca = new SimpleCursorAdapter(context,android.R.layout.simple_list_item_1,mCursor,from,to);
                    ListView lv = (ListView)findViewById(R.id.allSongs);
                    lv.setAdapter(sca);
                    sca.notifyDataSetChanged();
            }
        }
        
    };
    
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
        pPlaylist.setHandlerCode(ALL_SONGS);
        pPlaylist.setHandler(pHandler);
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
