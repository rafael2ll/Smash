package com.smash.smash.Adapters;

import android.support.v4.app.*;
import com.smash.smash.*;
import com.smash.smash.Fragments.*;
import java.util.*;

public class MyViewPagerAdapter extends FragmentStatePagerAdapter
{
	List<Fragment> mFragList = new ArrayList<>();
	
	public MyViewPagerAdapter(FragmentManager fm){
		super(fm);
		
		mFragList = Arrays.asList(new MyMusicasFrag(), new MyAlbunsFrag(), new MyArtistasFrag(), new Fragment());
	}

	public int getIcon(int i)
	{
		switch(i){
			case 0: return R.drawable.ic_music_circle;
			case 1: return R.drawable.ic_album_white;
			case 2: return R.drawable.ic_account_circle;
			case 3: return R.drawable.ic_library_music;
			
			default: return R.drawable.ic_bookmark_music;
		}
	}

	@Override
	public Fragment getItem(int p1)
	{
		return mFragList.get(p1);
	}

	@Override
	public int getCount()
	{
		return mFragList.size();
	}

	@Override
	public CharSequence getPageTitle(int position)
	{
		// TODO: Implement this method
		return null;
	}
	
}
