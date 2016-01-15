package com.captncript.dadsRadio;
import android.app.*;
import android.os.*;
import android.content.*;

public class SteadyVariables extends Fragment {
	private DadsPlayer pDadsPlayer;
	private Handler pHandler;
	private ServiceConnection pConnection;
    private String output;

    public void setOutput(String output)
    {
        this.output = output;
    }

    public String getOutput()
    {
        return output;
    }

	public void setPConnection(ServiceConnection pConnection)
	{
		this.pConnection = pConnection;
	}

	public ServiceConnection getPConnection()
	{
		return pConnection;
	}

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
