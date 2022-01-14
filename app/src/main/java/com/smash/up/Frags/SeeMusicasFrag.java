package com.smash.up.Frags;

import android.content.*;
import android.os.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import com.google.firebase.database.*;
import com.smash.up.*;
import com.smash.up.Helpers.*;
import com.smash.up.Holders.*;
import com.smash.up.Models.*;
import java.util.*;

import com.smash.up.R;
import android.support.design.widget.*;
import android.view.View.*;
import com.smash.up.Dialogs.*;

public class SeeMusicasFrag extends RecyclerBaseFrag
{
	FirebaseRecyclerAdapter adapter;
	Query query;
	Context ctx;
	FirebaseRecyclerAdapter albumAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		query= Musica.getQuery();
		ctx=getActivity();
		albumAdapter = new FirebaseRecyclerAdapter<Album,CircleAlbumHolder>(Album.class, R.layout.album_item, CircleAlbumHolder.class, Album.getQuery()){
			@Override
			protected void populateViewHolder(final CircleAlbumHolder p1, Album p2, int p3)
			{
				Log.w("Adapter",p2.getNome());
				p1.populateHolder(getActivity(), p2, new CircleAlbumHolder.OnAlbumClick(){

						@Override
						public void onLongClick(Album album)
						{
							new EditAlbumDialog(album).show(getChildFragmentManager(),"");
						}

						@Override
						public void onClick(Album a)
						{
							query=Musica.getQuery().orderByChild("albumKey").equalTo(a.key);
							updateQuery();
						}
					});
			}
		};
	}

	public void updateQuery(){
		showLoading();
		adapter = new FirebaseRecyclerAdapter<Musica, MusicaHolder>(Musica.class, R.layout.musicas_holder, MusicaHolder.class, query){

			@Override
			protected void populateViewHolder(MusicaHolder p1, Musica p2, int p3)
			{

				Log.w("Adapter",p2.getNome());
				hideLoading();
				p1.feedIt(p2, new MusicaHolder.OnMusicaClickListener(){

						@Override
						public void onDeleteClick(final Musica m)
						{
							Snackbar.make(mHorizontalRecycler,"Deseja deletar "+m.nome+ " de "+m.albumNome,Snackbar.LENGTH_LONG)
								.setAction("SIM", new OnClickListener(){

									@Override
									public void onClick(View p1)
									{
										Musica.delete(m);
									}
								}).show();
						}

						@Override
						public void onClick(Musica m)
						{
							new EditMusicaDialog(m).show(getChildFragmentManager(),"");
						}
					});
			}
		};
		mRecycler.setAdapter(adapter);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		updateQuery();
		showHorizontalandSetAdapter(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false), albumAdapter);
		setRecyclerAdapter(new LinearLayoutManager(getActivity()), adapter);
	}
}
