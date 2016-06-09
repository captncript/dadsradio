package com.captncript.dadsRadio;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import android.widget.Toast;


public class DadsPlayer extends Service implements MediaPlayer.OnPreparedListener, 
MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
	// TODO: this needs some serious documentation
    // Possibly change name of class to PlayerManager
	private final IBinder mIBinder = new LocalBinder();
	
	MediaPlayer mp1 = null;
	MediaPlayer mp2 = null;
	TextView tv = null;
	
	private boolean pIsComplete = false;
	private boolean pIsM1Paused = false;
	private boolean pIsM2Paused = false;
	private boolean pIsPrepared = false;
	
	private String aSyncError = null;
	
    //Handler codes
	private static final int PAUSE = 0;
    public static final int SONG_NAME = 1;
    
    public int songPlaying = 0;
	
	private boolean isM1Playing = false;
	private boolean isM2Playing = false;
	
	private Handler pHandler = null;
    
    private Playlist currentPlaylist;
	
    public void clearPlaylist() {
        currentPlaylist.clear();
    }
    
	public boolean getPIsPrepared() {
		//This is for testing can
		//probably be removed when finished
		return pIsPrepared;
	}
	
	public boolean getPIsComplete() {
		return pIsComplete;
	}
	
	public String getASyncError() {
		return aSyncError;
	}
	
    public boolean isPlaying() {
        if(mp1 != null && mp2 != null) {
            if(mp1.isPlaying() || mp2.isPlaying()) {
                return true;
            }
        }
        return false;
    }
    
	public class LocalBinder extends Binder {
		DadsPlayer getService() {
			//This is part of a messaging with the
			//main activity technique
			return DadsPlayer.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent mIntent) {
		//This returns an iBinder to 
		//communicate with the main activity
		
		return mIBinder;
	}
	
	public DadsPlayer() {
		//This is an empty constructor
	}
	
	@Override
	public void onPrepared(MediaPlayer mp) {
        if(MainActivity.debug) {
            System.out.println("dadsplayer:onPrepared");
        }
        
		pIsPrepared = true; //TODO: is this used?
		
		//Calls method/function mp not service mp
		if(mp != null && mp.equals(mp1)) {
			isM1Playing = true;
			isM2Playing = false;
		} else if(mp != null && mp.equals(mp2)) {
			isM1Playing = false;
			isM2Playing = true;
		} else {
			System.out.println("Media player equation issues");
		}
		
		mp.start();
	}

	@Override
	public boolean onError(MediaPlayer p1, int p2, int p3) {
		if(MainActivity.debug) {
            System.out.println("error with a mediaPlayer object");
        }
        
        aSyncError = "onError: error caught by listener";
		
        return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		//This will play the next song in the succession
		if(MainActivity.debug) {
            System.out.println("onCompletion");
		}
        
		nextSong();
	}
	
	public void pause() {
        if(MainActivity.debug) {
            System.out.println("DadsPlayer:pause");
        }
        
		Message msg = null;
		
		if(isM1Playing) {
			pIsM1Paused = true;
			isM1Playing = false;
			mp1.pause();
		} else if(isM2Playing) {
			mp2.pause();
			pIsM2Paused = true;
			isM2Playing = false;
		} else if(pIsM1Paused) {
			mp1.start();
			pIsM1Paused = false;
			isM1Playing = true;
		} else if(pIsM2Paused) {
			mp2.start();
			pIsM2Paused = false;
			isM2Playing = true;
		}
		
		try {
		    msg = Message.obtain(pHandler, PAUSE);
		    msg.sendToTarget();
		} catch(Exception e) {
			System.out.println(e);
		}
	}
		
    public void nextSong() {
        if(MainActivity.debug) {
            System.out.println("DadsPlayer:Next song");
        }
            
        songPlaying++;
        
        songChange();
    }
    
    public void prevSong() {
        if(MainActivity.debug) {
            System.out.println("DadsPlayer:Prev song");
        }
        
        songPlaying--;
        
        songChange();
    }
    
    public void songChange() {
        if(MainActivity.debug) {
            System.out.println("DadsPlayer:songChange");
        }
        
        if(isM1Playing) {
            //Repeated make it a function?

            try {
                mp2.reset();
                if(songPlaying < currentPlaylist.getCount() && songPlaying >= 0) {
                    mp2.setDataSource(currentPlaylist.getSong(songPlaying).getSource()); //Switch to playlist 
                } else {
                    cleanUp();
                    pIsComplete = true;
                }
            } catch (SecurityException e) {
                System.out.println("mp2 set security: " + e.toString());
            } catch (IllegalArgumentException e) {
                System.out.println("mp2 set illegalArgument: " + e.toString());
            } catch (IllegalStateException e) {
                System.out.println("mp2 set IllegalState: " + e.toString());
            } catch (IOException e) {
                System.out.println("mp2 set io: " + e.toString());
            }

            try {
                if(!pIsComplete) {
                    //Done in multiple places make it a function
                    Bundle bundle = new Bundle();
                    bundle.putString("name",currentPlaylist.getSong(songPlaying).getName());

                    Message msg = Message.obtain(pHandler,SONG_NAME); //Displays the songs name
                    msg.setData(bundle);
                    msg.sendToTarget();
                    mp2.prepare();
                }
            } catch (IllegalStateException e) {
                System.out.println("mp2 prepare: " + e.toString());
            } catch (IOException e) {
                System.out.println("mp2 prepare: " + e.toString());
            }
            if(!pIsComplete) {
                mp1.stop();
            }
        } else if(isM2Playing) {
            try {
                mp1.reset();

                if(songPlaying < currentPlaylist.getCount() && songPlaying >= 0) { //Switch to playlist
                    mp1.setDataSource(currentPlaylist.getSong(songPlaying).getSource()); //Switch to playlist
                } else {
                    cleanUp();
                    pIsComplete = true;
                }
            } catch (SecurityException e) {
                System.out.println("mp1 set: " + e.toString());
            } catch (IllegalArgumentException e) {
                System.out.println("mp1 set: " + e.toString());
            } catch (IllegalStateException e) {
                System.out.println("mp1 set: " + e.toString());
            } catch (IOException e) {
                System.out.println("mp1 set: " + e.toString());
            }
            
            try {
                if(!pIsComplete) {
                    Bundle bundle = new Bundle();
                    bundle.putString("name",currentPlaylist.getSong(songPlaying).getName()); //Switch to playlist

                    Message msg = Message.obtain(pHandler, SONG_NAME);
                    msg.setData(bundle);
                    msg.sendToTarget();
                    mp1.prepare();
                }
            } catch (IllegalStateException e) {
                System.out.println("mp1 prepare: " + e.toString());
            } catch (IOException e) {
                System.out.println("mp1 prepare: " + e.toString());
            }
            if(!pIsComplete) {
                //rewrite this so both are grouped
                mp2.stop();
            }
        }
    }
    
	public String dadPlay() {
        if(MainActivity.debug) {
            System.out.println("DadsPlayer:dadPlay");
		}
        
        if(isPlaying()) {
            mp1.stop();
            mp2.stop();
        }
        
        String mPrepped = null;
		
        pIsComplete = false;
        
		//Initialize mp1
		mp1 = new MediaPlayer();
		
		//Setup mp1 Listeners
		mp1.setOnErrorListener(this);
		mp1.setOnPreparedListener(this);
		mp1.setOnCompletionListener(this);

		//Initialize mp2
		mp2 = new MediaPlayer();

		//Setup mp2 Listeners
		mp2.setOnErrorListener(this);
		mp2.setOnPreparedListener(this);
		mp2.setOnCompletionListener(this);


		try {
			mp1.setDataSource(currentPlaylist.getSong(0).getSource()); //Switch to playlist
            Bundle bundle = new Bundle();
            bundle.putString("name",currentPlaylist.getSong(0).getName()); //Switch to playlist
            
            Message msg = Message.obtain(pHandler,SONG_NAME);
            msg.setData(bundle);
            msg.sendToTarget();
            mp1.prepare();
		} catch (SecurityException e) {
			mPrepped = "Security: " + e.toString();
		} catch (IllegalArgumentException e) {
			mPrepped = "IllegalArgument: " + e.toString();
		} catch (IllegalStateException e) {
			mPrepped = "IllegalState: " + e.toString();
		} catch (IOException e) {
			mPrepped = "IO: " + e.toString();
		}
        
		return (mPrepped);
	}
	
	public void setHandler(Handler mHandler) {
		this.pHandler = mHandler;
	}
	
    public void setPlaylist(Playlist playlist) {
        Toast t = new Toast(this);
        t.makeText(this,"Set",Toast.LENGTH_LONG).show();
        
        if(isPlaying()) {
            t.makeText(this,"Stopped",Toast.LENGTH_LONG).show();
            
            mp1.stop();
            mp2.stop();
            songPlaying = 0;
        }
        this.currentPlaylist = playlist;
    }
    
	public void cleanUp() {
		if(MainActivity.debug) {
            System.out.println("DadsPlayer: cleanUp");
		}
        
        if(mp1 != null) {
			mp1.release();
			mp1 = null;
		}
		if(mp2 != null) {
			mp2.release();
			mp2 = null;
		}
        isM1Playing = false;
        isM2Playing = false;
        pIsM1Paused = false;
        pIsM2Paused = false;
        songPlaying = 0;
		System.out.println("DadsPlayer: clean");
		
		try {
			Thread.currentThread().join(100);
		} catch (InterruptedException e) {
			System.out.println("Interrupted: " + e.toString());
		}
		
	}
}
