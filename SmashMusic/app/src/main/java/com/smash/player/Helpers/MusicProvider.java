package com.smash.player.Helpers;

import android.content.*;
import android.database.*;
import android.net.*;
import com.smash.player.Models.*;
import java.util.*;

public class MusicProvider
{
	public static List<Musica> allMusics= new ArrayList<>();
	public static List<Album> allAlbuns= new ArrayList<>();
	public static List<Artista> allArtistas= new ArrayList<>();
	
	public static void updateSongList(Context ctx,boolean reScan){
	
		if(reScan){
		ContentResolver musicResolver = ctx.getContentResolver();
		Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
		//iterate over results if valid
		if(musicCursor!=null && musicCursor.moveToFirst()){
			//get columns
			int titleColumn = musicCursor.getColumnIndex
			(android.provider.MediaStore.Audio.Media.TITLE);
			int dataColumn= musicCursor.getColumnIndex(
			android.provider.MediaStore.Audio.Media.DATA);
			int idColumn = musicCursor.getColumnIndex
			(android.provider.MediaStore.Audio.Media._ID);
			int artistColumn = musicCursor.getColumnIndex
			(android.provider.MediaStore.Audio.Media.ARTIST);
			int albumColumn =musicCursor.getColumnIndex
			(android.provider.MediaStore.Audio.Media.ALBUM);
			int albumIdColumn=musicCursor.getColumnIndex
			(android.provider.MediaStore.Audio.Media.ALBUM_ID);
			int artistaIdColumn=musicCursor.getColumnIndex
			(android.provider.MediaStore.Audio.Media.ARTIST_ID);
			
			//add songs to list
			do {
				long thisId = musicCursor.getLong(idColumn);
				long albumId= musicCursor.getLong(albumIdColumn);
				long artistaId= musicCursor.getLong(artistaIdColumn);
				String path= musicCursor.getString(dataColumn);
				String thisTitle = musicCursor.getString(titleColumn);
				String thisArtist = musicCursor.getString(artistColumn);
				String thisAlbum = musicCursor.getString(albumColumn);
				new Musica(false,thisId, thisTitle,path, thisAlbum,albumId, thisArtist,artistaId);
				new Artista(artistaId,thisArtist);
			} 
			while (musicCursor.moveToNext());
		}
		}
		allMusics=Musica.listAll(Musica.class);
		allArtistas=Artista.listAll(Artista.class);
	}
	public static List<Musica> toPlay,myMusics,myFavorites;
	public static int toPlayPosition=0;
	public static int repeatState=0;
	public static boolean shuffleState=false;

	public static void setToPlayPosition(int toPlayPosition)
	{
		MusicProvider.toPlayPosition = toPlayPosition;
	}
	public static int getRepeatState(Context ctx){
		repeatState=ctx.getSharedPreferences("SmashPlayer",ctx.MODE_PRIVATE).getInt("repeatState",0);
		return repeatState;
	}
	public static int toggleRepeatState(Context ctx){
		SharedPreferences.Editor editor=ctx.getSharedPreferences("SmashPlayer",ctx.MODE_PRIVATE).edit();
		switch(getRepeatState(ctx)){
			case 0:repeatState=1;break;
			case 1:repeatState=2;break;
			case 2:repeatState=0;
		}
		editor.putInt("repeatState",repeatState).commit();
		return repeatState;
	}
	public static boolean isShuffling(Context ctx){
		shuffleState=ctx.getSharedPreferences("SmashUp",ctx.MODE_PRIVATE).getBoolean("shuffleState",false);
		return shuffleState;
	}
	public static boolean toggleShuffling(Context ctx){
		shuffleState=!isShuffling(ctx);
		ctx.getSharedPreferences("SmashUp",ctx.MODE_PRIVATE)
			.edit().putBoolean("shuffleState",shuffleState).commit();
		return shuffleState;
	}
	public static int getToPlayPosition()
	{
		return toPlayPosition;
	}


	public static void setToPlay(Context ctx,List<Musica> toPlay)
	{
		MusicProvider.toPlay = toPlay;
		if(isShuffling(ctx))Collections.shuffle(MusicProvider.toPlay);
	}

	public static List<Musica> getToPlay()
	{
		return toPlay;
	}

	public static void setMyMusics(List<Musica> myMusics)
	{
		MusicProvider.myMusics = myMusics;
	}

	public static List<Musica> getMyMusics()
	{
		return myMusics;
	}

	public static void setMyFavorites(List<Musica> myFavorites)
	{
		MusicProvider.myFavorites = myFavorites;
	}

	public static List<Musica> getMyFavorites()
	{
		return myFavorites;
	}
}
