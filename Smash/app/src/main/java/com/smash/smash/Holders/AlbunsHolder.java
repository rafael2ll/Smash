package com.smash.smash.Holders;
import android.widget.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.smash.R;
import com.smash.smash.Adapters.*;
import android.view.View.*;
import com.mindorks.butterknifelite.ButterKnifeLite;
import android.view.*;
import com.smash.smash.Models.*;
import com.google.firebase.database.*;
import com.smash.smash.Helpers.*;

public class AlbunsHolder extends RecyclerAdapter.Holder
{
	@BindView(R.id.albumholderImageView) ImageView mImage;
	@BindView(R.id.albumholderTextView1) TextView mText;
	@BindView(R.id.albumholderTextView2) TextView mText2;
	
	OnClickListener mClick;
	
	public AlbunsHolder(View v){
		super(v);
		ButterKnifeLite.bind(this, v);
		v.setOnClickListener(this);
	}
	
	public void feedIt(Album album,boolean showReleased, OnClickListener i){
		this.mClick=i;
		mText.setText(album.nome);
		PicassoHelper.get().load(album.imageUri).fit().centerCrop().into(mImage);
		if(showReleased)mText2.setText(""+album.releasedDate);
		else mText2.setText(album.artistaNome);
	}

	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		mClick.onClick(v);
	}

}
