package com.smash.smash.Fragments;

import android.os.*;
import android.support.v7.widget.*;
import android.view.*;
import android.view.View.*;
import com.google.firebase.database.*;
import com.smash.smash.*;
import com.smash.smash.Helpers.*;
import com.smash.smash.Holders.*;
import com.smash.smash.Models.*;
import java.util.*;

import com.smash.smash.R;

public class MyArtistasFrag extends RecyclerBaseFrag
{
	List<Artista> mArtistas = new ArrayList<>();
	EasyRecyclerAdapter<Artista, ArtistasHolder> mAdapter;

	
	public MyArtistasFrag(){}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		showFab();
		mFab.setImageResource(R.drawable.ic_shuffle_white);
		mArtistas.clear();
		User.getArtistas(new ValueEventListener(){

				@Override
				public void onDataChange(DataSnapshot p1)
				{
					Artista a = p1.getValue(Artista.class);
					if(!mArtistas.contains(a))mArtistas.add(a);
					mAdapter.notifyDataSetChanged();
				}

				@Override
				public void onCancelled(DatabaseError p1)
				{
					// TODO: Implement this method
				}
			});

		mAdapter = new EasyRecyclerAdapter<Artista, ArtistasHolder>(mArtistas, Artista.class, R.layout.artista_holder, ArtistasHolder.class){

			@Override
			protected void populateViewHolder(ArtistasHolder viewHolder, Artista model, int position)
			{
				hideLoading();
				viewHolder.feedIt(model, new OnClickListener(){

						@Override
						public void onClick(View p1)
						{
							// TODO: Implement this method
						}
					});
			}
		};
		setRecyclerAdapter(new GridLayoutManager(getActivity(), 2), mAdapter);
	}
}
