package com.smash.smash.Fragments;
import android.content.*;
import android.os.*;
import android.view.*;
import com.smash.smash.*;
import com.smash.smash.Helpers.*;
import com.smash.smash.Holders.*;
import com.smash.smash.Models.*;
import java.util.*;
import android.support.v7.widget.*;

public class ArtistaTopMusicas extends RecyclerBaseFrag
{
	FirebaseRecyclerAdapter<Musica, MusicasHolder> mAdapter;
	Artista mArtista;
	
	public ArtistaTopMusicas(Artista m){
		this.mArtista=m;
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		mAdapter = new FirebaseRecyclerAdapter<Musica, MusicasHolder>(Musica.class, R.layout.my_music_holder, MusicasHolder.class, Musica.getQuery().equalTo("artistaKey",mArtista.key)
																	  .orderByChild("playedTimes")){

			@Override
			protected void populateViewHolder(MusicasHolder viewHolder, final Musica model, int position)
			{
				hideLoading();
				viewHolder.feedWithCount(model, new MusicasHolder.OnItemClickListener(){

						@Override
						public void onClick(View v)
						{
							List<Musica> mMusicas = mAdapter.getItens();
							if(v.getId() == R.id.mymusicholderImageViewMore){
								new MusicaMoreDialog(model).show(getChildFragmentManager(),"");
							}else{
								MusicMasterHandler.setToPlay(getActivity(), Arrays.asList(mMusicas.toArray()), true);
								MusicMasterHandler.setToPlayPosition(mMusicas.indexOf(model));
								getActivity().startService(new Intent(getActivity(),MusicService.class).setAction(MusicService.ACTION_PLAY));
								//ctx.startActivity(new Intent(ctx,PlayerActivity.class));
							}
						}
					});
			}
		};
		setRecyclerAdapter(new LinearLayoutManager(getActivity()), mAdapter);
	}
	
}
