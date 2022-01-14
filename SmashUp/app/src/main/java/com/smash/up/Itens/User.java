package com.smash.up.Itens;
import com.google.firebase.database.*;
import java.util.*;

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
		Album.getMusicas(album.key,new ValueEventListener(){

				@Override
				public void onDataChange(DataSnapshot p1)
				{
					for(DataSnapshot ds : p1.getChildren()){
						addMusica(ds.getValue(Musica.class));
					}
				}

				@Override
				public void onCancelled(DatabaseError p1)
				{
					// TODO: Implement this method
				}
			});
	}

	public static void addMusica(Musica mMusica)
	{
		Commons.mDefaultDatabaseRef.child("users").child(Commons.mAuthReference.getCurrentUser().getUid())
		.child("musicas").child(mMusica.key).setValue(mMusica.key);
		Commons.mDefaultDatabaseRef.child("users").child(Commons.mAuthReference.getCurrentUser().getUid())
		.child("albuns").child(mMusica.albumKey).setValue(mMusica.albumKey);
		Commons.mDefaultDatabaseRef.child("users").child(Commons.mAuthReference.getCurrentUser().getUid())
		.child("artistas").child(mMusica.artistaKey).setValue(mMusica.artistaKey);
	}
	public static void getMusicas(){
		Commons.mDefaultDatabaseRef.child("users").child(Commons.mAuthReference.getCurrentUser().getUid())
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
}
