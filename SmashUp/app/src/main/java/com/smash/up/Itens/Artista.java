package com.smash.up.Itens;
import com.google.firebase.database.*;
import com.google.android.gms.tasks.*;
import android.net.*;

@IgnoreExtraProperties
public class Artista 
{
	public String nome,imageKey,imageUri,key;
	public Artista()
	{}

	public void delete()
	{
		Commons.mDefaultDatabaseRef.child("artistas").child(key).removeValue();
	}

	public static void getAlbumArt(String imageKey, OnCompleteListener<Uri> loadListener)
	{
		Commons.mDefaultStorageRef.child("artistaImages").child(imageKey).getDownloadUrl().addOnCompleteListener(loadListener);
	}

	public void setKey(String key){
		this.key=key;
	}
	public String getKey(){
		return key;
	}
	
	public void setNome(String nome)
	{
		this.nome = nome;
	}

	public String getNome()
	{
		return nome;
	}

	public void setImageKey(String imageKey)
	{
		this.imageKey = imageKey;
	}

	public String getImageKey()
	{
		return imageKey;
	}
	public void save(DatabaseReference.CompletionListener o)
	{
		key=Commons.mDefaultDatabaseRef.child("artistas").push().getKey();
		Commons.mDefaultDatabaseRef.child("artistas").child(key).setValue(this,o);
	}
	public static void getArtista(String Key,ValueEventListener o){
		Commons.mDefaultDatabaseRef.child("artistas").child(Key).addListenerForSingleValueEvent(o);
	}
	public static DatabaseReference getMainRef(){
		return Commons.mDefaultDatabaseRef.child("artistas");
	}
	public static Query getQuery(){
		return Commons.mDefaultDatabaseRef.child("artistas");
	}
}
