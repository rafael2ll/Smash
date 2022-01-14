package com.smash.up.Helpers;
import com.google.firebase.*;
import com.smash.up.*;
import android.content.*;

public class FirebaseHelper
{


	public static void init(Context ctx)
	{
		FirebaseOptions options= new FirebaseOptions.Builder().setApiKey("AIzaSyCa4_rD_zKF5sDW1YrwnLQapf-l9RCZiHc")
			.setApplicationId("smash-bcf6a").setDatabaseUrl("https://smash-bcf6a.firebaseio.com").setGcmSenderId("921206007104-qupv4a42eqc8jqfr8ph0nsg3bjcucg2f.apps.googleusercontent.com")
			.setStorageBucket("smash-bcf6a.appspot.com").build();
		if(FirebaseApp.getApps(ctx).isEmpty()) FirebaseApp.initializeApp(ctx,options,FirebaseApp.DEFAULT_APP_NAME);
		
	}
	}
