package com.smash.up.Models;
import com.google.firebase.database.*;
import java.util.*;
import com.google.firebase.auth.*;

@IgnoreExtraProperties
public class User
{
	public String username;
	public User(){}
	public User(String s){
		this.username=s;
	}

	public static void addMusicas(Album album)
	{
		Album.getMusicas(album.key, new Musica.OnFindMusicaListener(){

				@Override
				public void onResult(Musica m, DatabaseError e)
				{
					if(m!=null)addMusica(m);
				}
			});
	}

	public static void addMusica(Musica mMusica)
	{
		String key= FirebaseAuth.getInstance().getCurrentUser().getUid();
		
		getUserRef(key).child("musicas").child(mMusica.key).setValue(mMusica.key);
		getUserRef(key).child("albuns").child(mMusica.albumKey).setValue(mMusica.albumKey);
		getUserRef(key).child("artistas").child(mMusica.artistaKey).setValue(mMusica.artistaKey);
	}
	public static void getMusicas(){
		getUserRef(FirebaseAuth.getInstance().getCurrentUser().getUid())
			.child("musicas").addListenerForSingleValueEvent(new ValueEventListener(){

				@Override
				public void onDataChange(DataSnapshot p1)
				{
					for(DataSnapshot ds : p1.getChildren()){
						Musica.findByKey(ds.getKey());
					}
				}

				@Override
				public void onCancelled(DatabaseError p1)
				{
					// TODO: Implement this method
				}
			});
		
	}
	
	public static boolean isFavorite(Musica get)
	{
		// TODO: Implement this method
		return false;
	}
	public static DatabaseReference getUserRef(String s){
		return FirebaseDatabase.getInstance().getReference().child("users").child(s);
	}
}
