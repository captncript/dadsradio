package com.captncript.dadsRadio;
import android.app.*;
import android.os.*;

public class SteadyVariables extends Fragment {
	private DadsPlayer pDadsPlayer;
	private Handler pHandler;

	public void setPHandler(Handler pHandler)
	{
		this.pHandler = pHandler;
	}

	public Handler getPHandler()
	{
		return pHandler;
	}

	public void setPDadsPlayer(DadsPlayer pDadsPlayer)
	{
		this.pDadsPlayer = pDadsPlayer;
	}

	public DadsPlayer getPDadsPlayer()
	{
		return pDadsPlayer;
	}
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
		// retain this fragment
        setRetainInstance(true);
    }
}
