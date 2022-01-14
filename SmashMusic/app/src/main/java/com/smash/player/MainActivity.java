package com.smash.player;

import android.os.*;
import android.support.design.widget.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import com.smash.player.Adapters.*;
import com.smash.player.Frags.*;
import com.smash.player.Helpers.*;
import com.malinskiy.materialicons.*;
import mehdi.sakout.dynamicbox.*;
import android.view.*;
import android.content.*;

public class MainActivity extends AppCompatActivity 
{
	Toolbar mToolbar;
	DynamicBox mDB;
	ViewPager mViewPager;
	TabLayout mTabLayout;
	ActionBarDrawerToggle mActionBarDrawerToggle;
	MainPagerAdapter mViewPagerAdapter;
	DrawerLayout mDrawerLayout;
	NavigationView mNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
	
		mToolbar=(Toolbar)findViewById(R.id.toolbar);
		mDrawerLayout=(DrawerLayout)findViewById(R.id.mDrawer_Layout);
		mNavigationView=(NavigationView)findViewById(R.id.mNavigation_View);
		mViewPager=(ViewPager)findViewById(R.id.viewpager);
		mTabLayout=(TabLayout)findViewById(R.id.tabLayout);
		mDB=new DynamicBox(this,mViewPager);
		mDB.showLoadingLayout();
		setSupportActionBar(mToolbar);
		configNav();configDrawer();
		new SmashAsync<Void>(){

			@Override
			public Void onPrepare()
			{
				MusicProvider.updateSongList(MainActivity.this,false);
				return null;
			}

			@Override
			public void onResult(Void result)
			{
				mDB.hideAll();
				mViewPagerAdapter=new MainPagerAdapter(MainActivity.this,getSupportFragmentManager())
					.addFrag(new MainMusicFrag(), "Músicas", Iconify.IconValue.zmdi_audio)
					.addFrag(new MainMusicFrag(), "Artistas", Iconify.IconValue.zmdi_mic)
					.addFrag(new MainMusicFrag(), "Albuns", Iconify.IconValue.zmdi_dot_circle_alt);
				mViewPager.setAdapter(mViewPagerAdapter);
				mTabLayout.setupWithViewPager(mViewPager);
				mViewPagerAdapter.configTabs(mTabLayout);
				
			}
		}.execute();
		}
	private void configDrawer(){
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, 
			mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }
	private void configNav()
	{
		mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){

				@Override
				public boolean onNavigationItemSelected(MenuItem p1)
				{
					p1.setChecked(!p1.isChecked());
					switch(p1.getItemId()){
						case R.id.rescan:MusicProvider.updateSongList(MainActivity.this,true);break;
						case R.id.lyric_ac_open:startActivity(new Intent(MainActivity.this,LyricActivity.class));break;
					}
					return true;
				}
			});
	}
}
