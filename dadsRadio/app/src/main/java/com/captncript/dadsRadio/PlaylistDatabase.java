package com.captncript.dadsRadio;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PlaylistDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Playlists";
    private final String PLAYLIST_TABLE_NAME;
    private final String PLAYLIST_TABLE_CREATE;
    
    PlaylistDatabase(Context context, String tableName) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        PLAYLIST_TABLE_NAME = tableName;
        PLAYLIST_TABLE_CREATE = 
            "CREATE TABLE " + PLAYLIST_TABLE_NAME + " (" +
            "DisplayName" + " TEXT, " +
            "Source" + " TEXT);";
    }
    
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(PLAYLIST_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase p1, int p2, int p3) {
        // TODO: Implement this method
    }

}
