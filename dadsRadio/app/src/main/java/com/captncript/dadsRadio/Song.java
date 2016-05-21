package com.captncript.dadsRadio;
import android.os.Parcelable;
import android.os.Parcel;

/* 
  This class is an application wide variable type
  used to store and manipulate songs. An object of type
  Song will function as parcelable.
  
  TODO: Fix formatting
        Add String source variable
        Fix playlist to use source
        Remove File?
*/

public class Song implements Parcelable{
    private String name;
    private String source;
    
    public Song() {
        //empty constructor
    }
    
    public Song(Parcel imported) {
        //Array values eg. 0/1 are chosen based on the writeParcel
        String[] allData = new String[2];
        
        imported.readStringArray(allData);
        
        setName(allData[0]);
        setSource(allData[1]);
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        String[] allData = {name,source}; //Display name, string path for dadplayer
        
        parcel.writeStringArray(allData);
    }
    
    public static final Parcelable.Creator<Song> CREATOR
    = new Parcelable.Creator<Song>() {
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}
