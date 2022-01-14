package com.smash.up.Dialogs;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import com.smash.up.*;
import com.smash.up.Helpers.*;
import com.smash.up.Models.*;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.*;
import com.mindorks.butterknifelite.annotations.*;

public class SimpleDialog extends BottomSheetDialogFragment
{
	Artista artista;
	Album album;
	Musica musica;
	
	@BindView(R.id.simpledialogSpinner)Spinner spinner;
	@BindView(R.id.simpledialogEditTextNome)EditText editText;
	@BindView(R.id.simpledialogCheckBoxIsSingle)CheckBox mIsSingle;
	OnPassListener mListener;
	FirebaseListAdapter<Artista> spinnerAdapter;
	public enum Type{
		ARTISTA,ALBUM,GENERO, KEY
		}
	Type type;
	View v;

	public SimpleDialog(Type b){
		type=b;
	}
	public SimpleDialog(OnPassListener o){
		type= Type.KEY;
		this.mListener=o;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		spinnerAdapter = new FirebaseListAdapter<Artista>(getActivity(), Artista.class, R.layout.text, Artista.getQuery()){
			@Override
			protected void populateView(View view, Artista artista, int position)
			{
				((TextView)view).setText(artista.getNome());
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
					if(type.equals(Type.KEY)){
						String givenKey = editText.getText().toString().toLowerCase().trim();
						String rightKey = "avestruz";
						
						mListener.onKeyResult(rightKey.equals(givenKey));
					}
					else if(type.equals(Type.ARTISTA)){
						artista=new Artista();
						artista.setImageKey("toAdd");
						artista.setNome(editText.getText().toString().trim());
						artista.save(null);
					}else if(type.equals(Type.ALBUM)){
						Album album=new Album();
						String artistaKey=spinnerAdapter.getItem(spinner.getSelectedItemPosition()).getKey();
						String artNome = spinnerAdapter.getItem(spinner.getSelectedItemPosition()).nome;
						album.setArtistaKey(artistaKey);
						album.setArtistaNome(artNome);
						album.setIsAlbum(!mIsSingle.isChecked());
						album.setImageUri("toAdd");
						album.setNome(editText.getText().toString().trim());
						album.save(album,null);
					}else if(type.equals(Type.GENERO)){
						Genero genero = new Genero(editText.getText().toString().trim());
						genero.setKey(Genero.getMainRef().push().getKey());
						genero.save(genero);
					}
				}
			});
		return builder.show();
	}

	private void finds()
	{
		com.mindorks.butterknifelite.ButterKnifeLite.bind(this, v);
		
		if(!type.equals(Type.ALBUM)){
			spinner.setVisibility(View.GONE);
			mIsSingle.setVisibility(View.GONE);
			return;
		}
		spinner.setAdapter(spinnerAdapter);
	}
	public interface OnPassListener{
		public void onKeyResult(boolean b);
	}
}
