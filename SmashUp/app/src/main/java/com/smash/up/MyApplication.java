package com.smash.up;
import android.app.*;
import com.osama.firecrasher.*;
import com.orm.*;
import android.content.*;
import com.smash.up.Itens.*;

public class MyApplication extends Application
{
	public static Context ctx;
	@Override
	public void onCreate()
	{
		FireCrasher.install(this);
		ctx=getApplicationContext();
		SugarContext.init(this);
		super.onCreate();
	}
	public static Context getContext(){
		return ctx;
	}
}
