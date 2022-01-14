package com.smash.up.Holders;
import android.view.*;
import android.widget.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.up.*;
import com.smash.up.Adapters.*;
import com.smash.up.Models.*;

public class MusicaHolder extends RecyclerAdapter.Holder
{

	private MusicaHolder.OnMusicaClickListener mListener;
	Musica mMusica;
	
	@BindView(R.id.musicasholderImageViewDelete) ImageView mDelete;
	@BindView(R.id.musicasholderTextView1) TextView mText1;
	@BindView(R.id.musicasholderTextView2) TextView mText2;
	public MusicaHolder(View v){
		super(v);
		
		com.mindorks.butterknifelite.ButterKnifeLite.bind(this, v);
		itemView.setOnClickListener(this);
		mDelete.setOnClickListener(this);
	}
	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		
		if(mListener==null)return;
		
		if(v.equals(mDelete))mListener.onDeleteClick(mMusica);
		else mListener.onClick(mMusica);
	}
	
	public void feedIt(Musica m, OnMusicaClickListener o){
		this.mListener = o;
		mMusica=m;
		mText1.setText(mMusica.nome);
		mText2.setText(mMusica.artistaNome);
	}
	
	public interface OnMusicaClickListener{
		public void onDeleteClick(Musica m);
		public void onClick(Musica m);
	}
}
