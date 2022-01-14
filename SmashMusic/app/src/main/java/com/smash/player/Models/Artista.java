package com.smash.player.Models;
import com.orm.*;

public class Artista extends SugarRecord
{
	public String name;
	public Artista()
	{}
	public Artista(Long id,String name){
		setId(id);setName(name);save();
	}
	

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
}
