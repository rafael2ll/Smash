package com.smash.player.Models;
import android.graphics.*;
import android.content.*;
import com.orm.*;

public class Musica extends SugarRecord
{
	public String nome,path,artistaNome,albumNome;
	public String lyric;
	Bitmap albumArt;
	long albumId,artistaId;
	public Musica(){}
	public Musica(boolean isUp,Long id,String nome,String path,String album,Long aId,String artista,Long artId){
		if(!isUp){
		setNome(nome.split("-")[0]).setAlbum(album.split("/")[0])
		.setArtista(artista.split("/")[0]).setAlbumId(aId).setArtistaId(artId);
		setId(id);this.path=path;
		save();
		}else{
		}
	}

	public void setLyric(String lyric)
	{
		this.lyric = lyric;
	}

	public String getLyric()
	{
		return lyric;
	}
	public Musica setAlbumId(long albumId)
	{
		this.albumId = albumId;
		return this;
	}

	public long getAlbumId()
	{
		return albumId;
	}

	public Musica setArtistaId(long artistaId)
	{
		this.artistaId = artistaId;
		return this;
	}

	public long getArtistaId()
	{
		return artistaId;
	}

	public Bitmap getAlbumArt(Context ctx)
	{
		return Album.getAlbumArt(ctx,albumId);
	}

	public String getAlbum()
	{
		return albumNome;
	}

	public String getArtista()
	{
		return artistaNome;
	}

	public String getNome()
	{
		return nome;
	}

	public Musica setNome(String nome)
	{
		this.nome = nome;
		return this;
	}

	public Musica setArtista(String artista)
	{
		this.artistaNome = artista;
		return this;
	}

	public Musica setAlbum(String album)
	{
		this.albumNome = album;
		return this;
	}

}
