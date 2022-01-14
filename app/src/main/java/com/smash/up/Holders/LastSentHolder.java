package com.smash.up.Holders;

import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.mindorks.butterknifelite.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.up.*;
import com.smash.up.Models.*;

import com.smash.up.R;
import com.smash.up.Helpers.*;
import java.text.*;
import android.net.*;
import android.view.View.*;

public class LastSentHolder extends RecyclerView.ViewHolder
{
	@BindView(R.id.lastsentmodelImageView) ImageView mImage;
	@BindView(R.id.last_sent_modelTextViewName) TextView mName;
	@BindView(R.id.last_sent_modelTextViewArtista) TextView mArtistaName;
	@BindView(R.id.last_sent_modelTextViewSent) TextView mSentWhen;
	
	OnClickListener mClick;
	public LastSentHolder(View v){
		super(v);
		ButterKnifeLite.bind(this,v);
		v.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					mClick.onClick(p1);
				}
			});
	}
	
	public void feedIt(Musica m, OnClickListener mclick){
		this.mClick= mclick;
		if(m.getNome()!=null)mName.setText(m.getNome());
		if(m.getArtistaNome()!=null)mArtistaName.setText(m.getArtistaNome());
		if(m.sentDate!=null)mSentWhen.setText(android.text.format.DateFormat.getTimeFormat(mName.getContext()).format(m.sentDate));
		
		if(m.getAlbumKey()!=null)Album.getAlbumArt(m.getAlbumKey(), new Album.OnGetAlbumArtListener(){

				@Override
				public void onResult(Uri i)
				{
					PicassoHelper.get().load(i).fit().centerCrop().into(mImage);
				}
			});
	}
}
