package com.smash.player.Helpers;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.graphics.*;
import android.media.*;
import android.media.session.*;
import android.os.*;
import android.support.v4.media.session.*;
import android.support.v7.app.NotificationCompat;
import android.support.v4.util.*;
import android.util.*;
import com.smash.player.*;
import java.io.*;
import android.support.v4.util.LruCache;
import android.support.v4.media.*;
import android.support.v4.app.*;
import com.squareup.picasso.*;
import android.net.*;
import com.squareup.picasso.Picasso.*;
import android.graphics.drawable.*;
import com.smash.player.Models.*;

public class MediaNotification extends BroadcastReceiver {
    private static final String TAG = "MediaNotification";
    private static final int NOTIFICATION_ID = 412;
    public static final String ACTION_PAUSE = "com.smash.player.pause";
    public static final String ACTION_PLAY = "com.smash.player.play";
    public static final String ACTION_PREV = "com.smash.player.prev";
    public static final String ACTION_NEXT = "com.smash.player.next";
    public static final String ACTION_STOP = "com.smash.player.stop";
	public static final String ACTION_ADD="com.smash.player.add";
    private final MusicService mService;
    private MediaSessionCompat.Token mSessionToken;
    private MediaControllerCompat mController;
    private MediaControllerCompat.TransportControls mTransportControls;
    private final SparseArray<PendingIntent> mIntents = new SparseArray<PendingIntent>();
    private PlaybackStateCompat mPlaybackState;
    private MediaMetadataCompat mMetadata;
    private NotificationCompat.Builder mNotificationBuilder;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Action mPlayPauseAction;
    private String mCurrentAlbumArt;
    private int mNotificationColor;
    private boolean mStarted = false;
    public MediaNotification(MusicService service) {
        mService = service;
        updateSessionToken();

        mNotificationColor = mService.getResources().getColor(R.color.grey_900);
        mNotificationManager = (NotificationManager) mService
			.getSystemService(Context.NOTIFICATION_SERVICE);
        String pkg = mService.getPackageName();
		mIntents.put(R.drawable.bs_ic_more,PendingIntent.getBroadcast(mService, 100,
																	  new Intent(ACTION_ADD).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT));
		mIntents.put(R.drawable.window_close,PendingIntent.getBroadcast(mService
																		, 100,new Intent(ACTION_STOP).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT));
        mIntents.put(R.drawable.ic_pause_circle_outline, PendingIntent.getBroadcast(mService, 100,
																  new Intent(ACTION_PAUSE).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT));
        mIntents.put(R.drawable.ic_play_circle_outline, PendingIntent.getBroadcast(mService, 100,
																 new Intent(ACTION_PLAY).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT));
        mIntents.put(R.drawable.ic_skip_previous, PendingIntent.getBroadcast(mService, 100,
																		  new Intent(ACTION_PREV).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT));
        mIntents.put(R.drawable.ic_skip_next, PendingIntent.getBroadcast(mService, 100,
																	  new Intent(ACTION_NEXT).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT));
    }

	

