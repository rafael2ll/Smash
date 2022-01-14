package com.smash.smash.Fragments;
import android.support.v4.app.*;
import android.support.v4.view.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.smash.*;
import android.support.design.widget.*;
import android.view.*;
import android.os.*;
import com.mindorks.butterknifelite.ButterKnifeLite;
import com.smash.smash.Adapters.*;

public class MyAreaViewPagerFragment extends Fragment
{
	@BindView(R.id.view_pager) ViewPager mViewPager;
	TabLayout mTabLayout;
	MyViewPagerAdapter mAdapter;
	
	
	public MyAreaViewPagerFragment(){}
	public MyAreaViewPagerFragment(TabLayout tabLayout){
		this.mTabLayout= tabLayout;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.view_pager, container, false);
		ButterKnifeLite.bind(this, v);
		mAdapter = new MyViewPagerAdapter(getChildFragmentManager());
		mViewPager.setAdapter(mAdapter);
		if(mTabLayout!=null)mTabLayout.setupWithViewPager(mViewPager, true);
		for(int i=0; i<mTabLayout.getTabCount(); i++){
			mTabLayout.getTabAt(i).setText(null).setIcon(mAdapter.getIcon(i));
		}
		return v;
	}
	
}
