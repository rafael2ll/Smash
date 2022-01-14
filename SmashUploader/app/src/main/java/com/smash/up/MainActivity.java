package com.smash.up;

import android.media.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import com.google.firebase.auth.*;
import com.mindorks.butterknifelite.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.up.Dialogs.*;
import com.smash.up.Frags.*;

import android.support.v7.app.ActionBarDrawerToggle;
import android.content.*;
import com.smash.up.Helpers.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
	@BindView(R.id.toolbar) Toolbar mToolbar;
	@BindView(R.id.mainNavigationView) NavigationView mNavView;
	@BindView(R.id.mainDrawerLayout) DrawerLayout mDrawerLayout;

	public static MediaPlayer mMediaPlayer;
	public static Context ctx;
	TaskService mTaskService;
	ServiceConnection mConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName p1, IBinder p2)
		{
			mTaskService = ((TaskService.TaskBind)p2).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName p1)
		{
			// TODO: Implement this method
		}
	};

	public static Context getContext()
	{
		return ctx;
	}
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		ButterKnifeLite.bind(this);
		ctx=this;
		mMediaPlayer = new MediaPlayer();
		
		setSupportActionBar(mToolbar);
		configDrawer();
		
		FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener(){

				@Override
				public void onAuthStateChanged(FirebaseAuth p1)
				{
					if(p1.getCurrentUser()==null)startFragment(new LoginFragment());
					else startFragment(new LastSentFragment());
				}
			});
		bindService(new Intent(this, TaskService.class), mConnection, BIND_AUTO_CREATE);
    }

	private void configDrawer()
	{
		ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name){

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
				
				mNavView.bringToFront();
				mDrawerLayout.requestLayout();
            }
        };

        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
		mNavView.setNavigationItemSelectedListener(this);
	}
	
	@Override
	public boolean onNavigationItemSelected(MenuItem p1)
	{
		p1.setChecked(true);
		
		switch(p1.getItemId()){
			case R.id.home: startFragment(getSupportFragmentManager().getFragments().get(0));break;
			case R.id.sendTrabalho: startFragment(new SendTrabalhoFrag());break;
			case R.id.agendMusicas: startFragment(new AddMusicaFrag());break;
			case R.id.sendMusics: startFragment(new SendTaskFragment(mTaskService));break;
				
			case R.id.createAlbum: cAlbum();break;
			case R.id.createArtista: cArtista(); break;
			case R.id.createGenero: cGenero();break;
			
			case R.id.seeMusics: startFragment(new SeeMusicasFrag());break;
			case R.id.seeAlbuns: startFragment(new VerAlbunsFrag());break;
			case R.id.seeArtistas: startFragment(new VerArtistasFrag());break;
			
		}
		return true;
	}
	
	public void cAlbum(){
		new SimpleDialog(SimpleDialog.Type.ALBUM).show(getSupportFragmentManager(),"");
	}
	public void cArtista(){
		new SimpleDialog(SimpleDialog.Type.ARTISTA).show(getSupportFragmentManager(),"");
	}
	public  void cGenero(){
		new SimpleDialog(SimpleDialog.Type.GENERO).show(getSupportFragmentManager(),"");
	}
	
	public void startFragment(Fragment frag, String tag){
		FragmentManager fm = getSupportFragmentManager();
		
		if(fm.findFragmentByTag(tag)!=null){
		
		}
		fm.beginTransaction()
		.replace(R.id.mainFrameLayout, frag, tag).addToBackStack(tag)
		.commit();
	}
	public void startFragment(Fragment frag){
		FragmentManager fm = getSupportFragmentManager();
		fm.beginTransaction()
			.replace(R.id.mainFrameLayout, frag).addToBackStack(null)
			.commit();
	}
	public static MediaPlayer getMediaPlayer()
	{
		return mMediaPlayer;
	}
	
}
