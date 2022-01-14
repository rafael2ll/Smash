package com.smash.player.Models;

import android.os.*;
import android.support.v4.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.smash.player.*;

public abstract class RecyclerFragment extends Fragment
{
	public RecyclerView mRecyclerView;
	public ProgressBar mLoadingView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.recycler_fragment,container,false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		mRecyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);
		mLoadingView=(ProgressBar)view.findViewById(R.id.recyclerfragmentProgressBar);
		readyToGo();
	}
	public void showLoading(){
		mLoadingView.setVisibility(View.VISIBLE);
	}
	public void hideLoading(){
		mLoadingView.setVisibility(View.GONE);
	}
	public void setRecyclerAdapter(RecyclerView.LayoutManager lm,RecyclerView.Adapter adapter){
		mRecyclerView.setLayoutManager(lm);
		mRecyclerView.setAdapter(adapter);
	}
	public abstract void readyToGo();
}
