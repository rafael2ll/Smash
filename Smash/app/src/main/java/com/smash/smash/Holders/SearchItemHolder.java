package com.smash.smash.Holders;
import android.content.*;
import android.net.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.mindorks.butterknifelite.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.smash.*;
import com.smash.smash.Adapters.*;
import com.smash.smash.Helpers.*;
import com.smash.smash.Models.*;

import com.smash.smash.R;

public class SearchItemHolder extends RecyclerAdapter.Holder
{
	@BindView(R.id.searchholderImageView1) public ImageView mImage;
	@BindView(R.id.searchholderTextView1) public TextView mText1;
	@BindView(R.id.searchholderTextView2) public TextView mText2;
	OnClickListener mClick;
	Musica mMusica;
	Album mAlbum;
	Artista mArtista;
	//Playlist mPlaylist;
	public SearchItemHolder(View v){
		super(v);
		ButterKnifeLite.bind(this, v);
		v.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		mClick.onClick(v);
	}

	public void feedIt(Album b){
		mAlbum = b;
		mClick = new OnClickListener(){

			@Override
			public void onClick(View p1)
			{
				Intent i = new Intent(p1.getContext(), AlbumActivity.class);
				i.putExtra(AlbumActivity.ALBUM_KEY, mAlbum.key);
				p1.getContext().startActivity(i);
			}
		};
		mText1.setText(mAlbum.nome);
		mText2.setText(mAlbum.artistaNome);
		PicassoHelper.get().load(mAlbum.imageUri)
		.transform(new CropCircleTransformation())
		.into(mImage);
	}
	
	public void feedIt(Artista a){
		mArtista = a;
		
		mText1.setText(mArtista.nome);
		mText2.setVisibility(View.GONE);
		mText1.setPadding(0, 20, 0, 0);
		PicassoHelper.get().load(mArtista.imageUri)
			.transform(new CropCircleTransformation())
			.into(mImage);
	}
	public void feedIt(Musica m, OnClickListener o){
		mMusica = m;
		mClick=o;
		mText1.setText(mMusica.nome);
		mText2.setText(mMusica.albumNome +" • "+mMusica.artistaNome);
		Album.getAlbumArt(mMusica.albumKey, new Album.OnGetAlbumArtListener(){

				@Override
				public void onResult(Uri i)
				{
					PicassoHelper.get().load(i)
						.transform(new CropCircleTransformation())
						.into(mImage);
				}
			});
		
	}
}
