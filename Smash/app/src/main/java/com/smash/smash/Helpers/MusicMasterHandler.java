package com.smash.smash.Helpers;

import android.content.*;
import com.google.common.reflect.*;
import com.google.gson.*;
import com.smash.smash.Models.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import android.support.v4.app.*;

public class MusicMasterHandler
{
	public static List<Musica> toPlay,myMusics,myFavorites;
	public static int toPlayPosition=-1;
	public static int repeatState=0;
	public static boolean shuffleState=false;
	public static SharedPreferences preferences;
	public static String STORAGE="StoragePreferences";

	public static void addToNext(Musica mMusica)
	{
		toPlay.add(getPlayingIndex()+1, mMusica);
	}

	public static int getPlayingIndex()
	{
		// TODO: Implement this method
		return toPlay.indexOf(getPlaying());
	}

	public static Musica getPlaying()
	{
		try{
		return MusicService.getItself().getPlayingMusica();
		}catch(Exception e){
			return null;
		}
	}

	public static boolean isOffline(Context ctx,Musica musica)
	{
		if(loadMusicaOffline(ctx)==null) return false;
		if(loadMusicaOffline(ctx).contains(musica.key)){
			File f= new File(ctx.getFilesDir(),musica.key);
			if(!f.exists()){
				removeMusicaOffline(ctx, musica.key);
			}
			return f.exists();
		}else return false;
	}

	public static void removeMusicaOffline(Context ctx, String musica)
	{
		List<String> offlines = loadMusicaOffline(ctx);
		if(offlines.contains(musica)) offlines.remove(musica);
		preferences = ctx.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(offlines);
        editor.putString("MusicaArrayListOffline", json);
        editor.apply();
		
	}
	public static void shuffleToPlay()
	{
		Collections.shuffle(toPlay);
	}

	public static void setToPlayPosition(int toPlayPosition)
	{
		MusicMasterHandler.toPlayPosition = toPlayPosition;
	}
	public static int getRepeatState(Context ctx){
		repeatState=ctx.getSharedPreferences("Smash",ctx.MODE_PRIVATE).getInt("repeatState",0);
		return repeatState;
	}
	public static int toggleRepeatState(Context ctx){
		SharedPreferences.Editor editor=ctx.getSharedPreferences("Smash",ctx.MODE_PRIVATE).edit();
		switch(getRepeatState(ctx)){
			case 0:repeatState=1;break;
			case 1:repeatState=2;break;
			case 2:repeatState=0;
		}
		editor.putInt("repeatState",repeatState).commit();
		return repeatState;
	}
	public static boolean isShuffling(Context ctx){
		shuffleState=ctx.getSharedPreferences("Smash",ctx.MODE_PRIVATE).getBoolean("shuffleState",false);
		return shuffleState;
	}
	public static boolean toggleShuffling(Context ctx){
		shuffleState=!isShuffling(ctx);
		ctx.getSharedPreferences("Smash",ctx.MODE_PRIVATE)
			.edit().putBoolean("shuffleState",shuffleState).commit();
		return shuffleState;
	}
	public static int getToPlayPosition(Context ctx)
	{
		return toPlayPosition == -1 ? loadMusicaIndex(ctx) : toPlayPosition;
	}


	public static void setToPlay(Context ctx,List<Musica> toPlay, boolean isShuffling)
	{
		MusicMasterHandler.toPlay = new ArrayList<>();
		MusicMasterHandler.toPlay.addAll(toPlay);
		if(isShuffling(ctx) && isShuffling) Collections.shuffle(MusicMasterHandler.toPlay);
		storeMusica(ctx, toPlay);
	}
    public static void storeMusica(Context context, List<Musica> arrayList) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("MusicaArrayList", json);
        editor.apply();
    }

	public static void storeMusicaOffline(Context context, String key) {
		List<String> arrayList = loadMusicaOffline(context);
		if(arrayList==null) arrayList= new ArrayList<>();
		arrayList.add(key);
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("MusicaArrayListOffline", json);
        editor.apply();
    }
	public static ArrayList<String> loadMusicaOffline(Context context) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("MusicaArrayListOffline", null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }
    public static ArrayList<Musica> loadMusica(Context context) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("MusicaArrayList", null);
        Type type = new TypeToken<ArrayList<Musica>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static void storeMusicaIndex(Context context,int index) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("MusicaIndex", index);
        editor.apply();
    }

    public static int loadMusicaIndex(Context context) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getInt("MusicaIndex", -1);//return -1 if no data found
    }

    public static void clearCachedMusicaPlaylist(Context context) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

	public static List<Musica> getToPlay(Context ctx)
	{
		return toPlay== null ? loadMusica(ctx) : toPlay;
	}

	public static void setMyMusics(List<Musica> myMusics)
	{
		MusicMasterHandler.myMusics = myMusics;
	}

	public static List<Musica> getMyMusics()
	{
		return myMusics;
	}

	public static void setMyFavorites(List<Musica> myFavorites)
	{
		MusicMasterHandler.myFavorites = myFavorites;
	}

	public static List<Musica> getMyFavorites()
	{
		return myFavorites;
	}
}
