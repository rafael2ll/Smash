package com.smash.up.Dialogs;
import android.support.design.widget.*;
import android.widget.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.up.R;
import com.smash.up.Models.*;
import android.view.*;
import android.os.*;
import com.mindorks.butterknifelite.*;
import com.smash.up.Helpers.*;
import android.text.*;
import android.util.*;

public class EditMusicaDialog extends BottomSheetDialogFragment 
{
	@BindView(R.id.musicaeditalbumTextInputLayout1) TextInputLayout mTilNome;
	@BindView(R.id.musicaeditalbumAutoCompleteTextView2) AutoCompleteTextView mAutoArtista;
	@BindView(R.id.musicaeditalbumAutoCompleteTextView3) AutoCompleteTextView mAutoAlbum;
	@BindView(R.id.musicaeditalbumAutoCompleteTextView4) AutoCompleteTextView mAutoArtistaParticipante;
	@BindView(R.id.musicaeditalbumImageViewSave) ImageView mSave;
	
	String TAG = "EditMusicaFrag";
	Musica mMusica;
	Artista mArtistaSelected, mArtistaPt;

	FirebaseListAdapter<Artista> mArtistaAdapter, ParticipanteAdapter;
	FirebaseListAdapter<Album> mAlbumAdapter;
	
	public EditMusicaDialog(Musica m){
		this.mMusica = m;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.musica_edit_album, container, false);
		ButterKnifeLite.bind(this, v);
		
		mAutoArtista.setThreshold(1);
		mAutoAlbum.setThreshold(1);
		mAutoArtistaParticipante.setThreshold(1);
		
		mAutoAlbum.setText(mMusica.albumNome);
		mAutoArtista.setText(mMusica.artistaNome);
		mTilNome.getEditText().setText(mMusica.nome);
		
		mAutoArtista.addTextChangedListener(new TextWatcher(){

				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TODO: Implement this method
				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					updateArtistas(p1.toString());
				}

				@Override
				public void afterTextChanged(Editable p1)
				{
					// TODO: Implement this method
				}
			});
		
		mAutoArtista.setOnItemClickListener(new AdapterView.OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					mArtistaSelected= mArtistaAdapter.getItem(p3);
					Log.w(TAG, mArtistaSelected.toString());
					updateAlbuns(mArtistaAdapter.getRef(p3).getKey());
					
				}
			});
		return v;
	}

	private void updateArtistas(String s){
		mArtistaAdapter = new FirebaseListAdapter<Artista>(getActivity(), Artista.class, R.layout.text, Artista.getQuery().orderByChild("nome").startAt(s.trim()).limitToFirst(10)){
			@Override
			protected void populateView(View view, Artista artista, int position)
			{
				((TextView)view).setText(artista.getNome());
			}
		};
		mAutoArtista.setAdapter(mArtistaAdapter);
		mAutoArtista.showDropDown();
	}
	
	private void updateAlbuns(String artistaKey){
		log(artistaKey);
		mAlbumAdapter =  new FirebaseListAdapter<Album>(getActivity(), Album.class, R.layout.text, Album.getQuery().orderByChild("artistaKey").equalTo(artistaKey)){
			@Override
			protected void populateView(View view, Album album, int position)
			{
				Log.w(TAG, album.nome);
				((TextView)view).setText(album.getNome());
			};
			};
			mAutoAlbum.setAdapter(mAlbumAdapter);
			mAutoAlbum.showDropDown();
	}

	private void log(String labrl)
	{
		Log.d(TAG, labrl);
	}
}
