package com.captncript.dadsRadio;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import android.os.Binder;
import android.os.IBinder;

public class PlaylistEditor extends Activity {
    public final static int ALL_SONGS = 0;
    
    private String playlistName;
    private TextView pTV;
    private ListView pLV1,pLV2;
    private Playlist pPlaylist;
    private Playlist playlistOfAllSongs = new Playlist(this);
    private Playlist originalPlaylist;
    private Context context = this;
    ArrayAdapter<String> playlistNames;
    
    private boolean isTemporary;
    private DadsPlayer.LocalBinder mainsBinder;
    
    private Handler pHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case ALL_SONGS:
                    loadAllSongs(); //Loads a display of all songs on device
                    loadPlaylist(); //Loads a display of all songs in the current playlist
                    break;
            }
        }
        
    };
    
    
    /*
        @intent boolean activePlaylist
        @intent String playlistName
        @intent Playlist playlist
    */
    @Override
    protected void onStart() {
        //This runs when called activity is started by another activity
        super.onStart();
        
        if(MainActivity.debug) {
            System.out.println("playlistEditor:onStart");
        }
        
        setContentView(R.layout.playlist);
        
        pTV = (TextView)findViewById(R.id.playlistName);
        pLV1 = (ListView)findViewById(R.id.allSongs);
        pLV2 = (ListView)findViewById(R.id.playlistSongs);
        
        Intent receivedIntent = getIntent();
        Bundle passedVals = receivedIntent.getBundleExtra("all");
        
        boolean hasActivePlaylist = passedVals.getBoolean("activePlaylist",false); //Defaults to false, but checks to see if there is an active playlist being sent
        mainsBinder = (DadsPlayer.LocalBinder)passedVals.getBinder("binder");
        
        playlistOfAllSongs.setHandlerCode(ALL_SONGS);
        playlistOfAllSongs.setHandler(pHandler);
        
        getLoaderManager().initLoader(0,null,playlistOfAllSongs);
        
        if(hasActivePlaylist) {
            playlistName = passedVals.getString("playlistName");
            pPlaylist = passedVals.getParcelable("playlist");
            originalPlaylist = pPlaylist;
            isTemporary = false;
            loadPlaylist();
        } else {
            playlistName = "Temp";
            pPlaylist = new Playlist(this);
            isTemporary = true;
        }
        pTV.setText(playlistName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options,menu);
        
        return super.onCreateOptionsMenu(menu);
    }
    
    private void loadAllSongs() {
        if(MainActivity.debug) {
            System.out.println("PlaylistEditor: loadAllSongs");
        }
        
        //This will populate the left layout
        //allSongs with all songs on the system
        final Cursor mCursor = playlistOfAllSongs.getCursor();
        String[] from = {"_display_name"};
        int[] to = {android.R.id.text1}; //assigns int "to" to an internal view
        final SimpleCursorAdapter sca = new SimpleCursorAdapter(context,android.R.layout.simple_list_item_1,mCursor,from,to);
        final ListView allSongsList = (ListView)findViewById(R.id.allSongs);

        allSongsList.setAdapter(sca); //Assigns adapter holding data to the listview
        sca.notifyDataSetChanged(); //Updates graphics
        
        allSongsList.setOnItemClickListener(new OnItemClickListener() {
            
            public void onItemClick(AdapterView<?> av, View view,int position, long id) {
                if(MainActivity.debug) {
                    System.out.println("PlaylistEditor:allSongsItemClick");
                }
                
                Song toAdd = new Song();
                
                mCursor.moveToPosition(position);
                
                toAdd.setName(mCursor.getString(mCursor.getColumnIndex("_display_name")));
                toAdd.setSource(mCursor.getString(mCursor.getColumnIndex("_data")));
                
                pPlaylist.addSong(toAdd);
                
                playlistNames.add(toAdd.getName());
            }
        });
    }
    
    private void loadPlaylist() {
        if(MainActivity.debug) {
            System.out.println("PlaylistEditor:loadPlayist");
        }
        //Fill in right panel with current playlist
        
        ArrayList<Song> allSongs = new ArrayList<Song>();
        
        if(pPlaylist.getCount() > 0) {
            allSongs = pPlaylist.getSongs();
        }
        
        final ArrayList<String> displayNames = new ArrayList<String>();
        playlistNames = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,displayNames);
        
        
        for(Song s : allSongs) {
            displayNames.add(s.getName());
        }
    
        pLV2.setAdapter(playlistNames);
        playlistNames.notifyDataSetChanged();
        
        pLV2.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> av, View view,int position, long id) {
                    if(MainActivity.debug) {
                        System.out.println("PlaylistEditor:loadPlaylistItemClick");
                    }
                    
                    displayNames.remove(position);
                    playlistNames.notifyDataSetChanged();
                    
                    pPlaylist.removeSong(position);
                }
        });
    }
    
    public void save(View view) {
        if(MainActivity.debug) {
            System.out.println("PlaylistEditor:save");
        }
        
        pPlaylist.write();
        
        goToMain();
    }
    
    public void goToMain() {
        //This starts the mainActivity
        if(MainActivity.debug) {
            System.out.println("PlaylistEditor:goToMain");
        }
        
        Bundle toPass = new Bundle();
        Intent intent = new Intent(this,MainActivity.class);
        
        toPass.putParcelable("playlist",pPlaylist);
        toPass.putBinder("binder",mainsBinder);
        
        intent.putExtra("all", toPass);

        startActivity(intent);
    }
    
    public void cancel(View view) {
        if(MainActivity.debug) {
            System.out.println("PlaylistEditor:cancel");
        }
        // TODO: playlist change is remaining
        pPlaylist = originalPlaylist;
        goToMain();
    }
}
