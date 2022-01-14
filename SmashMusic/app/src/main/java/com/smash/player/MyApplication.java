package com.smash.player;
import android.app.*;
import com.orm.*;
import com.osama.firecrasher.*;

public class MyApplication extends Application
{

	@Override
	public void onCreate()
	{
		SugarContext.init(this);
		FireCrasher.install(this);
		super.onCreate();
	}
	
}
