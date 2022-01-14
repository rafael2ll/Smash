package com.smash.up.Itens;

import android.content.*;
import android.view.*;
import android.support.v4.media.session.*;
import android.os.*;

public class RemoteControlReceiver extends BroadcastReceiver
 {
	MediaControllerCompat.TransportControls mTransportControls;
	MediaControllerCompat controller;
	PlaybackStateCompat mPlaybackState;
    @Override
    public void onReceive(Context context, Intent intent) {
		try
		{
			controller = new MediaControllerCompat(context, MusicService.getItself().getSessionToken());
			mPlaybackState=controller.getPlaybackState();
			mTransportControls=controller.getTransportControls();
		}
		catch (RemoteException e)
		{}

        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (KeyEvent.KEYCODE_MEDIA_PLAY == event.getKeyCode())handlePlayRequest();
			else if(KeyEvent.KEYCODE_MEDIA_PAUSE == event.getKeyCode())handlePlayRequest();
			else if(KeyEvent.KEYCODE_MEDIA_NEXT == event.getKeyCode())mTransportControls.skipToNext();
			else if(KeyEvent.KEYCODE_MEDIA_PREVIOUS == event.getKeyCode())mTransportControls.skipToPrevious();
        }
    }
	private void handlePlayRequest()
	{
		if(mPlaybackState.getState()==mPlaybackState.STATE_PLAYING){
			mTransportControls.pause();
		}else if(mPlaybackState.getState()==mPlaybackState.STATE_PAUSED){
			mTransportControls.play();
		}
	}
	
}
