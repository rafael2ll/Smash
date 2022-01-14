package com.smash.smash.Fragments;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import com.smash.smash.*;
import com.smash.smash.Helpers.*;
import com.smash.smash.Holders.*;
import com.smash.smash.Models.*;
import android.support.v7.widget.*;

public class ArtistaAlbunsFrag extends RecyclerBaseFrag
{
	FirebaseRecyclerAdapter mAdapter;
	Artista mArtista;
	
	public ArtistaAlbunsFrag(Artista a){
		this.mArtista=a;
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		mAdapter = new FirebaseRecyclerAdapter<Album, AlbunsHolder>(Album.class, R.layout.album_holder, AlbunsHolder.class, Album.getQuery().orderByChild("artistaKey").equalTo(mArtista.key)){

			@Override
			protected void populateViewHolder(AlbunsHolder viewHolder, final Album model, int position)
			{
				hideLoading();
				viewHolder.feedIt(model, true, new OnClickListener(){

						@Override
						public void onClick(View p1)
						{
							Intent i = new Intent(getActivity(), AlbumActivity.class);
							i.putExtra(AlbumActivity.ALBUM_KEY, model.key);
							startActivity(i);
						}
				});
			}
		};
		setRecyclerAdapter(new GridLayoutManager(getActivity(), 2), mAdapter);
	}
}
