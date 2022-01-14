package com.smash.up;

import android.content.*;
import android.os.*;
import android.support.annotation.*;
import android.support.design.widget.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;
import com.smash.up.Itens.*;
import com.smash.up.SubActivities.*;

import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import com.smash.up.Adapters.*;
import com.google.android.gms.tasks.*;
import javax.sql.*;
import android.support.v4.app.NavUtils;

public class MainActivity extends AppCompatActivity 
{
	Toolbar toolbar;
	TextView userName;
	
	FirebaseAuth.AuthStateListener mAuthListener;
	NavigationView mNavigationView;
	DrawerLayout mDrawerLayout;
	public static Context ctx;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		ctx=this;
		
		toolbar=(Toolbar)findViewById(R.id.toolbar);
		mNavigationView=(NavigationView)findViewById(R.id.mainNavigationView);
		setSupportActionBar(toolbar);
		userName=(TextView)mNavigationView.inflateHeaderView(R.layout.header).findViewById(R.id.headerTextViewUser);
		userName.setOnLongClickListener(new View.OnLongClickListener(){
				@Override
				public boolean onLongClick(View p1)
				{
					Commons.mAuthReference.signOut();
	
					return false;
				}
			});
		mAuthListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				FirebaseUser user = firebaseAuth.getCurrentUser();
				if (user != null) {
					startFragment(new Fragment(){
							@Override
							public View onCreateView(LayoutInflater i,ViewGroup vp,Bundle b){
								return i.inflate(R.layout.main_frag,vp,false);
							}
						});
					Commons.mDefaultDatabaseRef.child("users").child(user.getUid()).addListenerForSingleValueEvent(
						new ValueEventListener(){
							@Override
							public void onDataChange(DataSnapshot p1)
							{
								userName.setText(p1.getValue(User.class).username);
							}
							@Override
							public void onCancelled(DatabaseError p1)
							{}
						});
					}else startFragment(new RegisterFragment());
					}};
				
			Commons.initializeApp(this, new Commons.OnInitializeAppListener(){
				@Override
				public void onInitialized(boolean b){
					Toast.makeText(MainActivity.this,String.valueOf(b),2000).show();
					Commons.mAuthReference.addAuthStateListener(mAuthListener);
				}		
			});
	
		mNavigationView.inflateMenu(R.menu.main);
		
		mNavigationView.setNavigationItemSelectedListener(new
			NavigationView.OnNavigationItemSelectedListener(){
				@Override
				public boolean onNavigationItemSelected(MenuItem p1)
				{
					if(!p1.isChecked())p1.setChecked(true);
					else p1.setChecked(false);
					switch(p1.getItemId()){
						case R.id.home:startFragment(new Fragment(){
									@Override
									public View onCreateView(LayoutInflater i,ViewGroup vp,Bundle b){
										return i.inflate(R.layout.main_frag,vp,false);
									}
								});return true;
						case R.id.agendMusicas:Agendar();
							return true;
						case R.id.sendMusics:
							sendMusicas();
							return true;
						case R.id.seeMusics:
							startFragment(new SeeMusicasFrag());
							return true;
						case R.id.createAlbum:cAlbum();
							return true;
						case R.id.createArtista:cArtista();return true;
						case R.id.seeArtistas:
							startFragment(new SeeArtistasFrag());
							return true;
						case R.id.player:startActivity(new Intent(MainActivity.this,PlayerActivity.class));
							return true;
						case R.id.seeAlbuns:
							startFragment(new SeeAlbunsFrag());
							return true;
						
					}
					return true;
				}
			});

        mDrawerLayout=(DrawerLayout)findViewById(R.id.mainDrawerLayout);
          ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name, R.string.app_name){

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
				Toast.makeText(MainActivity.this,"Drawer opened",2000).show();
				mNavigationView.bringToFront();
				mDrawerLayout.requestLayout();
            }
        };

        //Setting the actionbarToggle to drawer layout
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        
        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

    }
	public void cAlbum(){
		new SimpleDialog(SimpleDialog.Type.ALBUM).show(getSupportFragmentManager(),"");
	}
	public void cArtista(){
		new SimpleDialog(SimpleDialog.Type.ARTISTA).show(getSupportFragmentManager(),"");
	}
	public void seeMusicas(){
		startFragment(new SeeMusicasFrag());
	}
	public void Agendar(){
		startFragment(new MusicAddFrag());
	}
	public void sendMusicas(){
		startActivity(new Intent(this,SendTasksActivity.class));
	}
	
	public void startFragment(Fragment f){
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.mainFrameLayout,f).addToBackStack(null).commit();
	}

	@Override
	public void onBackPressed()
	{
		try{
		//NavUtils.navigateUpFromSameTask(this);
		}catch(Exception e){
			Log.w("Firecrasher.err",e);
		super.onBackPressed();
		}
	}
	
}
