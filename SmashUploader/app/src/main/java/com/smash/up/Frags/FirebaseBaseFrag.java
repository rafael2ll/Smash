package com.smash.up.Frags;
import com.google.firebase.database.*;
import com.smash.up.Models.*;
import android.support.v4.app.*;

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
	
	public DatabaseReference getRef (String q){
		return FirebaseDatabase.getInstance().getReference().child(q);
	}
}
