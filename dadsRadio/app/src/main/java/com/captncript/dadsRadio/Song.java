package com.captncript.dadsRadio;
import java.io.File;

/* 
  This class is an application wide variable type
  used to store and manipulate songs
*/

public class Song {
    private File file;
    private String name;
    
    public Song() {
        //empty constructor
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }


    public void setFile(File file)
    {
        this.file = file;
    }

    public File getFile()
    {
        return file;
    }
}
