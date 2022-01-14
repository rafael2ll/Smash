package com.smash.smash;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.media.*;
import android.support.v4.media.session.*;
import android.support.v4.view.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.mindorks.butterknifelite.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.smash.*;
import com.smash.smash.Adapters.*;
import com.smash.smash.Helpers.*;
import com.smash.smash.Models.*;
import com.squareup.picasso.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

public class PlayerActivity extends AppCompatActivity implements Runnable, View.OnClickListener, SeekBar.OnSeekBarChangeListener
{
	@BindView(R.id.playermainImageViewBack) ImageView back;
	@BindView(R.id.playermainImageViewAdd) ImageView add;    @BindView(R.id.playermainImageMore) ImageView more;
	@BindView(R.id.playermainImageViewPrev) ImageView prev;  @BindView(R.id.playermainImageViewNext) ImageView next;
	@BindView(R.id.playermainImageViewPlayPause) ImageView playPause; @BindView(R.id.playermainImageViewRepeat) ImageView repeat;
	@BindView(R.id.playermainImageViewShuffle) ImageView shuffle;     @BindView(R.id.playermainTextViewPassed) TextView timePassed;
	@BindView(R.id.playermainTextViewNome) TextView nome;    @BindView(R.id.playermainTextViewArtista) TextView artista; 
	@BindView(R.id.playermainTextView1) TextView timeTotal;  @BindView(R.id.playermainViewPager)ViewPager mViewPager;
	@BindView(R.id.playermainSeekBarCompat) SeekBar seekBar; @BindView(R.id.toolbar) Toolbar toolbar;
	
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
	boolean isInMyMusicas=false;
	MusicService musicService;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_main);
		ButterKnifeLite.bind(this);
		setSupportActionBar(toolbar);
		
		setTaskBarColored(this);
		
		add.setOnClickListener(this);playPause.setOnClickListener(this);
		more.setOnClickListener(this);repeat.setOnClickListener(this);
		prev.setOnClickListener(this);shuffle.setOnClickListener(this);
		next.setOnClickListener(this);seekBar.setOnSeekBarChangeListener(this);
		
		nome.setSelected(true);
		artista.setSelected(true);
		
		switch(MusicMasterHandler.getRepeatState(this)){
			case 0:addIcon(repeat, R.drawable.ic_repeat_off);break;
			case 1:addIcon(repeat, R.drawable.ic_repeat);break;
			case 2:addIcon(repeat, R.drawable.ic_repeat_once);
				
		}
		if(MusicMasterHandler.isShuffling(this))addIcon(shuffle, R.drawable.ic_shuffle);
		else addIcon(shuffle, R.drawable.ic_shuffle_off);

		try
		{
			musicService=MusicService.getItself();
			mMediaPlayer=MusicService.getMP();
			mSession=musicService.getSession();
			loadCallback();
			
			adapter=new PlayerAdapter(getSupportFragmentManager());
			List<PlayerFragment> fragList=new ArrayList<>();
			for(Musica a :MusicMasterHandler.getToPlay(this)){
				fragList.add(new PlayerFragment(a));
			}
			adapter.setFrags(fragList);
			
			mViewPager.setAdapter(adapter);
			mViewPager.setClipToPadding(false);
			mViewPager.setPadding(10,0,10,0);
			mViewPager.setPageMargin(10);
			
			mediaController = new MediaControllerCompat(this, musicService.getSessionToken());
			mediaController.registerCallback(mCallback);
			mPlaybackState= mediaController.getPlaybackState();
			mTransportControls=mediaController.getTransportControls();
			updateMetadata();
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
			addIcon(playPause, R.drawable.ic_pause_circle_outline);
		}
		else if(mPlaybackState.getState()==mPlaybackState.STATE_PAUSED){
			addIcon(playPause, R.drawable.ic_play_circle_outline);
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
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			
			nome.setText(description.getTitle());
			artista.setText(description.getSubtitle());
						
			final List<Transformation> transformList=new ArrayList<>();
			transformList.add(new BlurTransform(this));
			transformList.add(new ColorFilterTransformation(getResources().getColor(R.color.divider )));
		
			Album.getAlbumArt(mMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI), new Album.OnGetAlbumArtListener(){

					@Override
					public void onResult(Uri i)
					{
						if(i!=null)Picasso.with(PlayerActivity.this).load(i).fit()
								.centerCrop().transform(transformList).into(back);
					}
				});
			Musica m = MusicMasterHandler.getToPlay(this).get(musicService.getPlayingPosition());
			User.containsMusica(m.key, new User.OnContainsListener(){

					@Override
					public void isContained(boolean b)
					{
						isInMyMusicas = b;
						if(b) add.setImageResource(R.drawable.ic_close);
						else  add.setImageResource(R.drawable.ic_plus);
					}
				});
			double timeTotal= mMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
			long minutes=(int)TimeUnit.MILLISECONDS.toMinutes((long) timeTotal);
			long seconds=(int)TimeUnit.MILLISECONDS.toSeconds((long) timeTotal) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeTotal));

			if(seconds<10) this.timeTotal.setText(String.format("0%d:0%d",minutes,(int)seconds));
			else this.timeTotal.setText(String.format("0%d:%d",minutes,(int)seconds));
			seekBar.setMax((int)timeTotal);
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
		Toast.makeText(this, isInMyMusicas+"", 2).show();
		Musica m = MusicMasterHandler.getToPlay(this).get(musicService.getPlayingPosition());

		if(isInMyMusicas){
			User.removeMusica(m.key);
			add.setImageResource(R.drawable.ic_plus);
		}else{
			User.addMusica(m);
			add.setImageResource(R.drawable.ic_close);
		}
		isInMyMusicas= !isInMyMusicas;
	}

	private void handleMoreRequest()
	{
		Musica m = MusicMasterHandler.getToPlay(this).get(musicService.getPlayingPosition());
		File f= new File(getExternalCacheDir().getAbsolutePath()+"/"+m.key);
		
		DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		//imageUri is a valid Uri
		DownloadManager.Request downloadRequest= new DownloadManager.Request(Uri.parse(m.musicaUri)); 
		//without this line, it works
		downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
		//subpath is valid
		downloadRequest.setDestinationUri(Uri.fromFile(f));
		downloadManager.enqueue(downloadRequest);
	}

	private void handleShuffleRequest()
	{
		boolean state=musicService.shuffle();
		if(state)addIcon(shuffle, R.drawable.ic_shuffle);
		else addIcon(shuffle, R.drawable.ic_shuffle_off);

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
		
		switch(musicService.toggleRepeatState()){
			case 0:addIcon(repeat, R.drawable.ic_repeat_off);break;
			case 1:addIcon(repeat, R.drawable.ic_repeat);break;
			case 2:addIcon(repeat, R.drawable.ic_repeat_once);

		}
	}

	private void handlePlayRequest()
	{
		if(mPlaybackState.getState()==mPlaybackState.STATE_PLAYING){
			mTransportControls.pause();
			addIcon(playPause, R.drawable.ic_play_circle_outline);
		}
		else if(mPlaybackState.getState()==mPlaybackState.STATE_PAUSED){
			mTransportControls.play();
			addIcon(playPause, R.drawable.ic_pause_circle_outline);
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

	public static class PlayerFragment extends Fragment{
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
	public void addIcon(ImageView v, int res){
		v.setImageResource(res);
	}
	
	public static void setTaskBarColored(AppCompatActivity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            Window w = context.getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
            int statusBarHeight = getStatusBarHeight(context);

            View view = new View(context);
            view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.getLayoutParams().height = statusBarHeight;
            ((ViewGroup) w.getDecorView()).addView(view);
            view.setBackgroundColor(context.getResources().getColor(R.color.divider));
        }
    }

	public static int getActionBarHeight(AppCompatActivity a) {
		int actionBarHeight = 0;
		TypedValue tv = new TypedValue();
		if (a.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
		{
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,a.getResources().getDisplayMetrics());
		}
		return actionBarHeight;
	}

	public static int getStatusBarHeight(AppCompatActivity a) {
		int result = 0;
		int resourceId = a.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = a.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
	
}
