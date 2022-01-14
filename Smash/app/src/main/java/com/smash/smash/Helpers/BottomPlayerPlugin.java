package com.smash.smash.Helpers;

import android.content.*;
import android.net.*;
import android.os.*;
import android.support.v4.media.*;
import android.support.v4.media.session.*;
import android.support.v7.widget.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.mindorks.butterknifelite.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.smash.*;
import com.smash.smash.Models.*;
import com.squareup.picasso.*;

import com.smash.smash.R;
import android.support.v4.content.*;

public class BottomPlayerPlugin implements Runnable, OnClickListener
{
	View mBottomPlayer;
	@BindView(R.id.bottomplayerfragCardView) CardView mPlayerCard;
	@BindView(R.id.bottomplayerfragTextViewNome) static TextView mPlayerNome;
	@BindView(R.id.bottomplayerfragTextViewArtista) static TextView mPlayerArtista;
	@BindView(R.id.bottomplayerfragImageViewAlbum) static ImageView mPlayerAlbum;
	@BindView(R.id.bottomplayerfragImageViewNext) static ImageView mPlayerNext;
	@BindView(R.id.bottomplayerfragImageViewPlay) static ImageView mPlayerPlayPause;
	@BindView(R.id.bottomplayerProgressBar) static ProgressBar mPlayerProgressBar;
	Context ctx;
	Handler mHandler= new Handler();
	MediaMetadataCompat mMetadata;
	PlaybackStateCompat mPlayState;
	MediaControllerCompat mediaController;
	MediaControllerCompat.TransportControls mTransportControls;
	MediaControllerCompat.Callback mCallback ;
	Musica mMusica;
	String TAG = "BottomPlayer";
	
	public BottomPlayerPlugin(Context ctx){
		this.ctx=ctx;
		mBottomPlayer = LayoutInflater.from(ctx).inflate(R.layout.bottom_player_frag, null, false);
		ButterKnifeLite.bind(this, mBottomPlayer);
		mPlayerCard.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.accent));
		mPlayerCard.setOnClickListener(this);mPlayerNext.setOnClickListener(this);mPlayerPlayPause.setOnClickListener(this);
		mCallback = new MediaControllerCompat.Callback(){
			@Override
			public void onPlaybackStateChanged(PlaybackStateCompat state) {
				mPlayState = state;

				updatePlaybackState( state.getState()== state.STATE_PLAYING);
				LogHelper.d(TAG, "Received new playback state" + state);
				
			}

			@Override
			public void onMetadataChanged(MediaMetadataCompat metadata) {
				mMetadata = metadata;
				LogHelper.d(TAG, "Received new metadata "+ metadata);
				updateMetadata(metadata);
			}

			
			@Override
			public void onSessionDestroyed() {
				super.onSessionDestroyed();
				LogHelper.d(TAG, "Session was destroyed, resetting to the new session token");
				
			}
		};
	}
	
	@Override
	public void onClick(View p1)
	{
		if(p1==mPlayerCard)ctx.startActivity(new Intent(ctx,PlayerActivity.class));
		else if(p1==mPlayerNext)mTransportControls.skipToNext();
		else if(p1==mPlayerPlayPause){
			if(mPlayState.getState()==mPlayState.STATE_PLAYING){
				mTransportControls.pause();
				mPlayerPlayPause.setImageResource(R.drawable.ic_play_circle_outline);
			}else{
				mTransportControls.play();
				mPlayerPlayPause.setImageResource(R.drawable.ic_pause_circle_outline);
			}
		}
	}
	public void initListener(MediaSessionCompat.Token token) throws RemoteException{
		mediaController = new MediaControllerCompat(ctx, token);
	
		mediaController.registerCallback(mCallback);
		mPlayState = mediaController.getPlaybackState();
		mTransportControls=mediaController.getTransportControls();
		
	}
	public View get(){
		return mBottomPlayer;
	}
	private void updateMetadata(MediaMetadataCompat m)
	{
		MediaDescriptionCompat description=m.getDescription();
		mHandler.postDelayed(this, 100);
		mPlayerNome.setText(description.getTitle());
		mPlayerArtista.setText(description.getSubtitle());
		Album.getAlbumArt(m.getString(m.METADATA_KEY_ALBUM_ART_URI), new Album.OnGetAlbumArtListener(){

				@Override
				public void onResult(Uri i)
				{
					if(i!=null)PicassoHelper.get().load(i).into(mPlayerAlbum);
				}
			});
	}
	
	public void attach(Musica m){
		mMusica=m;
	}
	public void updatePlaybackState(boolean isPlaying){
		if(isPlaying)mPlayerPlayPause.setImageResource(R.drawable.ic_pause_circle_outline_grey);
		else mPlayerPlayPause.setImageResource(R.drawable.ic_play_circle_outline_grey);
	}
	@Override
	public void run()
	{
		if(MusicService.getMP().getCurrentPosition()<1){
			mPlayerProgressBar.setIndeterminate(true);
		}else{
			mPlayerProgressBar.setIndeterminate(false);
			mPlayerProgressBar.setMax((int)mMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
		}
		mPlayerProgressBar.setProgress((int)MusicService.getMP().getCurrentPosition());
		log.w(TAG, "Position: "+ mPlayState.getPlaybackState());
		mHandler.postDelayed(this, 100);
	}
}
