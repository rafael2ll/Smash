package com.smash.player;

import android.content.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.widget.TextView;
import com.smash.player.Helpers.*;
import org.json.*;
import android.support.v7.widget.*;

public class LyricActivity extends AppCompatActivity 
{
	TextView status = null;
	IntentFilter iF = new IntentFilter();
	Toolbar mToolbar;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");

            Log.d("Music", cmd + " : " + action);

            String artist = intent.getStringExtra("artist");
            String album = intent.getStringExtra("album");
            String track = intent.getStringExtra("track");
            boolean playing = intent.getBooleanExtra("playing", false);

            Log.d("Music", artist + " : " + album + " : " + track);

            if (!playing) {
                status.setText("Nenhuma música tocando");
            } else {
				mToolbar.setTitle(track);
				mToolbar.setSubtitle(artist);
                status.setText(artist + "\n" + album + "\n" + track);
				new LyricGetAsync(track, artist, new JSONParser.OnJSONResultListener(){

						@Override
						public void onGet(JSONArray array)
						{
							try
							{
								String lyric=(((JSONObject)array.get(0)).getString("text"));
								status.setText(lyric);
							}catch(JSONException e){}
						}
						}).execute();
					}
				}
				};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lyric_ac);

  
        iF.addAction("com.android.music.musicservicecommand");
        iF.addAction("com.android.music.metachanged");
        iF.addAction("com.android.music.playstatechanged");
        iF.addAction("com.android.music.updateprogress");

        status = (TextView) findViewById(R.id.status);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
        registerReceiver(mReceiver, iF);
        AudioManager manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (!manager.isMusicActive()) {
            status.setText("Nenhuma música tocando");
        }

    }

	@Override
	protected void onResume()
	{
		super.onResume();
		registerReceiver(mReceiver,iF);
	}
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
}
