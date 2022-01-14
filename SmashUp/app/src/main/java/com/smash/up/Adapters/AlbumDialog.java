package com.smash.up.Adapters;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.firebase.ui.database.*;
import com.google.firebase.database.*;
import com.smash.up.*;
import com.smash.up.Itens.*;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import com.smash.up.R;
import android.text.*;

public class AlbumDialog extends DialogFragment
{
	View v;Spinner albumSpinner;
	EditText text;
	String newKey;
	Album album;
	FirebaseListAdapter<Artista> adapter;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		album=new Album();
	
		super.onCreate(savedInstanceState);
		adapter = new FirebaseListAdapter<Artista>(getActivity(), Artista.class, android.R.layout.simple_dropdown_item_1line, Artista.getQuery()){
			@Override
			protected void populateView(View view, Artista artista, int position)
			{
				((TextView)view.findViewById(android.R.id.text1)).setText(artista.getNome());
			}
		};
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		v=LayoutInflater.from(getActivity()).inflate(R.layout.simple_dialog,null,false);
		finds();
		
		AlertDialog.Builder builder=new AlertDialog.Builder(getActivity())
			.setView(v).setPositiveButton("SALVAR",new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					album.setNome(text.getText().toString().trim());
					album.setImageUri("toAdd");
					album.setArtistaKey(((Artista)albumSpinner.getSelectedItem()).getKey());
					album.save(album,null);
					//MainActivity.createAlbum(key,nome);
				}
				});
				return builder.show();
		}
	private void finds()
	{
		albumSpinner=(Spinner)v.findViewById(R.id.simpledialogSpinner);
		text=(EditText)v.findViewById(R.id.simpledialogEditTextNome);
		albumSpinner.setAdapter(adapter);
		}
}
