package com.smash.player.Helpers;

import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.google.firebase.*;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;
import com.smash.player.*;

import com.smash.player.R;

public class Commons
{
	public static FirebaseApp mApp;
	public static FirebaseStorage mStorageInstance;
	public static FirebaseDatabase mDBInstance;
	public static FirebaseAuth mAuthReference;
	public static StorageReference mDefaultStorageRef;
	public static DatabaseReference mDefaultDatabaseRef;
	public static View inflate(Context ctx,int res){
		return LayoutInflater.from(ctx).inflate(res,null);
	}
	public static void initializeApp(final Context ctx, final OnInitializeAppListener o){
		new AsyncTask<Void,Void,Boolean>(){

			@Override
			protected Boolean doInBackground(Void[] p1)
			{
				FirebaseOptions options= new FirebaseOptions.Builder().setApiKey("AIzaSyCa4_rD_zKF5sDW1YrwnLQapf-l9RCZiHc")
					.setApplicationId("smash-bcf6a").setDatabaseUrl("https://smash-bcf6a.firebaseio.com").setGcmSenderId("921206007104-qupv4a42eqc8jqfr8ph0nsg3bjcucg2f.apps.googleusercontent.com")
					.setStorageBucket("smash-bcf6a.appspot.com").build();
				if(FirebaseApp.getApps(ctx).isEmpty())mApp=FirebaseApp.initializeApp(ctx,options,FirebaseApp.DEFAULT_APP_NAME);
				return mApp==null ? false : true;
			}
			protected void onPostExecute(Boolean b){
				if(b){
					mStorageInstance=FirebaseStorage.getInstance(mApp);
					mDBInstance=FirebaseDatabase.getInstance(mApp);
					mAuthReference=FirebaseAuth.getInstance(mApp);

					enablePersistance();

					mDefaultDatabaseRef=mDBInstance.getReference();
					mDefaultStorageRef=mStorageInstance.getReference();
				}
				if(o!=null)o.onInitialized(b);
			}

		}.execute();
	}
	public static void enablePersistance(){
		mDBInstance.setPersistenceEnabled(true);
	}
	public static interface OnInitializeAppListener{
		public void  onInitialized(boolean b);
	}
	public static void setTaskBarColored(AppCompatActivity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            Window w = context.getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
            int statusBarHeight = Commons.getStatusBarHeight(context);

            View view = new View(context);
            view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.getLayoutParams().height = statusBarHeight;
            ((ViewGroup) w.getDecorView()).addView(view);
            view.setBackgroundColor(context.getResources().getColor(R.color.grey_900_half));
        }
    }

	public static int getActionBarHeight(AppCompatActivity a) {
		int actionBarHeight = 0;
		TypedValue tv = new TypedValue();
		if (a.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
		{
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,a.getResources().getDisplayMetrics());
		}
		return actionBarHeight;
	}

	public static int getStatusBarHeight(AppCompatActivity a) {
		int result = 0;
		int resourceId = a.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = a.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
}
