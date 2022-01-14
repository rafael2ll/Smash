package com.smash.smash.Adapters;

import com.smash.smash.Models.*;
import android.support.v4.app.*;
import com.smash.smash.Fragments.*;
import android.support.v4.content.*;

public class ArtistaViewPagerAdapter extends FragmentStatePagerAdapter
{
	Artista mArtista;
	
	public ArtistaViewPagerAdapter(Artista a, FragmentManager fm){
		super(fm);
		this.mArtista=a;
	}
	
	@Override
	public int getCount()
	{
		return 2;
	}

	@Override
	public Fragment getItem(int p1)
	{
	
		return p1==0 ? new ArtistaTopMusicas(mArtista) : new ArtistaAlbunsFrag(mArtista);
	}

}
