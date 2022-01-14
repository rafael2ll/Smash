package com.smash.up;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.media.*;
import android.support.v4.media.session.*;
import android.support.v4.view.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.google.android.gms.tasks.*;
import com.smash.up.Itens.*;
import com.squareup.picasso.*;
import java.util.concurrent.*;

import android.support.v7.widget.Toolbar;
import com.malinskiy.materialicons.*;
import android.util.*;
import com.smash.up.Adapters.*;
import jp.wasabeef.picasso.transformations.*;
import java.util.*;
import android.media.*;
import android.graphics.*;

public class PlayerActivity extends AppCompatActivity implements Runnable,View.OnClickListener
{
	ImageView back,add,more,prev,next,playPause,repeat,shuffle;
	Toolbar toolbar;
	Handler durationHandler=new Handler();
	String TAG="PlayerActivity";
	PlayerAdapter adapter;
	MediaControllerCompat mediaController;
	MediaControllerCompat.Callback mCallback;
	MediaControllerCompat.TransportControls mTransportControls;
	PlaybackStateCompat mPlaybackState;
	MediaMetadataCompat mMetadata;
	MediaSessionCompat mSession;
	MediaPlayer mMediaPlayer;
	TextView timePassed,nome,artista,timeTotal;
	ViewPager mViewPager;
	SeekBar seekBar;
	MusicService musicService;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_main);
		toolbar=(Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Commons.setTaskBarColored(this);
		seekBar=(SeekBar)findViewById(R.id.playermainSeekBarCompat);
		mViewPager=(ViewPager)findViewById(R.id.playermainViewPager);
		timeTotal=(TextView)findViewById(R.id.playermainTextView1);
		timePassed=(TextView)findViewById(R.id.playermainTextViewPassed);
		nome=(TextView)findViewById(R.id.playermainTextViewNome);
		artista=(TextView)findViewById(R.id.playermainTextViewArtista);
		
		add=findImageView(R.id.playermainImageViewAdd);playPause=findImageView(R.id.playermainImageViewPlayPause );
		more=findImageView(R.id.playermainImageMore );repeat=findImageView(R.id.playermainImageViewRepeat );
		prev=findImageView(R.id.playermainImageViewPrev);shuffle=findImageView(R.id.playermainImageViewShuffle );
		next=findImageView(R.id.playermainImageViewNext);back=findImageView(R.id.playermainImageViewBack);
		add.setOnClickListener(this);playPause.setOnClickListener(this);
		more.setOnClickListener(this);repeat.setOnClickListener(this);
		prev.setOnClickListener(this);shuffle.setOnClickListener(this);
		next.setOnClickListener(this);
		
		addIcon(add,Iconify.IconValue.zmdi_plus_circle_o,R.color.icons,25);addIcon(more,Iconify.IconValue.zmdi_more,R.color.icons,25);
		//addIcon(next,Iconify.IconValue.zmdi_arrow_forward,R.color.icons,35);addIcon(prev,Iconify.IconValue.zmdi_arrow_back,R.color.icons,35);
		addIcon(playPause,Iconify.IconValue.zmdi_pause,R.color.icons,40);
		switch(MusicMasterHandler.getRepeatState(this)){
			case 0:addIcon(repeat,Iconify.IconValue.zmdi_repeat,R.color.grey_700,20);break;
			case 1:addIcon(repeat,Iconify.IconValue.zmdi_repeat,R.color.deep_orange_a400,20);break;
			case 2:addIcon(repeat,Iconify.IconValue.zmdi_repeat_one,R.color.deep_orange_a400,20);

		}
		if(MusicMasterHandler.isShuffling(this))addIcon(shuffle,Iconify.IconValue.zmdi_shuffle,R.color.deep_orange_a400,20);
		else addIcon(shuffle,Iconify.IconValue.zmdi_shuffle,R.color.grey_700,20);
		
		try
		{
			musicService=MusicService.getItself();
			mMediaPlayer=MusicService.getMP();
			mSession=musicService.getSession();
			loadCallback();
			adapter=new PlayerAdapter(getSupportFragmentManager());
			List<PlayerFragment> fragList=new ArrayList<>();
			for(Musica a :MusicMasterHandler.getToPlay()){
				fragList.add(new PlayerFragment(a));
			}
			adapter.setFrags(fragList);
			mViewPager.setAdapter(adapter);
			mediaController = new MediaControllerCompat(this, musicService.getSessionToken());
			mediaController.registerCallback(mCallback);
			mTransportControls=mediaController.getTransportControls();
			updateMetadata();
			seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

					@Override
					public void onProgressChanged(SeekBar p1, int p2, boolean p3)
					{
						p1.setProgress(p2);
						mTransportControls.seekTo(p2);
					}

					@Override
					public void onStartTrackingTouch(SeekBar p1)
					{
						// TODO: Implement this method
					}

					@Override
					public void onStopTrackingTouch(SeekBar p1)
					{
						// TODO: Implement this method
					}
				});
		}
		catch (RemoteException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void onDestroy()
	{
		mediaController.unregisterCallback(mCallback);
		super.onDestroy();
	}

	private void loadCallback()
	{
		mCallback=new MediaControllerCompat.Callback(){
			@Override
			public void onPlaybackStateChanged(PlaybackStateCompat state) {
				mPlaybackState = state;
				
				LogHelper.d(TAG, "Received new playback state"+ state);
				updatePlaybackState();
			}

			@Override
			public void onMetadataChanged(MediaMetadataCompat metadata) {
				mMetadata = metadata;
				LogHelper.d(TAG, "Received new metadata "+ metadata);
				updateMetadata();
			}
			
			@Override
			public void onSessionDestroyed() {
				super.onSessionDestroyed();
				LogHelper.d(TAG, "Session was destroyed, resetting to the new session token");
				updateSessionToken();
			}
		};
	}
	private void updatePlaybackState()
	{
		if(mPlaybackState==null)mPlaybackState=mSession.getController().getPlaybackState();
		if(mPlaybackState.getState()==mPlaybackState.STATE_PLAYING){
			addIcon(playPause,Iconify.IconValue.zmdi_pause,R.color.icons,40);
		}
		else if(mPlaybackState.getState()==mPlaybackState.STATE_PAUSED){
			addIcon(playPause,Iconify.IconValue.zmdi_play,R.color.icons,40);
		}
	}
	private void updateMetadata()
	{
		if(!isNull()){
		if(mMetadata==null)mMetadata=mSession.getController().getMetadata();
		MediaDescriptionCompat description= mMetadata.getDescription();
		mViewPager.setCurrentItem((int)mMetadata.getLong("POSITION"),true);
		toolbar.setTitle(description.getTitle());
		toolbar.setSubtitle(description.getSubtitle());
		toolbar.setSubtitleTextColor(Color.WHITE);
		try{
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}catch(NullPointerException e){}
		nome.setText(description.getTitle());
		final List<Transformation> transformList=new ArrayList<>();
		transformList.add(new BlurTransformation(this,25,5));
		transformList.add(new ColorFilterTransformation(getResources().getColor(R.color.grey_900_half )));
		artista.setText(description.getSubtitle());
			Album.getAlbumArt(mMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI), new Album.OnGetAlbumArtListener(){

					@Override
					public void onResult(Uri i)
					{
						if(i!=null)Picasso.with(PlayerActivity.this).load(i).fit()
							.centerCrop().transform(transformList).into(back);
					}
				});
			double timeRemaining= mMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
			long minutes=(int)TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining);
			long seconds=(int)TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining));
	
			if(seconds<10)timeTotal.setText(String.format("0%d:0%d",minutes,(int)seconds));
			else timeTotal.setText(String.format("0%d:%d",minutes,(int)seconds));
			seekBar.setMax((int)timeRemaining);
		durationHandler.postDelayed(this,100);
		}
	}

	@Override
	public void onClick(View p1)
	{
		if(p1.equals(prev))mTransportControls.skipToPrevious();
		else if(p1.equals(next))mTransportControls.skipToNext();
		else if(p1.equals(playPause))handlePlayRequest();
		else if(p1.equals(repeat))handleRepeatState();
		else if(p1.equals(shuffle))handleShuffleRequest();
		else if(p1.equals(more))handleMoreRequest();
		else if(p1.equals(add))handleAddRequest();
	}

	private void handleAddRequest()
	{
		
	}

	private void handleMoreRequest()
	{
		// TODO: Implement this method
	}

	private void handleShuffleRequest()
	{
		boolean state=musicService.shuffle();
		if(state)addIcon(shuffle,Iconify.IconValue.zmdi_shuffle,R.color.deep_orange_a400,20);
		else addIcon(shuffle,Iconify.IconValue.zmdi_shuffle,R.color.grey_700,20);

		Integer index=musicService.getPlayingPosition();
		List<PlayerFragment> fragList=new ArrayList<>();
		for(Musica a :musicService.mPlayingMusicas){
			fragList.add(new PlayerFragment(a));
		}
		adapter.setFrags(fragList);
		mViewPager.setAdapter(adapter);
		mViewPager.setCurrentItem(index,false);
	}

	private void handleRepeatState()
	{
		addIcon(repeat,Iconify.IconValue.zmdi_repeat,R.color.deep_orange_a400,20);
		switch(musicService.toggleRepeatState()){
			case 0:addIcon(repeat,Iconify.IconValue.zmdi_repeat,R.color.grey_700,20);break;
			case 1:addIcon(repeat,Iconify.IconValue.zmdi_repeat,R.color.deep_orange_a400,20);break;
			case 2:addIcon(repeat,Iconify.IconValue.zmdi_repeat_one,R.color.deep_orange_a400,20);
		
		}
	}

	private void handlePlayRequest()
	{
		if(mPlaybackState.getState()==mPlaybackState.STATE_PLAYING){
			mTransportControls.pause();
			addIcon(playPause,Iconify.IconValue.zmdi_play,R.color.icons,40);
		}
		else if(mPlaybackState.getState()==mPlaybackState.STATE_PAUSED){
			mTransportControls.play();
			addIcon(playPause,Iconify.IconValue.zmdi_pause,R.color.icons,40);
		}
	}

	
	private void updateSessionToken()
	{
		
	}

	@Override
	public void run()
	{
		Log.w(TAG,"Run");
		if(!isNull() && mMediaPlayer!=null && mMediaPlayer.isPlaying()){
		if(mMediaPlayer.getCurrentPosition()>0){
		Log.w(TAG,"Its running"+mMediaPlayer.getCurrentPosition());
		double timeRemaining = mMediaPlayer.getCurrentPosition();
		long minutes=(int)TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining);
		long seconds=(int)TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining));
		if(seconds<10){
			timePassed.setText(String.format("0%d:0%d",minutes,(int)seconds));      //repeat yourself that again in 100 miliseconds
		}
		else{
			timePassed.setText(String.format("0%d:%d",minutes,(int)seconds));      //repeat yourself that again in 100 miliseconds
		}
		seekBar.setProgress((int)timeRemaining);
	}
	}
		durationHandler.postDelayed(this, 100);
}
	
	public class PlayerFragment extends Fragment{
		Musica musica;
		ImageView albumArt;
		public PlayerFragment(Musica m){
			this.musica=m;
		}
		public String[] getData(){
			return new String[]{musica.nome,musica.artistaNome};
		}
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View v=inflater.inflate(R.layout.player_item,container,false);
			albumArt=(ImageView)v.findViewById(R.id.playeritemImageView);
			if (musica != null) Album.getAlbumArt(musica.albumKey, new Album.OnGetAlbumArtListener(){

						@Override
						public void onResult(Uri i)
						{
							if(i!=null)Picasso.with(getActivity()).load(i)
								.fit().centerCrop().into(albumArt);
							
						}
					});
			return v;
		}
		
	}
	public boolean isNull(){
		return toolbar==null;
	}
	public ImageView findImageView(int res){
		return (ImageView)findViewById(res);
	}
	public void addIcon(ImageView v,Iconify.IconValue e,int colorRes, int size){
		v.setImageDrawable(new IconDrawable(this,e).colorRes(colorRes).sizeDp(size));
	}
}
