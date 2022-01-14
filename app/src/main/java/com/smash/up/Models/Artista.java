package com.smash.up.Models;
import com.google.firebase.database.*;
import com.google.android.gms.tasks.*;
import android.net.*;
import com.google.firebase.storage.*;

@IgnoreExtraProperties
public class Artista 
{
	public String nome,imageKey,imageUri,key;
	public Artista()
	{}

	public void delete()
	{
		getMainRef().child(key).removeValue();
	}

	public static void getAlbumArt(String imageKey, OnCompleteListener<Uri> loadListener)
	{
		getArtistaImageStorage().child(imageKey).getDownloadUrl().addOnCompleteListener(loadListener);
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
		key=getMainRef().push().getKey();
		getMainRef().child(key).setValue(this,o);
	}
	
	public static void getArtista(String Key,ValueEventListener o){
		getMainRef().child(Key).addListenerForSingleValueEvent(o);
	}

	@Override
	public String toString()
	{
		return nome;
	}
	
	public static DatabaseReference getMainRef(){
		return FirebaseDatabase.getInstance().getReference().child("artistas");
	}
	
	public static Query getQuery(){
		return getMainRef();
	}
	
	public static StorageReference getArtistaImageStorage(){
		return FirebaseStorage.getInstance().getReference().child("artistaImages");
	}
}
