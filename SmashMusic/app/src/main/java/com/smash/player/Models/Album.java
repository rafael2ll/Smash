package com.smash.player.Models;

import android.content.*;
import android.database.*;
import android.graphics.*;
import android.net.*;
import android.provider.*;
import com.smash.player.*;
import java.io.*;
import android.util.*;
import com.orm.*;

public class Album extends SugarRecord
{
	
	public static String getCoverArtPath(long albumId,Context context) {
		Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
		Uri uri =ContentUris.withAppendedId(sArtworkUri, albumId);
		Log.w("Files",uri.getPath());
		return uri.toString();
	}
	public static Bitmap getAlbumArt(Context context,Long id){
		Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
		Uri uri =ContentUris.withAppendedId(sArtworkUri, id);
		ContentResolver res = context.getContentResolver();
		try
		{
			Log.w("Bitmaps",uri.toString());
			InputStream in = res.openInputStream(uri);
			Bitmap artwork =BitmapFactory.decodeStream(in);
			return artwork;
		}
		catch (FileNotFoundException e)
		{
			return BitmapFactory.decodeResource(context.getResources(),R.drawable.image_back);
		}
	}}
