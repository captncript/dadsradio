package com.captncript.dadsRadio;
import java.io.File;

/* 
  This class is an application wide variable type
  used to store and manipulate songs
  
  TODO: Fix formatting
        Add String source variable
        Fix playlist to use source
        Remove File?
*/

public class Song {
    private String name;
    private String source;
    
    public Song() {
        //empty constructor
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

}
