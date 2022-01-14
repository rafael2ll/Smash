package com.smash.up.Models;

import com.google.firebase.database.*;
import com.google.firebase.storage.*;
import com.google.android.gms.tasks.*;
import android.net.*;
import java.util.*;

@IgnoreExtraProperties
public class Album
{
	public String nome,imageUri,artistaNome,artistaKey,key;
	public long released;
	public boolean isAlbum = true;
	public Album()
	{}

	public void setIsAlbum(boolean isAlbum)
	{
		this.isAlbum = isAlbum;
	}

	public boolean isAlbum()
	{
		return isAlbum;
	}

	public void setReleased(long released)
	{
		this.released = released;
	}

	public long getReleased()
	{
		return released;
	}

	public String getImageUri()
	{
		return imageUri;
	}

	public static void getMusicas(String key, final Musica.OnFindMusicaListener listener)
	{
		Musica.getQuery().orderByChild("albumKey").equalTo(key)
			.addListenerForSingleValueEvent(new ValueEventListener(){

				@Override
				public void onDataChange(DataSnapshot p1)
				{
					for(DataSnapshot ds : p1.getChildren()){
						if(listener!=null)listener.onResult(ds.getValue(Musica.class), null);
					}
				}

				@Override
				public void onCancelled(DatabaseError p1)
				{
					if(listener!=null)listener.onResult(null, p1);
				}
			});
	}
	public void delete()
	{
		getMainRef().child(key).removeValue();
	}

	public void setImageUri(String uri){
		this.imageUri=uri;
	}
	
	public void setArtistaNome(String s){
		this.artistaNome= s;
	}
	public void setNome(String nome)
	{
		this.nome = nome;
	}

	public String getNome()
	{
		return nome;
	}

	public void setArtistaKey(String artistaKey)
	{
		this.artistaKey = artistaKey;
	}

	public String getArtistaKey()
	{
		return artistaKey;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getKey()
	{
		return key;
	}
	
	public static void update(Album a){
		getMainRef().child(a.key).setValue(a);
	
	}
	
	public static void save(Album a,DatabaseReference.CompletionListener o){
		a.key=getMainRef().push().getKey();
		getMainRef().child(a.key).setValue(a,o);
	}
	
	public static void getAlbum(String Key,ValueEventListener o){
		getMainRef().child(Key).addListenerForSingleValueEvent(o);
	}
	
	public static void getAlbumArt(String albumKey, final OnGetAlbumArtListener mListener){
		getMainRef().child(albumKey).child("imageUri").addListenerForSingleValueEvent(new ValueEventListener(){

				@Override
				public void onDataChange(DataSnapshot p1)
				{
					mListener.onResult(Uri.parse(p1.getValue(String.class)));
				}

				@Override
				public void onCancelled(DatabaseError p1)
				{
					// TODO: Implement this method
				}
			});
	}
	public static DatabaseReference getMainRef(){
		return FirebaseDatabase.getInstance().getReference().child("albuns");
	}
	public static Query getQuery(){
		return getMainRef();
	}
	public static  StorageReference getAlbumArtStorage(){
		return FirebaseStorage.getInstance().getReference().child("albumImages");
	}
	public  interface OnGetAlbumArtListener{
		public void onResult(Uri i);
	}

	@Override
	public String toString()
	{
		return nome;
	}
	
}
