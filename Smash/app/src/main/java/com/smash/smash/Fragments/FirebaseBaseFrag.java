package com.smash.smash.Fragments;

import com.google.firebase.database.*;
import com.smash.smash.Models.*;
import android.support.v4.app.*;
import com.google.firebase.auth.*;

public class FirebaseBaseFrag extends Fragment
{
	public DatabaseReference getMusicas (){
		return Musica.getMainRef();
	}
	
	public DatabaseReference getArtistas (){
		return Artista.getMainRef();
	}
	
	public DatabaseReference getAlbuns (){
		return Album.getMainRef();
	}
	
	public DatabaseReference getUser(){
		return User.getUserRef(FirebaseAuth.getInstance().getCurrentUser().getUid());
	}
	public DatabaseReference getRef (String q){
		return FirebaseDatabase.getInstance().getReference().child(q);
	}
}
