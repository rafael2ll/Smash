package com.smash.up.Holders;
import android.support.v7.widget.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.mindorks.butterknifelite.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.up.*;

import com.smash.up.R;
import com.smash.up.Models.*;
import com.google.firebase.database.*;

public class EditHolder extends RecyclerView.ViewHolder implements OnClickListener
{
	@BindView(R.id.twofieldslistTextView1) TextView mText1;
	@BindView(R.id.twofieldslistTextView2) TextView mText2;
	@BindView(R.id.twofieldslistImageView) ImageView mAddPhoto;
	
	Album album;
	Artista artista;
	OnAddPhotoClickListener mListener;
	public EditHolder(View v){
		super(v);
		ButterKnifeLite.bind(this, v);
		mAddPhoto.setOnClickListener(this);
		v.setOnClickListener(this);
	}

	public void feedIt(Object o, OnAddPhotoClickListener mListener){
		this.mListener= mListener;
		
		if(o instanceof Artista){
			this.artista = (Artista) o;
			
			mText1.setText(artista.getNome());
			mText2.setVisibility(View.GONE);
			
			if(artista.imageUri==null) mAddPhoto.setImageResource(R.drawable.ic_has_not_img);
			else mAddPhoto.setImageResource( R.drawable.ic_has_image);
		}
		else if(o instanceof Album){
			this.album = (Album )o;
			
			mText1.setText(album.getNome());
			if(album.getImageUri().equals("toAdd")) mAddPhoto.setImageResource(R.drawable.ic_has_not_img);
			else mAddPhoto.setImageResource( R.drawable.ic_has_image);
			try{
				album.artistaNome.charAt(1);
				mText2.setText(album.artistaNome);
			}catch(Exception e){
			Artista.getMainRef().child(album.artistaKey).child("nome").addListenerForSingleValueEvent(new ValueEventListener(){

					@Override
					public void onDataChange(DataSnapshot p1)
					{
						Album.getMainRef().child(album.key).child("artistaNome").setValue(p1.getValue(String.class));
						mText2.setText(p1.getValue(String.class));
					}

					@Override
					public void onCancelled(DatabaseError p1)
					{
						// TODO: Implement this method
					}
				});
				}
		}
	}
	@Override
	public void onClick(View p1)
	{
		if(album!=null){
			mListener.onClick(album, p1.getId() == R.id.twofieldslistImageView);
			}
		else if(artista!=null) {
			mListener.onClick(artista, p1.getId() == R.id.twofieldslistImageView);
		}
	}
	
	public interface OnAddPhotoClickListener{
		public void onClick(Album a, boolean isMore);
		public void onClick(Artista b, boolean isMore);
	}
}
