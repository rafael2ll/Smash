package com.smash.smash;

import android.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.support.annotation.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.*;
import com.google.firebase.crash.*;
import com.mindorks.butterknifelite.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.smash.Fragments.*;
import com.smash.smash.Helpers.*;
import com.smash.smash.Models.*;
import java.util.*;

import android.Manifest;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
	@BindView(R.id.toolbar) Toolbar mToolbar;
	@BindView(R.id.tabanim_maincontent) CoordinatorLayout mLayout;
	@BindView(R.id.tabLayout) TabLayout mTabLayout;
	@BindView(R.id.mNavigation_View) NavigationView mNavView;
	@BindView(R.id.mDrawer_Layout) DrawerLayout mDrawerLayout;
	
	List<Musica> mLastMusicas = new ArrayList<>();
	int mLastPosition=0;
	SearchFragment searchFrag;
	Musica mPlayingNow;
	private static final int STORAGE_PERMISSION_CODE = 23;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		ButterKnifeLite.bind(this);
		FirebaseCrash.report(new Exception("My first Android non-fatal error"));
		if(FirebaseAuth.getInstance().getCurrentUser()==null){
			login();
			finish();
		}
		login();
		searchFrag= new SearchFragment();
		setSupportActionBar(mToolbar);
		
		ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout , mToolbar, R.string.app_name, R.string.app_name);
		mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
		mNavView.setNavigationItemSelectedListener(this);
		
		mTabLayout.setSelectedTabIndicatorColor(Color.WHITE);
		mTabLayout.setSelectedTabIndicatorHeight(7);
		
	
		startFragment(new MyAreaViewPagerFragment(mTabLayout), MyMusicasFrag.TAG, false);
		if(!isWriteStorageAllowed())requestStoragePermission();
		
	}
		

	@Override
	public boolean onNavigationItemSelected(MenuItem p1)
	{
		switch(p1.getItemId()){
			case R.id.search: //startActivity(new Intent(this, SearchActivity.class));
		}
		return false;
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		bindService(new Intent(this, MusicService.class).setAction(MusicService.ACTION_START), new ServiceConnection(){

				@Override
				public void onServiceConnected(ComponentName p1, IBinder p2)
				{
					// TODO: Implement this method
				}

				@Override
				public void onServiceDisconnected(ComponentName p1)
				{
					// TODO: Implement this method
				}
			} , BIND_AUTO_CREATE);
		
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		FrameLayout fl= (FrameLayout)findViewById(R.id.playerBottom);
		fl.removeAllViews();
		fl.addView(MusicService.mBottomPlayer.get());
			Log.w("Smash","Service started");
		
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //*** setOnQueryTextFocusChangeListener ***
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					
				}
			});

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

				@Override
				public boolean onQueryTextSubmit(String query) {

					return false;
				}

				@Override
				public boolean onQueryTextChange(String searchQuery) {
					searchFrag.query(searchQuery);
					return true;
				}
			});

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
				@Override
				public boolean onMenuItemActionCollapse(MenuItem item) {
					getSupportFragmentManager().popBackStack();
					return true;  
				}

				@Override
				public boolean onMenuItemActionExpand(MenuItem item) {
					startFragment(searchFrag,"SearchFrag",true);
					return true;  
				}
			});
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	private void startFragment(Fragment mFrag, String tag, boolean backStack)
	{
		getSupportFragmentManager().enableDebugLogging(true);
		FragmentTransaction ft=getSupportFragmentManager().beginTransaction().replace(R.id.mainReplacer, mFrag, tag);
		if(backStack)ft.addToBackStack(tag);
		ft.commit();
	}
	private boolean isWriteStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }
	private void requestStoragePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if(requestCode == STORAGE_PERMISSION_CODE){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Displaying a toast
                Toast.makeText(this,"Permission granted now you can read the storage",Toast.LENGTH_LONG).show();
            }else{
                //Displaying another toast if permission is not granted
                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
    }
	private void login()
	{
		FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener(){

				@Override
				public void onAuthStateChanged(FirebaseAuth p1)
				{
					if(p1.getCurrentUser()==null)startActivity(new Intent(MainActivity.this, LoginActivity.class));
					else{}
				}
			});
	}
}
