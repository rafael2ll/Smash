package com.smash.player.Helpers;
import android.os.*;

public abstract class SmashAsync<T> extends AsyncTask
{

	@Override
	protected T doInBackground(Object[] p1)
	{
		
		return onPrepare();
	}

	@Override
	protected void onPostExecute(T result)
	{
		super.onPostExecute(result);
		onResult(result);
	}
	
	
	public abstract T onPrepare();
	public abstract void onResult(T result);
}
