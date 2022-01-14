package com.smash.player.Adapters;

import android.content.*;
import android.graphics.drawable.*;
import android.support.v4.app.*;
import com.malinskiy.materialicons.*;
import java.util.*;
import android.support.design.widget.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;

public class MainPagerAdapter extends FragmentStatePagerAdapter
{
	List<Fragment> fragList=new ArrayList<>();
	List<String> titleList=new ArrayList<>();
	HashMap<String,Drawable> iconHash=new HashMap<>();
	Context ctx;
	public MainPagerAdapter(Context ctx,FragmentManager fm){
		super(fm);
		this.ctx=ctx;
	}

	public void configTabs(TabLayout mTabLayout)
	{
		mTabLayout.setSelectedTabIndicatorColor(Color.WHITE);
		mTabLayout.setSelectedTabIndicatorHeight(5);
		mTabLayout.setTabTextColors(ctx.getResources().getColor(R.color.grey_900_half),Color.WHITE);
		int i=0;
		for(String t : titleList){
			View v= LayoutInflater.from(ctx).inflate(R.layout.tab_view,null,false);
			((TextView)v.findViewById(R.id.tabviewTextView)).setText(t);
			((ImageView)v.findViewById(R.id.tabviewImageView)).setImageDrawable(iconHash.get(t));
			mTabLayout.getTabAt(i).setCustomView(v);
			i++;
		}
	}

	public List<String> getPageTitles()
	{
		return titleList;
	}
	public MainPagerAdapter addFrag(Fragment t,String title,Iconify.IconValue e){
		fragList.add(t);titleList.add(title);
		iconHash.put(title,new IconDrawable(ctx,e).colorRes(R.color.icons).sizeDp(22));
		notifyDataSetChanged();
		return this;
	}
	@Override
	public CharSequence getPageTitle(int position)
	{
		return titleList.get(position);
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
}
