package com.smash.up.Frags;

import com.smash.up.Helpers.*;
import android.view.*;
import android.os.*;
import com.smash.up.Models.*;
import com.smash.up.Holders.*;
import com.smash.up.*;
import com.smash.up.Dialogs.*;
import android.support.v7.widget.*;

public class VerArtistasFrag extends RecyclerBaseFrag
{
	FirebaseRecyclerAdapter mAdapter;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		
		mAdapter = new FirebaseRecyclerAdapter<Artista, EditHolder>(Artista.class, R.layout.two_fields_list, EditHolder.class, Artista.getQuery().orderByChild("imageUri")){

			@Override
			protected void populateViewHolder(EditHolder viewHolder, Artista model, int position)
			{
				hideLoading();
				viewHolder.feedIt(model, new EditHolder.OnAddPhotoClickListener(){

						@Override
						public void onClick(Album a, boolean isMore)
						{
						}

						@Override
						public void onClick(Artista b, boolean isMore)
						{
							new AttachPhotoBottomFrag(b).show(getChildFragmentManager(),"");
						}
					});
			}
		};
		setRecyclerAdapter(new LinearLayoutManager(getActivity()), mAdapter);
	}
}
