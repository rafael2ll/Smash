package com.smash.smash.Holders;
import android.view.*;
import android.widget.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.smash.*;
import com.smash.smash.Adapters.*;
import com.mindorks.butterknifelite.ButterKnifeLite;
import com.smash.smash.Models.*;
import java.io.*;
import com.smash.smash.Helpers.*;
import android.os.AsyncTask;
import android.support.v4.content.*;

public class MusicasHolder extends RecyclerAdapter.Holder
{
	@BindView(R.id.mymusicholderTextViewNome) TextView mNome;
	@BindView(R.id.mymusicholderView1) View mTag;
	@BindView(R.id.mymusicholderTextViewArtista) TextView mArtista;
	@BindView(R.id.mymusicholderImageViewMore) ImageView mImage;
	@BindView(R.id.mymusicholderImageView1) ImageView mPlaying;
	OnItemClickListener mClick;
	
	public MusicasHolder(View v){
		super(v);
		ButterKnifeLite.bind(this, v);
		v.setOnClickListener(this);
		mImage.setOnClickListener(this);
	}
	
	public void feedWithCount(final Musica m, OnItemClickListener o){
		this.mClick=o;
		mNome.setText(m.nome);
		mArtista.setText(m.getPlayedTimes()+ " • from '"+m.getAlbumNome()+"'");
		Musica playing = MusicMasterHandler.getPlaying();
		if(playing!=null){
			if(playing.key.equals(m.key))mPlaying.setVisibility(View.VISIBLE);
			else mPlaying.setVisibility(View.INVISIBLE);
		} else {
			mPlaying.setVisibility(View.INVISIBLE);
		}
		new AsyncTask<Void,Void, Boolean>(){

			@Override
			protected Boolean doInBackground(Void[] p1)
			{
				return isOnline();
			}

			@Override
			protected void onPostExecute(Boolean b){
				if(b){
					mTag.setBackgroundColor(ContextCompat.getColor(mNome.getContext(), R.color.grey_600));
				}
				else if(MusicMasterHandler.isOffline(mNome.getContext(), m)){
					mTag.setBackgroundColor(ContextCompat.getColor(mNome.getContext(), R.color.grey_800));
				}
				else{
					mTag.setBackgroundColor(ContextCompat.getColor(mNome.getContext(), R.color.grey_300));
					itemView.setClickable(false);
				}
			}
		}.execute();
	}
	
	public void feedIt(final Musica m, OnItemClickListener o){
		this.mClick=o;
		mNome.setText(m.nome);
		mArtista.setText(m.artistaNome);
		Musica playing = MusicMasterHandler.getPlaying();
		if(playing!=null){
			if(playing.key.equals(m.key))mPlaying.setVisibility(View.VISIBLE);
			else mPlaying.setVisibility(View.INVISIBLE);
		} else {
			mPlaying.setVisibility(View.INVISIBLE);
		}
		new AsyncTask<Void,Void, Boolean>(){

			@Override
			protected Boolean doInBackground(Void[] p1)
			{
				return isOnline();
			}
			
		@Override
		protected void onPostExecute(Boolean b){
			if(b){
				mTag.setBackgroundColor(ContextCompat.getColor(mNome.getContext(), R.color.grey_600));
			}
			else if(MusicMasterHandler.isOffline(mNome.getContext(), m)){
				mTag.setBackgroundColor(ContextCompat.getColor(mNome.getContext(), R.color.grey_800));
			}
			else{
				mTag.setBackgroundColor(ContextCompat.getColor(mNome.getContext(), R.color.grey_300));
				itemView.setClickable(false);
			}
		}
		}.execute();
	}
	
	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		mClick.onClick(v);
	}
	
	public interface OnItemClickListener{
		public void onClick(View v);
	}
	
	public boolean isOnline() {

		Runtime runtime = Runtime.getRuntime();
		try {

			Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
			int   exitValue = ipProcess.waitFor();
			return (exitValue == 0);

		} catch (IOException e)          { e.printStackTrace(); } 
		catch (InterruptedException e) { e.printStackTrace(); }

		return false;
	}
}