    public void startNotification() {
        if (!mStarted) {
            mController.registerCallback(mCb);
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_NEXT);
            filter.addAction(ACTION_PAUSE);
            filter.addAction(ACTION_PLAY);
            filter.addAction(ACTION_PREV);
			filter.addAction(ACTION_STOP);
			filter.addAction(ACTION_ADD);
            mService.registerReceiver(this, filter);
            mMetadata = mController.getMetadata();
            mPlaybackState = mController.getPlaybackState();
            mStarted = true;
            // The notification must be updated after setting started to true
            updateNotificationMetadata();
        }
    }
    /**
     * Removes the notification and stops tracking the session. If the session
     * was destroyed this has no effect.
     */
    public void stopNotification() {
        mStarted = false;
        mController.unregisterCallback(mCb);
        try {
            mService.unregisterReceiver(this);
        } catch (IllegalArgumentException ex) {
            // ignore if the receiver is not registered.
        }
        mService.stopForeground(true);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.d(TAG, "Received intent with action " + action);
        if (ACTION_PAUSE.equals(action)) {
            mTransportControls.pause();
        } else if (ACTION_PLAY.equals(action)) {
            mTransportControls.play();
        } else if (ACTION_NEXT.equals(action)) {
            mTransportControls.skipToNext();
        } else if (ACTION_PREV.equals(action)) {
            mTransportControls.skipToPrevious();
        } else if (ACTION_STOP.equals(action)){
			mTransportControls.stop();
			mService.stopForeground(true);
			mService.stopSelf();
		}
    }
    /**
     * Update the state based on a change on the session token. Called either when
     * we are running for the first time or when the media session owner has destroyed the session
     * (see {@link android.media.session.MediaController.Callback#onSessionDestroyed()})
     */
    private void updateSessionToken() {
        MediaSessionCompat.Token freshToken = mService.getSessionToken();
        if (mSessionToken == null || !mSessionToken.equals(freshToken)) {
            if (mController != null) {
                mController.unregisterCallback(mCb);
            }
            mSessionToken = freshToken;
            try
			{
				mController = new MediaControllerCompat(mService, mSessionToken);
			}
			catch (RemoteException e)
			{}
            mTransportControls = mController.getTransportControls();
            if (mStarted) {
                mController.registerCallback(mCb);
            }
        }
    }
    private final MediaControllerCompat.Callback mCb = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            mPlaybackState = state;
            Log.d(TAG, "Received new playback state"+ state);
            updateNotificationPlaybackState();
        }
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            mMetadata = metadata;
            Log.d(TAG, "Received new metadata "+ metadata);
            updateNotificationMetadata();
        }
        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
            Log.d(TAG, "Session was destroyed, resetting to the new session token");
            updateSessionToken();
        }
    };
    private void updateNotificationMetadata() {
        Log.d(TAG, "updateNotificationMetadata. mMetadata=" + mMetadata);
        if (mMetadata == null || mPlaybackState == null) {
            return;
        }
        updatePlayPauseAction();
        mNotificationBuilder = new NotificationCompat.Builder(mService);
        int playPauseActionIndex = 0;
		// If skip to previous action is enabled
        if ((mPlaybackState.getActions() & PlaybackState.ACTION_SKIP_TO_PREVIOUS) != 0) {
            mNotificationBuilder
				.addAction(R.drawable.ic_skip_previous,
						   "Previous",
						   mIntents.get(R.drawable.ic_skip_previous));
            playPauseActionIndex = 1;
        }
        mNotificationBuilder.addAction(mPlayPauseAction);
        // If skip to next action is enabled
        if ((mPlaybackState.getActions() & PlaybackState.ACTION_SKIP_TO_NEXT) != 0) {
            mNotificationBuilder.addAction(R.drawable.ic_skip_next,
										   "Next",
										   mIntents.get(R.drawable.ic_skip_next));
        }
		Bitmap bitmap = mMetadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART);
		if(bitmap==null){
			bitmap=BitmapFactory.decodeResource(mService.getResources(),R.drawable.image_back);
		}
        String bitmapLink= mMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI);
        MediaDescriptionCompat description=mMetadata.getDescription();
        mNotificationBuilder
			.setStyle(new NotificationCompat.MediaStyle()
					  .setShowActionsInCompactView(playPauseActionIndex,playPauseActionIndex+1)  // only show play/pause in compact view
					  .setMediaSession(mSessionToken)
					  .setShowCancelButton(true)
					  .setCancelButtonIntent(mIntents.get(R.drawable.window_close)))
			.setColor(mNotificationColor)
			.setContentIntent(PendingIntent.getActivity(mService, 0, new Intent(mService, PlayerActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
			.setSmallIcon(R.drawable.play)
			.setVisibility(Notification.VISIBILITY_PUBLIC)
			.setOngoing(true)
			.setUsesChronometer(true)
			.setContentTitle(description.getTitle())
			.setContentText(description.getSubtitle())
			.setLargeIcon(bitmap.copy(bitmap.getConfig(),true));
        updateNotificationPlaybackState();
		
        mService.startForeground(NOTIFICATION_ID, mNotificationBuilder.build());
    }
    private void updatePlayPauseAction() {
        Log.d(TAG, "updatePlayPauseAction");
        String playPauseLabel = "";
        int playPauseIcon;
        if (mPlaybackState.getState() == PlaybackState.STATE_PLAYING) {
            playPauseLabel = "Pause";
            playPauseIcon = R.drawable.ic_pause_circle_outline;
        } else {
            playPauseLabel = "Play";
            playPauseIcon = R.drawable.ic_play_circle_outline;
        }
        if (mPlayPauseAction == null) {
            mPlayPauseAction = new NotificationCompat.Action(playPauseIcon, playPauseLabel,
															 mIntents.get(playPauseIcon));
        } else {
            mPlayPauseAction.icon = playPauseIcon;
            mPlayPauseAction.title = playPauseLabel;
            mPlayPauseAction.actionIntent = mIntents.get(playPauseIcon);
        }
    }
    private void updateNotificationPlaybackState() {
        Log.d(TAG, "updateNotificationPlaybackState. mPlaybackState=" + mPlaybackState);
        if (mPlaybackState == null || !mStarted) {
            Log.d(TAG, "updateNotificationPlaybackState. cancelling notification!");
            mService.stopForeground(true);
            return;
        }
        if (mNotificationBuilder == null) {
            Log.d(TAG, "updateNotificationPlaybackState. there is no notificationBuilder. Ignoring request to update state!");
            return;
        }
        if (mPlaybackState.getPosition() >= 0) {
			Log.d(TAG,"position is"+mPlaybackState.getPosition());
            Log.d(TAG, "updateNotificationPlaybackState. updating playback position to "+(System.currentTimeMillis() - mPlaybackState.getPosition()) / 1000 + " seconds");
            mNotificationBuilder
				.setWhen(System.currentTimeMillis() - mPlaybackState.getPosition())
				.setShowWhen(true)
				.setUsesChronometer(true);
            mNotificationBuilder.setShowWhen(true);
        } else {
            Log.d(TAG, "updateNotificationPlaybackState. hiding playback position");
            mNotificationBuilder
				.setWhen(0)
				.setShowWhen(false)
				.setUsesChronometer(false);
        }
        updatePlayPauseAction();
        mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
    }
}
