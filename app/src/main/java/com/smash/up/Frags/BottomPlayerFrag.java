package com.smash.up.Frags;

import android.media.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.up.*;
import com.smash.up.Models.*;
import java.io.*;
import java.util.concurrent.*;

public class BottomPlayerFrag extends BottomSheetDialogFragment implements OnClickListener, MediaPlayer.OnPreparedListener, Runnable
{
	@BindView(R.id.bottomplayerSeekbar) SeekBar mSeekbar;
	@BindView(R.id.bottomplayerTextViewTime) TextView mProgress;
	@BindView(R.id.bottomplayerTextViewNome) TextView mName;
	@BindView(R.id.bottomplayerTextViewOthers) TextView mOthers;
	@BindView(R.id.bottomplayerImageViewPlay) ImageView mPlayPause;
	
	Musica mMusica;
	MediaPlayer mMediaPlayer;
	Handler durationHandler = new Handler();
	
	public BottomPlayerFrag(Musica m){
		this.mMusica=m;
		mMediaPlayer = MainActivity.getMediaPlayer();
		mMediaPlayer.reset();
		try
		{
			mMediaPlayer.setDataSource(mMusica.getMusicaUri());
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.prepareAsync();
			mMediaPlayer.setOnPreparedListener(this);
		}
		catch (Exception e){}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.bottom_player, container, false);
		com.mindorks.butterknifelite.ButterKnifeLite.bind(this, v);
		mPlayPause.setOnClickListener(this);
		
		mName.setText(mMusica.getNome());
		mOthers.setText(mMusica.getAlbumNome()+ " • "+mMusica.getArtistaNome());
		
		mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

				@Override
				public void onProgressChanged(SeekBar p1, int p2, boolean p3)
				{
					
				}

				@Override
				public void onStartTrackingTouch(SeekBar p1)
				{
					// TODO: Implement this method
				}

				@Override
				public void onStopTrackingTouch(SeekBar p1)
				{
					mMediaPlayer.seekTo(p1.getProgress());
				}
			});
		return v;
	}

	@Override
	public void onClick(View p1)
	{
		if(mMediaPlayer.isPlaying()){
			mMediaPlayer.pause();
			mPlayPause.setImageResource(R.drawable.ic_play);
		}else{
			mMediaPlayer.start();
			mPlayPause.setImageResource(R.drawable.ic_pause);
		}
	}

	@Override
	public void onPrepared(MediaPlayer p1)
	{
		p1.start();
		mSeekbar.setMax(mMusica.getDuration());
		durationHandler.postDelayed(this, 100);
	}
	
	@Override
	public void run()
	{

		if(mMediaPlayer!=null && mMediaPlayer.isPlaying()){
			if(mMediaPlayer.getCurrentPosition()>0){

				double timeRemaining = mMediaPlayer.getCurrentPosition();
				long minutes=(int)TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining);
				long seconds=(int)TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining));
				if(seconds<10){
					mProgress.setText(String.format("0%d:0%d",minutes,(int)seconds));      //repeat yourself that again in 100 miliseconds
				}
				else{
					mProgress.setText(String.format("0%d:%d",minutes,(int)seconds));      //repeat yourself that again in 100 miliseconds
				}
				mSeekbar.setProgress((int)timeRemaining);
			}
		}
		durationHandler.postDelayed(this, 100);
		
	}

}
