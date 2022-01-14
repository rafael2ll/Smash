package com.smash.up.Adapters;

import android.app.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.view.*;
import com.smash.up.R;
import com.smash.up.Itens.*;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.*;
import android.content.*;
import com.firebase.ui.database.*;
import com.google.firebase.database.*;
import android.util.*;

public class SimpleDialog extends DialogFragment
{
	Artista artista;
	Album album;
	Musica musica;
	Spinner spinner;
	EditText editText;
	FirebaseListAdapter<Artista> spinnerAdapter;
	public enum Type{
		ARTISTA,ALBUM,EDIT_ALBUM,EDIT_MUSICA,EDIT_ARTISTA
	}
	Type type;
	View v;
	
	public SimpleDialog(Type b){
		type=b;
	}
	public SimpleDialog(Album a){
		type=Type.EDIT_ALBUM;
		album=a;
	}
	public SimpleDialog(Artista a){
		type=Type.EDIT_ARTISTA;
		artista=a;
	}
	public SimpleDialog(Musica a){
		type=Type.EDIT_MUSICA;
		musica=a;
	}
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		spinnerAdapter = new FirebaseListAdapter<Artista>(getActivity(), Artista.class, R.layout.text, Artista.getQuery()){
			@Override
			protected void populateView(View view, Artista artista, int position)
			{
				((TextView)view.findViewById(R.id.textTextView)).setText(artista.getNome());
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
					if(type.equals(Type.ARTISTA)){
						artista=new Artista();
						artista.setImageKey("toAdd");
						artista.setNome(editText.getText().toString().trim());
						artista.save(null);
					}else if(type.equals(Type.ALBUM)){
						Album album=new Album();
						String artistaKey=spinnerAdapter.getItem(spinner.getSelectedItemPosition()).getKey();
						album.setArtistaKey(artistaKey);
						album.setImageUri("toAdd");
						album.setNome(editText.getText().toString().trim());
						album.save(album,null);
					}else if(type.equals(Type.EDIT_ALBUM)){
						Album.getMainRef().child(album.key).child("nome").setValue(editText.getText().toString().trim());
					}else if(type.equals(Type.EDIT_ARTISTA)){
						Artista.getMainRef().child(artista.key).child("nome").setValue(editText.getText().toString().trim());
					}else if(type.equals(Type.EDIT_MUSICA)){
						Musica.getMainRef().child(musica.key).child("nome").setValue(editText.getText().toString().trim());
					}
			}
			});
		return builder.show();
	}

	private void finds()
	{
		spinner=(Spinner)v.findViewById(R.id.simpledialogSpinner);
		editText=(EditText)v.findViewById(R.id.simpledialogEditTextNome);
		if(!type.equals(Type.ALBUM)){
			spinner.setVisibility(View.GONE);
			return;
		}
		spinner.setAdapter(spinnerAdapter);
	}
	
}
