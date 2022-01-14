package com.smash.up.Models;
import android.net.*;
import com.google.firebase.database.*;
import com.google.thirdparty.publicsuffix.*;
import com.google.firebase.storage.*;
import java.util.*;

@IgnoreExtraProperties
public class Musica
{
	public String nome,artistaNome,albumNome;
	public String albumKey,key,musicaLink,musicaUri;
	public int duration;
	public Date sentDate;
	public String artistaKey;

	public Musica()
	{}

	public void setSentData(Date date)
	{
		this.sentDate= date;
	}

	public static void findByKey(String key)
	{
		
	}

	public String getMusicaUri()
	{
		return musicaUri;
	}


	public void setMusicaUri(String musicaUri)
	{
		this.musicaUri=musicaUri;
	}

	public void setDuration(int duration)
	{
		this.duration = duration;
	}

	public int getDuration()
	{
		return duration;
	}

	public static void delete(Musica m)
	{
		getMusicasStorage().child(m.musicaLink).delete();
		getMainRef().child(m.key)
		.removeValue();
		
	}
	
	public Musica createFromSugar(SugarMusicRecord record,ValueEventListener listener)
	{
		setNome(record.nome);
		setDuration(record.duration);
		Album.getMainRef().child(record.albumKey)
			.addListenerForSingleValueEvent(listener);
		return this;
	}
	
	public void setNome(String nome)
	{
		this.nome = nome;
	}

	public String getNome()
	{
		return nome;
	}

	public void setArtistaNome(String artistaNome)
	{
		this.artistaNome = artistaNome;
	}

	public String getArtistaNome()
	{
		return artistaNome;
	}

	public void setAlbumNome(String albumNome)
	{
		this.albumNome = albumNome;
	}

	public String getAlbumNome()
	{
		return albumNome;
	}

	public void setAlbumKey(String albumKey)
	{
		this.albumKey = albumKey;
	}

	public String getAlbumKey()
	{
		return albumKey;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getKey()
	{
		return key;
	}

	public void setMusicaKey(String key)
	{
		this.musicaLink = key;
	}

	public String getMusicaKey()
	{
		return musicaLink;
	}

	public void setArtistaKey(String artistaKey)
	{
		this.artistaKey = artistaKey;
	}

	public String getArtistaKey()
	{
		return artistaKey;
	}
	
	public static DatabaseReference getMainRef(){
		return FirebaseDatabase.getInstance().getReference().child("musicas");
	}
	
	public static Query getQuery(){
		return getMainRef();
	}
	
	public static StorageReference getMusicasStorage(){
		return FirebaseStorage.getInstance().getReference().child("musicas");
	}
	public interface OnFindMusicaListener{
		public void onResult(Musica m, DatabaseError e);
	}
}
