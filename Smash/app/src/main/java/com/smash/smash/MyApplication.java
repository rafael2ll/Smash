package com.smash.smash;
import android.app.*;
import com.google.firebase.analytics.*;
import com.google.firebase.database.*;
import com.orm.*;
import com.osama.firecrasher.*;
import com.smash.smash.Helpers.*;
import com.smash.smash.Models.*;
import android.content.*;

public class MyApplication extends Application
{

	private static Context ctx;
	
	public static Context getContext()
	{
		return ctx;
	}

	@Override
	public void onCreate()
	{
		FireCrasher.install(this);
		ctx= this;
		SugarContext.init(this);
		PicassoHelper.init(this);
		FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true);
		FirebaseDatabase.getInstance().setPersistenceEnabled(true);
		Musica.getMainRef().keepSynced(true);
		Album.getMainRef().keepSynced(true);
		Artista.getMainRef().keepSynced(true);
		
		super.onCreate();
	}
}
