package com.smash.smash.Fragments;
import android.content.*;
import android.os.*;
import android.support.v7.widget.*;
import android.view.*;
import com.google.firebase.database.*;
import com.smash.smash.Helpers.*;
import com.smash.smash.Holders.*;
import com.smash.smash.Models.*;
import java.util.*;
import android.net.*;
import java.io.*;
import com.smash.smash.R;
import com.google.android.gms.tasks.*;
import com.google.firebase.storage.*;
import android.widget.*;
import android.util.*;
import com.google.common.collect.*;

public class MyMusicasFrag extends RecyclerBaseFrag
{
	EasyRecyclerAdapter<Musica, MusicasHolder> mAdapter;
	List<Musica> mMusicas = new ArrayList<>();
	HashMap<String,Musica> mMusicasHash= new HashMap<>();
	public static final String TAG = "MyMusicasFrag";

	public MyMusicasFrag(){}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		showFab();
		mFab.setImageResource(R.drawable.ic_shuffle_white);
	
		
		mAdapter = new EasyRecyclerAdapter<Musica, MusicasHolder>(mMusicas, Musica.class, R.layout.my_music_holder, MusicasHolder.class){

			@Override
			protected void populateViewHolder(final MusicasHolder viewHolder, final Musica model, int position)
			{
				viewHolder.feedIt(model, new MusicasHolder.OnItemClickListener(){

						@Override
						public void onClick(View v)
						{
							if(v.getId() == R.id.mymusicholderImageViewMore){
								new MusicaMoreDialog(model).show(getChildFragmentManager(),"");
							}else{
							MusicMasterHandler.setToPlay(getActivity(), mMusicas, true);
							MusicMasterHandler.setToPlayPosition(MusicMasterHandler.getToPlay(getActivity()).indexOf(model));
							getActivity().startService(new Intent(getActivity(),MusicService.class).setAction(MusicService.ACTION_PLAY));
							//ctx.startActivity(new Intent(ctx,PlayerActivity.class));
							}
						}
					});
				hideLoading();
			}
		};
		
		User.getMusicas(new User.OnGetMyMusicsListener(){

				@Override
				public void onGot(List<String> mMusicasHash)
				{
					mMusicas.clear();
					for(String key : mMusicasHash){
						Musica.findByKey(key, new Musica.OnFindMusicaListener(){

							@Override
							public void onResult(Musica m, DatabaseError e)
							{
								if(m!=null){
									mMusicas.add(m);
									Collections.sort(mMusicas, new Comparator<Musica>(){

											@Override
											public int compare(Musica p1, Musica p2)
											{

												return p1.getNome().compareToIgnoreCase(p2.getNome());
											}
										});
									mAdapter.notifyDataSetChanged();
									Album.getAlbumArt(m.albumKey, new Album.OnGetAlbumArtListener(){

											@Override
											public void onResult(Uri i)
											{
												PicassoHelper.get().load(i).fetch();
											}
										});
								}
								}
						});
					}
				}
			});
		setRecyclerAdapter(new LinearLayoutManager(getActivity()), mAdapter);
		
	}
	
}
