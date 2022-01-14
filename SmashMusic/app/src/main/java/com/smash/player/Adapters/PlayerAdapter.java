package com.smash.player.Adapters;

import android.support.v4.app.*;
import com.smash.player.*;
import java.util.*;

public class PlayerAdapter extends FragmentStatePagerAdapter
{
	List<PlayerActivity.PlayerFragment> fragList=new ArrayList<>();
	
	public PlayerAdapter(FragmentManager ctx){
		super(ctx);
	}
	public void setFrags(List<PlayerActivity.PlayerFragment> fragList){
		this.fragList=fragList;
		notifyDataSetChanged();
	}
	@Override
	public int getCount()
	{
		return fragList.size();
	}

	@Override
	public Fragment getItem(int p1)
	{
		return fragList.get(p1);
	}
	public String[] getData(int pos){
		return fragList.get(pos).getData();
	}
}
