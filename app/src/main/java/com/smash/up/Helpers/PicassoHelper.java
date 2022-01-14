package com.smash.up.Helpers;
import android.content.*;
import com.squareup.picasso.*;

public class PicassoHelper
{
	public static Picasso mPicasso;
	public static void init(Context ctx){
		mPicasso= Picasso.with(ctx);
	}
	
	public static Picasso get(){
		return mPicasso;
	}
}
