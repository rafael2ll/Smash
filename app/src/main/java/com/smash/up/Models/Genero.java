package com.smash.up.Models;
import java.util.*;
import com.google.firebase.database.*;

public class Genero
{
	public String key,nome;
	public List<String> related;
	
	public Genero(){}
	
	public Genero(String s){
		this.nome=s;
	}

	public static void save(Genero genero)
	{
		getMainRef().child(genero.key).setValue(genero);
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

	public void setRelated(List<String> related)
	{
		this.related = related;
	}

	public List<String> getRelated()
	{
		return related;
	}
	
	public static DatabaseReference getMainRef(){
		return FirebaseDatabase.getInstance().getReference().child("generos");
	}
	
	public static DatabaseReference getGenero(String s){
		return getMainRef().child(s);
	}
}
