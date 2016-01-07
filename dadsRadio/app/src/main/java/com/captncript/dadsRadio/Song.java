package com.captncript.dadsRadio;
import java.io.File;

public class Song {
    private File file;
    private String name;

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
    }}
