package com.smash.smash.Holders;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.mindorks.butterknifelite.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.smash.*;
import com.smash.smash.Adapters.*;
import com.smash.smash.Models.*;

import com.smash.smash.R;
import com.smash.smash.Helpers.*;

public class ArtistasHolder extends RecyclerAdapter.Holder
{
	@BindView(R.id.artistaholderImageView) ImageView mImage;
	@BindView(R.id.artistaholderTextView) TextView mText;
	OnClickListener mClick;
	
	public ArtistasHolder(View v){
		super(v);
		ButterKnifeLite.bind(this,v);
		v.setOnClickListener(this);
	}
	
	public void feedIt(Artista art, OnClickListener i){
		this.mClick=i;
		mText.setText(art.nome);
		
		if(art.imageUri!=null)PicassoHelper.get().load(art.imageUri).error(R.drawable.ic_artista)
		.placeholder(R.drawable.ic_artista).transform(new CropCircleTransformation())
		.into(mImage);
		else PicassoHelper.get().load(R.drawable.ic_artista).into(mImage);
	}
	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		mClick.onClick(v);
	}
}
