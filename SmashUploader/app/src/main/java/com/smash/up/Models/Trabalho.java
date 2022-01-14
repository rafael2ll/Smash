package com.smash.up.Models;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;

@IgnoreExtraProperties()
public class Trabalho
{
	public String nome, fileUri, fileKey, serie;
	public String key;
	public long lenght = -1;
	public String shortLink;
	public long date;
	
	public Trabalho()
	{}

	public void setLenght(long lenght)
	{
		this.lenght = lenght;
	}

	public long getLenght()
	{
		return lenght;
	}

	public void setShortLink(String shortKey)
	{
		this.shortLink = shortKey;
	}

	public String getShortKey()
	{
		return shortLink;
	}

	public static void save(Trabalho t)
	{
		getRef().child(t.key).setValue(t);
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getKey()
	{
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

	public void setFileUri(String fileUri)
	{
		this.fileUri = fileUri;
	}

	public String getFileUri()
	{
		return fileUri;
	}

	public void setFileKey(String fileKey)
	{
		this.fileKey = fileKey;
	}

	public String getFileKey()
	{
		return fileKey;
	}

	public void setSerie(String serie)
	{
		this.serie = serie;
	}

	public String getSerie()
	{
		return serie;
	}

	public void setDate(long date)
	{
		this.date = date;
	}

	public long getDate()
	{
		return date;
	}
	
	public static StorageReference getStorageRef(){
		return FirebaseStorage.getInstance().getReference().child("trabalhos");
	}
	public static DatabaseReference getRef(){
		return FirebaseDatabase.getInstance().getReference().child("trabalhos");
	}
}
