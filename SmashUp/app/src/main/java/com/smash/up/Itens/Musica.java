package com.smash.up.Itens;
import android.net.*;
import com.google.firebase.database.*;
import com.google.thirdparty.publicsuffix.*;

@IgnoreExtraProperties
public class Musica
{
	public String nome,artistaNome,albumNome;
	public String albumKey,key,musicaLink,musicaUri;
	public int duration;
	public String artistaKey;
	public Perfil f;
	public Musica()
	{}

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

	public void delete()
	{
		Commons.mDefaultStorageRef.child("musicas").child(musicaLink).delete();
		Commons.mDefaultDatabaseRef.child("musicas").child(key)
		.removeValue();
		
	}
	
	public Musica createFromSugar(SugarMusicRecord record,ValueEventListener listener)
	{
		setNome(record.nome);
		setDuration(record.duration);
		Commons.mDefaultDatabaseRef.child("albuns").child(record.albumKey)
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
		return Commons.mDefaultDatabaseRef.child("musicas");
	}
	public static Query getQuery(){
		return Commons.mDefaultDatabaseRef.child("musicas");
	}
}
