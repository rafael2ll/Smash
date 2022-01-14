package com.smash.smash.Fragments;
import android.os.*;
import android.view.*;
import com.smash.smash.R;
import com.smash.smash.Helpers.*;
import com.smash.smash.Holders.*;
import com.smash.smash.Models.*;
import java.util.*;
import android.view.View.*;
import com.google.firebase.database.*;
import android.support.v7.widget.*;
import android.content.*;
import com.smash.smash.*;

public class MyAlbunsFrag extends RecyclerBaseFrag
{
	List<Album> mAlbuns = new ArrayList<>();
	EasyRecyclerAdapter<Album, AlbunsHolder> mAdapter;

	public MyAlbunsFrag(){}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		showFab();
		mFab.setImageResource(R.drawable.ic_shuffle_white);
		mAlbuns.clear();
		/*User.getAlbuns(new ValueEventListener(){

				@Override
				public void onDataChange(DataSnapshot p1)
				{
					Album a = p1.getValue(Album.class),
					if(!mAlbuns.contains(a)Q)mAlbuns.add(a);
					mAdapter.notifyDataSetChanged();
				}

				@Override
				public void onCancelled(DatabaseError p1)
				{
					// TODO: Implement this method
				}
			});*/
		Album.getMainRef().addListenerForSingleValueEvent(new ValueEventListener(){

				@Override
				public void onDataChange(DataSnapshot p1)
				{
					for(DataSnapshot ds : p1.getChildren()){
						mAlbuns.add(ds.getValue(Album.class));
						mAdapter.notifyDataSetChanged();
					}
				}

				@Override
				public void onCancelled(DatabaseError p1)
				{
					// TODO: Implement this method
				}
			});
		mAdapter = new EasyRecyclerAdapter<Album, AlbunsHolder>(mAlbuns, Album.class, R.layout.album_holder, AlbunsHolder.class){

			@Override
			protected void populateViewHolder(AlbunsHolder viewHolder, final Album model, int position)
			{
				hideLoading();
				viewHolder.feedIt(model, false, new OnClickListener(){

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
