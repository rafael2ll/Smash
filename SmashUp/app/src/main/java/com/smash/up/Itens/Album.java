package com.smash.up.Itens;

import com.google.firebase.database.*;
import com.google.firebase.storage.*;
import com.google.android.gms.tasks.*;
import android.net.*;

@IgnoreExtraProperties
public class Album
{
	public String nome,imageUri,artistaKey,key;
	public Album()
	{}

	public String getImageUri()
	{
		return imageUri;
	}

	public static void getMusicas(String key, ValueEventListener listener)
	{
		Musica.getQuery().orderByChild("albumKey").equalTo(key)
			.addListenerForSingleValueEvent(listener);
	}

	public void delete()
	{
		Commons.mDefaultDatabaseRef.child("albuns").child(key).removeValue();
	}

	public void setImageUri(String uri){
		this.imageUri=uri;
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
		Commons.mDefaultDatabaseRef.child("albuns").child(a.key).setValue(a);
	
	}
	public static void save(Album a,DatabaseReference.CompletionListener o){
		a.key=Commons.mDefaultDatabaseRef.child("albuns").push().getKey();
		Commons.mDefaultDatabaseRef.child("albuns").child(a.key).setValue(a,o);
	}
	public static void getAlbum(String Key,ValueEventListener o){
		Commons.mDefaultDatabaseRef.child("albuns").child(Key).addListenerForSingleValueEvent(o);
	}
	public static void getAlbumArt(String imageKey,OnCompleteListener<Uri> o)
	{
		Commons.mDefaultStorageRef.child("albumImages").child(imageKey)
		.getDownloadUrl().addOnCompleteListener(o);
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
		return Commons.mDefaultDatabaseRef.child("albuns");
	}
	public static Query getQuery(){
		return Commons.mDefaultDatabaseRef.child("albuns");
	}
	public  interface OnGetAlbumArtListener{
		public void onResult(Uri i);
	}
}
