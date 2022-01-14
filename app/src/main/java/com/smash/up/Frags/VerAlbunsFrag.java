package com.smash.up.Frags;
import android.os.*;
import android.support.v7.widget.*;
import android.view.*;
import com.smash.up.*;
import com.smash.up.Dialogs.*;
import com.smash.up.Helpers.*;
import com.smash.up.Holders.*;
import com.smash.up.Models.*;

public class VerAlbunsFrag extends RecyclerBaseFrag
{
	FirebaseRecyclerAdapter mAdapter;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		mAdapter = new FirebaseRecyclerAdapter<Album, EditHolder>(Album.class, R.layout.two_fields_list, EditHolder.class, Album.getQuery().orderByChild("imageUri")){

			@Override
			protected void populateViewHolder(EditHolder viewHolder, Album model, int position)
			{
				hideLoading();
				viewHolder.feedIt(model, new EditHolder.OnAddPhotoClickListener(){

						@Override
						public void onClick(Album a, boolean isMore)
						{
							if(isMore)new AttachPhotoBottomFrag(a).show(getChildFragmentManager(),"");
							else new EditAlbumDialog(a).show(getChildFragmentManager(),"");
						}

						@Override
						public void onClick(Artista b, boolean isMore)
						{
							
						}
					});
			}
		};
		setRecyclerAdapter(new LinearLayoutManager(getActivity()), mAdapter);
	}
}
