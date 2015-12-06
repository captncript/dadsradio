package com.captncript.dadsRadio;

import android.app.*;
import android.media.MediaPlayer;
import android.os.*;

import java.io.IOException;
import java.io.File;
import java.io.PrintStream;
import java.io.FileNotFoundException;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        outputSetup();
    }

    protected void outputSetup() {

        try {
            File file = new File("./error");
            PrintStream ps = new PrintStream(file);

            System.setOut(ps);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void sound() {
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource("./song.mp3");
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
