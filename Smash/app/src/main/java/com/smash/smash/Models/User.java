package com.smash.smash.Models;
import com.google.firebase.database.*;
import java.util.*;
import com.google.firebase.auth.*;
import android.util.*;

@IgnoreExtraProperties
public class User
{
	public String username;
	public String email;
	
	public User(){}
	
	public User(String s){
		this.username=s;
	}

	public static void setFavorite(Musica mMusica)
	{
		// TODO: Implement this method
	}

	public static void containsMusica(final String key, final OnContainsListener listener)
	{
		getMe().child("musicas").addListenerForSingleValueEvent(new ValueEventListener(){

				@Override
				public void onDataChange(DataSnapshot p1)
				{
					for(DataSnapshot ds : p1.getChildren()){
						Log.w("User",ds.getKey());
						if(key.equals(ds.getKey())){
							listener.isContained(true);
							return;
						}
					}
					listener.isContained(false);
				}

				@Override
				public void onCancelled(DatabaseError p1)
				{
					// TODO: Implement this method
				}
			});
		
	}

	public static void getAlbuns(final ValueEventListener o){
		getMe().child("albuns").addValueEventListener(new ValueEventListener(){

				@Override
				public void onDataChange(DataSnapshot p1)
				{
					for(DataSnapshot ds : p1.getChildren()){
						Album.getAlbum(ds.getKey(), o);
					}
				}

				@Override
				public void onCancelled(DatabaseError p1)
				{
					// TODO: Implement this method
				}
			});
	}
	public static void getArtistas(final ValueEventListener o){
		getMe().child("artistas").addValueEventListener(new ValueEventListener(){

				@Override
				public void onDataChange(DataSnapshot p1)
				{
					for(DataSnapshot ds : p1.getChildren()){
						Artista.getArtista(ds.getKey(), o);
					}
				}

				@Override
				public void onCancelled(DatabaseError p1)
				{
					// TODO: Implement this method
				}
			});
	}
	public static void addMusicas(Album album)
	{
		Album.getMusicas(album.key, new Musica.OnFindMusicaListener(){

				@Override
				public void onResult(Musica m, DatabaseError e)
				{
					addMusica(m);
				}
			});
	}

	public static void addMusica(Musica mMusica)
	{
		String key= FirebaseAuth.getInstance().getCurrentUser().getUid();
		Log.d("User","Add musica:"+mMusica.nome + " × " +mMusica.key);
		getUserRef(key).child("musicas").child(mMusica.key).setValue(mMusica.key);
		getUserRef(key).child("albuns").child(mMusica.albumKey).setValue(mMusica.albumKey);
		getUserRef(key).child("artistas").child(mMusica.artistaKey).setValue(mMusica.artistaKey);
	}
	public static void removeMusica(String key){
		getMe().child("musicas").child(key).removeValue();
	}
	public static void getMusicas(final OnGetMyMusicsListener o){
		
		getUserRef(FirebaseAuth.getInstance().getCurrentUser().getUid())
			.child("musicas").addValueEventListener(new ValueEventListener(){

				@Override
				public void onDataChange(DataSnapshot p1)
				{
					List<String> mList = new ArrayList<>();
					for(DataSnapshot ds : p1.getChildren()){
						mList.add(ds.getKey());
					}
					o.onGot(mList);
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
	private static DatabaseReference getMe(){
		return getUserRef(FirebaseAuth.getInstance().getCurrentUser().getUid());
	}
	public static DatabaseReference getUserRef(String s){
		return FirebaseDatabase.getInstance().getReference().child("users").child(s);
	}
	public interface OnGetMyMusicsListener{
		public void onGot(List<String> mMusicasHash);
	}
	public interface OnContainsListener{
		public void isContained(boolean b);
	}

	}
