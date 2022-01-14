package com.smash.smash.Fragments;

import android.os.*;
import android.support.v7.widget.*;
import android.view.*;
import com.smash.smash.Adapters.*;

public class SearchFragment extends RecyclerBaseFrag
{
	SearchRecyclerAdapter mAdapter = new SearchRecyclerAdapter();
	
	
	public SearchFragment(){}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		setRecyclerAdapter(new LinearLayoutManager(getActivity()), mAdapter);
		hideLoading();
	}
	public void query(String s){
		showLoading();
		mAdapter.search(s, new SearchRecyclerAdapter.OnFinishedListener(){

				@Override
				public void onFinished(boolean isDone)
				{
					if(isDone)hideLoading();
				}
			});
	}
}
