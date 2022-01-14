package com.smash.up;

import android.app.*;
import android.content.*;
import com.orm.*;
import com.osama.firecrasher.*;
import com.smash.up.Helpers.*;

public class MyApplication extends Application
{
	public static Context ctx;
	
	@Override
	public void onCreate()
	{
		FireCrasher.install(this);
		ctx=getApplicationContext();
		FirebaseHelper.init(this);
		SugarContext.init(this);
		PicassoHelper.init(this);
		
		super.onCreate();
	}
	public static Context getContext(){
		return ctx;
	}
}
