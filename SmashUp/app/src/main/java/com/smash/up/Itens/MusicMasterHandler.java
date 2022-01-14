package com.smash.up.Itens;
import android.content.*;
import java.util.*;

public class MusicMasterHandler
{
	public static List<Musica> toPlay,myMusics,myFavorites;
	public static int toPlayPosition=0;
	public static int repeatState=0;
	public static boolean shuffleState=false;
	
	public static void setToPlayPosition(int toPlayPosition)
	{
		MusicMasterHandler.toPlayPosition = toPlayPosition;
	}
	public static int getRepeatState(Context ctx){
		repeatState=ctx.getSharedPreferences("SmashUp",ctx.MODE_PRIVATE).getInt("repeatState",0);
		return repeatState;
	}
	public static int toggleRepeatState(Context ctx){
		SharedPreferences.Editor editor=ctx.getSharedPreferences("SmashUp",ctx.MODE_PRIVATE).edit();
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
		MusicMasterHandler.toPlay = toPlay;
		if(isShuffling(ctx))Collections.shuffle(MusicMasterHandler.toPlay);
	}

	public static List<Musica> getToPlay()
	{
		return toPlay;
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
	}}
