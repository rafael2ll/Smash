package com.smash.player.Helpers;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.media.MediaPlayer.*;
import android.media.session.*;
import android.net.*;
import android.net.wifi.*;
import android.net.wifi.WifiManager.*;
import android.os.*;
import android.support.v4.media.*;
import android.support.v4.media.session.*;
import android.util.*;
import android.widget.*;
import com.smash.player.*;
import com.smash.player.Helpers.*;
import com.smash.player.Models.*;
import com.squareup.picasso.*;
import java.util.*;

import com.smash.player.R;
import org.json.*;


public class MusicService extends Service implements OnPreparedListener,
OnCompletionListener, OnErrorListener, AudioManager.OnAudioFocusChangeListener
{
	public static final String ACTION_PAUSE = "com.smash.up.pause";
    public static final String ACTION_PLAY = "com.smash.up.play";
    public static final String ACTION_PREV = "com.smash.up.prev";
    public static final String ACTION_NEXT = "com.smash.up.next";
	public static final String ACTION_STOP = "com.smash.up.stop";
	public static final String ACTION_START= "com.smash.up.start";

    private static final String TAG = "MusicService";
    // Action to thumbs up a media item
    private static final String CUSTOM_ACTION_THUMBS_UP = "thumbs_up";
    // Delay stopSelf by using a handler.
    private static final int STOP_DELAY = 30000;
    // The volume we set the media player to when we lose audio focus, but are
    // allowed to reduce the volume instead of stopping playback.
    public static final float VOLUME_DUCK = 0.2f;
    // The volume we set the media player when we have audio focus.
    public static final float VOLUME_NORMAL = 1.0f;
    public static final String ANDROID_AUTO_PACKAGE_NAME = "com.google.android.projection.gearhead";
    public static final String ANDROID_AUTO_EMULATOR_PACKAGE_NAME = "com.example.android.media";
    // Music catalog manager

	private static MediaSessionCompat mSession;
    public static MediaPlayer mMediaPlayer;
    // "Now playing" queue:u
    public List<Musica> mPlayingMusicas;
	public List<Musica> unshuffableList;
    public int mCurrentPlayingPos=0;
    // Current local media player state
	public static int repeatState;
    private int mState = PlaybackState.STATE_NONE;
    // Wifi lock that we hold when streaming files from the internet, in order
    // to prevent the device from shutting off the Wifi radio
    private WifiLock mWifiLock;
    private MediaNotification mMediaNotification;
    // Indicates whether the service was started.
    private boolean mServiceStarted;
	public static  MusicService mService;

	public static MediaSessionCompat getService()
	{
		return mSession;
	}
    enum AudioFocus
	{
        NoFocusNoDuck, // we don't have audio focus, and can't duck
        NoFocusCanDuck, // we don't have focus, but can play at a low volume
		// ("ducking")
        Focused // we have full audio focus
		}
    // Type of audio focus we have:
    private AudioFocus mAudioFocus = AudioFocus.NoFocusNoDuck;
    private AudioManager mAudioManager;
    // Indicates if we should start playing immediately after we gain focus.
    private boolean mPlayOnFocusGain;
    private Handler mDelayedStopHandler = new Handler() {
        @Override
        public void handleMessage(Message msg)
		{
            if ((mMediaPlayer != null && mMediaPlayer.isPlaying()) ||
				mPlayOnFocusGain)
			{
                Log.d(TAG, "Ignoring delayed stop since the media player is in use.");
                return;
            }
            Log.d(TAG, "Stopping service with delay handler.");
            stopSelf();
            mServiceStarted = false;
        }
    };

	@Override
	public IBinder onBind(Intent p1)
	{
		return null;
	}

    @Override
    public void onCreate()
	{
        super.onCreate();
		mService = this;
        Log.d(TAG, "onCreate");
        mPlayingMusicas = new ArrayList<>();
        // Create the Wifi lock (this does not acquire the lock, this just creates it)
        mWifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
			.createWifiLock(WifiManager.WIFI_MODE_FULL, "MusicDemo_lock");
        // Create the music catalog metadata provider
 		ComponentName mMediaButton=new ComponentName(this, RemoteControlReceiver.class);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // Start a new MediaSession
        mSession = new MediaSessionCompat(this, TAG, mMediaButton, null);
        //setSessionToken(mSession.getSessionToken());
		mSession.setSessionActivity(PendingIntent.getActivity(this, 0, new Intent(this,PlayerActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
        mSession.setCallback(new MediaSessionCallback());
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
						  MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        // Use these extras to reserve space for the corresponding actions, even when they are disabled
        // in the playbackstate, so the custom actions don't reflow.
        Bundle extras = new Bundle();
        extras.putBoolean("com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_NEXT", true);
        extras.putBoolean("com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_PREVIOUS", true);
        mSession.setExtras(extras);
        //updatePlaybackState(null);
        mMediaNotification = new MediaNotification(this);
    }

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.w(TAG, "onStart");
		if (intent.getAction() != null)
			switch (intent.getAction())
			{
				case ACTION_START:
					mServiceStarted = true;
					repeatState = MusicProvider.getRepeatState(this);
					unshuffableList = MusicProvider.getToPlay();
					mPlayingMusicas = MusicProvider.getToPlay();break;
				case ACTION_PLAY:mCurrentPlayingPos = MusicProvider.getToPlayPosition();
					handlePlayRequest();
					//MyApplication.getContext().startActivity(new Intent(this,PlayerActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
					break;
				case ACTION_PAUSE:handlePauseRequest();break;
				case ACTION_PREV:
				case ACTION_NEXT:
				case ACTION_STOP:
			}
		return START_NOT_STICKY;
	}

    /*
     * (non-Javadoc)
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy()
	{
        Log.d(TAG, "onDestroy");
        // Service is being killed, so make sure we release our resources
        handleStopRequest(null);
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        // In particular, always release the MediaSession to clean up resources
        // and notify associated MediaController(s).
        mSession.release();
    }
	public static MediaPlayer getMP()
	{
		return mMediaPlayer;
	}
	public static MusicService getItself()
	{
		return mService;
	}

	public MediaSessionCompat.Token getSessionToken()
	{
		return mSession.getSessionToken();
	}
	public int getPlayingPosition()
	{
		return mCurrentPlayingPos;
	}
	public int toggleRepeatState()
	{
		repeatState = MusicProvider.toggleRepeatState(this);
		if (repeatState != 2)mMediaPlayer.setLooping(false);
		else if (repeatState == 2)mMediaPlayer.setLooping(true);
		return repeatState;
	}
	public boolean shuffle()
	{

		boolean shuffleState=MusicProvider.toggleShuffling(this);
		Musica m=mPlayingMusicas.get(mCurrentPlayingPos);
		if (shuffleState)
		{
			Collections.shuffle(MusicProvider.getToPlay());
			mPlayingMusicas = MusicProvider.getToPlay();
		}
		else
		{
			mPlayingMusicas = unshuffableList;
			MusicProvider.setToPlay(this, unshuffableList);
		}
		mCurrentPlayingPos = mPlayingMusicas.indexOf(m);
		return shuffleState;

	}
	// *********  MediaSession.Callback implementation:
    private final class MediaSessionCallback extends MediaSessionCompat.Callback
	{
        @Override
        public void onPlay()
		{
            Log.d(TAG, "play");
            if (mPlayingMusicas == null || mPlayingMusicas.isEmpty())
			{
				mCurrentPlayingPos = 0;
            }
            if (mPlayingMusicas != null && !mPlayingMusicas.isEmpty())
			{
                handlePlayRequest();
            }
        }
        @Override
        public void onSkipToQueueItem(long queueId)
		{
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras)
		{}

		@Override
        public void onPause()
		{
            Log.d(TAG, "pause. current state=" + mState);
            handlePauseRequest();
        }
        @Override
        public void onStop()
		{
            Log.d(TAG, "stop. current state=" + mState);
            handleStopRequest(null);
        }
        @Override
        public void onSkipToNext()
		{
            Log.d(TAG, "skipToNext");
            mCurrentPlayingPos++;
            if (mPlayingMusicas != null && mCurrentPlayingPos >= mPlayingMusicas.size())
			{
                mCurrentPlayingPos = 0;
            }
			handlePlayRequest();
        }
        @Override
        public void onSkipToPrevious()
		{
            Log.d(TAG, "skipToPrevious");
            mCurrentPlayingPos--;
            if (mPlayingMusicas != null && mCurrentPlayingPos < 0)
			{
                // This sample's behavior: skipping to previous when in first song restarts the
                // first song.
                mCurrentPlayingPos = 0;
            }
			handlePlayRequest();
        }
        @Override
        public void onCustomAction(String action, Bundle extras)
		{

        }
        @Override
        public void onPlayFromSearch(String query, Bundle extras)
		{

        }
    }
    // *********  MediaPlayer listeners:
    /*
     * Called when media player is done playing current song.
     * @see android.media.MediaPlayer.OnCompletionListener
     */
    @Override
    public void onCompletion(MediaPlayer player)
	{
        Log.d(TAG, "onCompletion from MediaPlayer");
        // The media player finished playing the current song, so we go ahead
        // and start the next.
        if (mPlayingMusicas != null && !mPlayingMusicas.isEmpty())
		{
            // In this sample, we restart the playing queue when it gets to the end:
            mCurrentPlayingPos++;
            if (mCurrentPlayingPos >= mPlayingMusicas.size())
			{
                mCurrentPlayingPos = 0;
            }
            handlePlayRequest();
        }
		else
		{
            // If there is nothing to play, we stop and release the resources:
            handleStopRequest(null);
        }
    }
    /*
     * Called when media player is done preparing.
     * @see android.media.MediaPlayer.OnPreparedListener
     */
    @Override
    public void onPrepared(MediaPlayer player)
	{
        Log.d(TAG, "onPrepared from MediaPlayer");
        // The media player is done preparing. That means we can start playing if we
        // have audio focus.
        configMediaPlayerState();
		updateMetadata();
		updatePlaybackState(null);
    }
    /**
     * Called when there's an error playing media. When this happens, the media
     * player goes to the Error state. We warn the user about the error and
     * reset the media player.
     *
     * @see android.media.MediaPlayer.OnErrorListener
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra)
	{
        Log.e(TAG, "Media player error: what=" + what + ", extra=" + extra);
        handleStopRequest("MediaPlayer error " + what + " (" + extra + ")");
        return true; // true indicates we handled the error
    }
    // *********  OnAudioFocusChangeListener listener:
    /**
     * Called by AudioManager on audio focus changes.
     */
    @Override
    public void onAudioFocusChange(int focusChange)
	{
        Log.d(TAG, "onAudioFocusChange. focusChange=" + focusChange);
        if (focusChange == AudioManager.AUDIOFOCUS_GAIN)
		{
            // We have gained focus:
            mAudioFocus = AudioFocus.Focused;
        }
		else if (focusChange == AudioManager.AUDIOFOCUS_LOSS ||
				 focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
				 focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK)
		{
            // We have lost focus. If we can duck (low playback volume), we can keep playing.
            // Otherwise, we need to pause the playback.
            boolean canDuck = focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
            mAudioFocus = canDuck ? AudioFocus.NoFocusCanDuck : AudioFocus.NoFocusNoDuck;
            // If we are playing, we need to reset media player by calling configMediaPlayerState
            // with mAudioFocus properly set.
            if (mState == PlaybackState.STATE_PLAYING && !canDuck)
			{
                // If we don't have audio focus and can't duck, we save the information that
                // we were playing, so that we can resume playback once we get the focus back.
                mPlayOnFocusGain = true;
            }
        }
		else
		{
            Log.e(TAG, "onAudioFocusChange: Ignoring unsupported focusChange: " + focusChange);
        }
        configMediaPlayerState();
    }
    // *********  private methods:
    /**
     * Handle a request to play music
     */
    private void handlePlayRequest()
	{
        Log.d(TAG, "handlePlayRequest: mState=" + mState);
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        if (!mServiceStarted)
		{
            Log.v(TAG, "Starting service");
            // The MusicService needs to keep running even after the calling MediaBrowser
            // is disconnected. Call startService(Intent) and then stopSelf(..) when we no longer
            // need to play media.
            startService(new Intent(getApplicationContext(), MusicService.class));
            mServiceStarted = true;
        }
        mPlayOnFocusGain = true;
        tryToGetAudioFocus();
        if (!mSession.isActive())
		{
            mSession.setActive(true);
        }
        // actually play the song
        if (mState == PlaybackState.STATE_PAUSED)
		{
            // If we're paused, just continue playback and restore the
            // 'foreground service' state.
            configMediaPlayerState();
        }
		else
		{
            // If we're stopped or playing a song,
            // just go ahead to the new song and (re)start playing
            playCurrentSong();
        }
    }
    /**
     * Handle a request to pause music
     */
    private void handlePauseRequest()
	{
        Log.d(TAG, "handlePauseRequest: mState=" + mState);
        if (mState == PlaybackState.STATE_PLAYING)
		{
            // Pause media player and cancel the 'foreground service' state.
            mState = PlaybackState.STATE_PAUSED;
            if (mMediaPlayer.isPlaying())
			{
                mMediaPlayer.pause();
            }
            // while paused, retain the MediaPlayer but give up audio focus
            relaxResources(false);
            giveUpAudioFocus();
        }
        updatePlaybackState(null);
    }
    /**
     * Handle a request to stop music
     */
    private void handleStopRequest(String withError)
	{
        Log.d(TAG, "handleStopRequest: mState=" + mState + " error=" + withError);
        mState = PlaybackState.STATE_STOPPED;
        // let go of all resources...
        relaxResources(true);
        giveUpAudioFocus();
        updatePlaybackState(withError);
		mMediaNotification.stopNotification();
        // service is no longer necessary. Will be started again if needed.
        stopSelf();
        mServiceStarted = false;
    }
    /**
     * Releases resources used by the service for playback. This includes the
     * "foreground service" status, the wake locks and possibly the MediaPlayer.
     *
     * @param releaseMediaPlayer Indicates whether the Media Player should also
     *            be released or not
     */
    private void relaxResources(boolean releaseMediaPlayer)
	{
        Log.d(TAG, "relaxResources. releaseMediaPlayer=" + releaseMediaPlayer);
        // stop being a foreground service
        stopForeground(true);
        // reset the delayed stop handler.
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
        // stop and release the Media Player, if it's available
        if (releaseMediaPlayer && mMediaPlayer != null)
		{
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        // we can also release the Wifi lock, if we're holding it
        if (mWifiLock.isHeld())
		{
            mWifiLock.release();
        }
    }
    /**
     * Reconfigures MediaPlayer according to audio focus settings and
     * starts/restarts it. This method starts/restarts the MediaPlayer
     * respecting the current audio focus state. So if we have focus, it will
     * play normally; if we don't have focus, it will either leave the
     * MediaPlayer paused or set it to a low volume, depending on what is
     * allowed by the current focus settings. This method assumes mPlayer !=
     * null, so if you are calling it, you have to do so from a context where
     * you are sure this is the case.
     */
    private void configMediaPlayerState()
	{
        Log.d(TAG, "configAndStartMediaPlayer. mAudioFocus=" + mAudioFocus);
        if (mAudioFocus == AudioFocus.NoFocusNoDuck)
		{
            // If we don't have audio focus and can't duck, we have to pause,
            if (mState == PlaybackState.STATE_PLAYING)
			{
                handlePauseRequest();
            }
        }
		else
		{  // we have audio focus:
            if (mAudioFocus == AudioFocus.NoFocusCanDuck)
			{
                mMediaPlayer.setVolume(VOLUME_DUCK, VOLUME_DUCK); // we'll be relatively quiet
            }
			else
			{
                mMediaPlayer.setVolume(VOLUME_NORMAL, VOLUME_NORMAL); // we can be loud again
            }
            // If we were playing when we lost focus, we need to resume playing.
            if (mPlayOnFocusGain)
			{
                if (!mMediaPlayer.isPlaying())
				{
                    Log.d(TAG, "configAndStartMediaPlayer startMediaPlayer.");
                    mMediaPlayer.start();
                }
                mPlayOnFocusGain = false;
                mState = PlaybackState.STATE_PLAYING;
            }
        }
        updatePlaybackState(null);
    }
    /**
     * Makes sure the media player exists and has been reset. This will create
     * the media player if needed, or reset the existing media player if one
     * already exists.
     */
    private void createMediaPlayerIfNeeded()
	{
        Log.d(TAG, "createMediaPlayerIfNeeded. needed? " + (mMediaPlayer == null));
        if (mMediaPlayer == null)
		{
            mMediaPlayer = new MediaPlayer();
            // Make sure the media player will acquire a wake-lock while
            // playing. If we don't do that, the CPU might go to sleep while the
            // song is playing, causing playback to stop.
            mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            // we want the media player to notify us when it's ready preparing,
            // and when it's done playing:
            mMediaPlayer.setOnPreparedListener(this);
			if (repeatState == 2)mMediaPlayer.setLooping(true);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
        }
		else
		{
            mMediaPlayer.reset();
        }
    }
    /**
     * Starts playing the current song in the playing queue.
     */
    void playCurrentSong()
	{
        mState = PlaybackState.STATE_STOPPED;
        relaxResources(false); // release everything except MediaPlayer

		createMediaPlayerIfNeeded();
		mState = PlaybackState.STATE_BUFFERING;
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		Uri trackUri = ContentUris.withAppendedId(
		android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,mPlayingMusicas.get(mCurrentPlayingPos).getId());
		try{
		mMediaPlayer.setDataSource(this,trackUri);
		mMediaPlayer.prepareAsync();
		updatePlaybackState(null);
		mWifiLock.acquire();
		}catch(Exception e){}
	}
    private void updateMetadata()
	{
		final Musica m=mPlayingMusicas.get(mCurrentPlayingPos);
		
		final MediaMetadataCompat.Builder metadataBuilder=new MediaMetadataCompat.Builder()
			.putString(MediaMetadataCompat.METADATA_KEY_TITLE, m.getNome())
			.putLong("POSITION", mCurrentPlayingPos)
			.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mMediaPlayer.getDuration())
			.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, m.getArtista())
			.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,m.getAlbumArt(this))
			.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, Album.getCoverArtPath(m.getAlbumId(),this))
			.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, m.getAlbum());
		mSession.setMetadata(metadataBuilder.build());
    }
    /**
     * Update the current media player state, optionally showing an error message.
     *PlayerActivity.java:183
     * @param error if not null, error message to present to the user.
     *
     */
    private void updatePlaybackState(String error)
	{
        Log.d(TAG, "updatePlaybackState, setting session playback state to " + mState);
        long position = PlaybackState.PLAYBACK_POSITION_UNKNOWN;
        if (mMediaPlayer != null && mMediaPlayer.isPlaying())
		{
            position = mMediaPlayer.getCurrentPosition();
        }
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
			.setActions(getAvailableActions());
        setCustomAction(stateBuilder);
        // If there is an error message, send it to the playback state:
        if (error != null)
		{
            // Error states are really only supposed to be used for errors that cause playback to
            // stop unexpectedly and persist until the user takes action to fix it.
            stateBuilder.setErrorMessage(error);
            mState = PlaybackState.STATE_ERROR;
        }
        stateBuilder.setState(mState, position, 1.0f, SystemClock.elapsedRealtime());
        mSession.setPlaybackState(stateBuilder.build());
        if (mState == PlaybackState.STATE_PLAYING || mState == PlaybackState.STATE_PAUSED)
		{
			mMediaNotification.startNotification();
        }
    }
    private void setCustomAction(PlaybackStateCompat.Builder stateBuilder)
	{
        if (mPlayingMusicas != null)
		{
			int favoriteIcon = R.drawable.ic_launcher;
            /*if (User.isFavorite(mPlayingMusicas.get(mCurrentPlayingPos)))
			{
                favoriteIcon = R.drawable.ic_launcher;
            }*/
			stateBuilder.addCustomAction(CUSTOM_ACTION_THUMBS_UP, "Set as Favorite",
										 favoriteIcon);
        }
    }
    private long getAvailableActions()
	{
        long actions = PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_FROM_MEDIA_ID |
			PlaybackState.ACTION_PLAY_FROM_SEARCH;
        if (mPlayingMusicas == null || mPlayingMusicas.isEmpty())
		{
            return actions;
        }
        if (mState == PlaybackState.STATE_PLAYING)
		{
            actions |= PlaybackState.ACTION_PAUSE;
        }
        if (mCurrentPlayingPos > 0)
		{
            actions |= PlaybackState.ACTION_SKIP_TO_PREVIOUS;
        }
        if (mCurrentPlayingPos < mPlayingMusicas.size() - 1)
		{
            actions |= PlaybackState.ACTION_SKIP_TO_NEXT;
        }
        return actions;
    }

    /**
     * Try to get the system audio focus.
     */
    void tryToGetAudioFocus()
	{
        Log.d(TAG, "tryToGetAudioFocus");
        if (mAudioFocus != AudioFocus.Focused)
		{
            int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
														 AudioManager.AUDIOFOCUS_GAIN);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
			{
                mAudioFocus = AudioFocus.Focused;
            }
        }
    }
    /**
     * Give up the audio focus.
     */
    void giveUpAudioFocus()
	{
        Log.d(TAG, "giveUpAudioFocus");
        if (mAudioFocus == AudioFocus.Focused)
		{
            if (mAudioManager.abandonAudioFocus(this) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
			{
                mAudioFocus = AudioFocus.NoFocusNoDuck;
            }
        }
    }
}
