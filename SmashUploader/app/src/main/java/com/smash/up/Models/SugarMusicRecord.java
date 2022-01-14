package com.smash.up.Models;

import com.orm.*;

public class SugarMusicRecord extends SugarRecord
{
	public String nome,path,albumKey,artistaKey;
	public long progressDone,max;
	public String state;
	public int duration;
	public SugarMusicRecord()
	{}

	public void setDuration(int duration)
	{
		this.duration=duration;
	}
	public int getDuration(){
		return duration;
	}
	public double getMax()
	{
		return max;
	}

	public void setSending()
	{
		state="sending";save();
	}

	public void setPaused()
	{
		state="paused";save();
	}

	public void setMax(long totalByteCount)
	{
		max=totalByteCount;
	}

	public void setError()
	{
		state="stopped";save();
	}

	public void setNome(String nome)
	{
		this.nome = nome;
	}

	public String getNome()
	{
		return nome;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public String getPath()
	{
		return path;
	}

	public void setAlbumKey(String albumKey)
	{
		this.albumKey = albumKey;
	}

	public String getAlbumKey()
	{
		return albumKey;
	}

	public void setArtistaKey(String artistaKey)
	{
		this.artistaKey = artistaKey;
	}

	public String getArtistaKey()
	{
		return artistaKey;
	}

	public void setProgressDone(long progressDone)
	{
		this.progressDone = progressDone;
	}

	public long getProgressDone()
	{
		return progressDone;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getState()
	{
		return state;
	}

	
}
