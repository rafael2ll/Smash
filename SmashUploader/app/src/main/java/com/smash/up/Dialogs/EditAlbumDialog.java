package com.smash.up.Dialogs;
import android.support.design.widget.*;
import android.support.v7.widget.*;
import android.widget.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.up.R;
import com.smash.up.Adapters.*;
import com.smash.up.Helpers.*;
import com.smash.up.Models.*;
import android.view.*;
import android.os.*;
import com.mindorks.butterknifelite.ButterKnifeLite;
import java.util.*;
import android.view.View.*;
import com.google.firebase.database.*;

public class EditAlbumDialog extends BottomSheetDialogFragment implements OnClickListener
{
	@BindView(R.id.edit_album_dialogTextInputLayoutNome) TextInputLayout mTIL;
	@BindView(R.id.edit_album_dialogTextInputLayoutYear) TextInputLayout mYearReleased;
	@BindView(R.id.editalbumdialogTextViewArtistaAtual) TextView mAtualArtista;
	@BindView(R.id.editalbumdialogSpinnerArtista) Spinner mArtistasSpinner;
	@BindView(R.id.recycler) RecyclerView mRecyclerView;
	@BindView(R.id.editalbumdialogCheckBoxIsSingle) CheckBox mIsSingle;
	@BindView(R.id.editalbumdialogImageViewSave) ImageView mSave;
	GenerosRecyclerAdapter mGenerosAdapter= new GenerosRecyclerAdapter();
	FirebaseListAdapter<Artista> mArtistaAdapter;
	EditText mText;
	Album mAlbum;
	Artista mArtistaSelected;
	
	public EditAlbumDialog(Album b){
		this.mAlbum=b;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.edit_album_dialog, container, false);
		ButterKnifeLite.bind(this, v);
		mArtistaAdapter = new FirebaseListAdapter<Artista>(getActivity(), Artista.class, R.layout.text, Artista.getQuery()){
			@Override
			protected void populateView(View view, Artista artista, int position)
			{
				mArtistasSpinner.setSelection(mArtistaAdapter.getPosition(mAlbum.artistaKey));
				((TextView)view).setText(artista.getNome());
			}
		};
		mArtistasSpinner.setAdapter(mArtistaAdapter);
		mAtualArtista.setText("Artista atual: "+mAlbum.artistaNome);
		mSave.setOnClickListener(this);
		mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));
		mRecyclerView.setAdapter(mGenerosAdapter);
		mTIL.getEditText().setText(mAlbum.nome);
		mYearReleased.getEditText().setText(String.valueOf(mAlbum.released));
		
		mArtistasSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
				{
					mArtistaSelected = mArtistaAdapter.getItem(p3);
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> p1)
				{
					// TODO: Implement this method
				}
			});
		return v;
	}

	@Override
	public void onClick(View p1)
	{
		long date =Long.parseLong( mYearReleased.getEditText().getText().toString());
		List<String> mTags = mGenerosAdapter.getCheckedItens();
		final String nome = mTIL.getEditText().getText().toString().trim();
		
		DatabaseReference mAlbumRef =Album.getMainRef().child(mAlbum.key);
		
		mAlbumRef.child("released").setValue(date);
		
		if(!mTags.isEmpty())mAlbumRef.child("generos").setValue(mTags);
		
		if(mAlbum.isAlbum() == mIsSingle.isChecked()){
			mAlbumRef.child("isAlbum").setValue(!mIsSingle.isChecked());
		}
		if(!mAlbum.nome.equals(nome)){
			mAlbumRef.child("nome").setValue(nome);
			Album.getMusicas(mAlbum.key, new Musica.OnFindMusicaListener(){

					@Override
					public void onResult(Musica m, DatabaseError e)
					{
						if(m!=null)Musica.getMainRef().child(m.key).child("albumNome").setValue(nome);
					}
				});
			}
		if(mArtistaSelected!=null){
			mAlbumRef.child("artistaKey").setValue(mArtistaSelected.key);
			mAlbumRef.child("artistaNome").setValue(mArtistaSelected.nome);
			Album.getMusicas(mAlbum.key, new Musica.OnFindMusicaListener(){

					@Override
					public void onResult(Musica m, DatabaseError e)
					{
						if(m!=null){
							Musica.getMainRef().child(m.key).child("artistaKey").setValue(mArtistaSelected.key);
							Musica.getMainRef().child(m.key).child("artistaNome").setValue(mArtistaSelected.nome);
						}
					}
				});
		}
	}

}
