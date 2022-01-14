package com.smash.smash.Fragments;
import android.os.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.smash.smash.*;
import android.support.design.widget.*;

public class RecyclerBaseFrag extends FirebaseBaseFrag
{
	public RecyclerView mRecycler;
	public ProgressBar mProgress;
	public FloatingActionButton mFab;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v=inflater.inflate(R.layout.recycler, container, false);
		mRecycler = (RecyclerView) v.findViewById(R.id.recycler);
		mProgress = (ProgressBar) v.findViewById(R.id.recyclerProgressBar);
		mFab = (FloatingActionButton) v.findViewById(R.id.fab);
		return v;
	}
	
	public void showFab(){
		mFab.setVisibility(View.VISIBLE);
	}
	
	public  void hideLoading(){
		mProgress.setVisibility(View.GONE);
	}
	
	public void showLoading(){
		if(mProgress!=null)mProgress.setVisibility(View.VISIBLE);
	}
	
	public void setRecyclerAdapter(RecyclerView.LayoutManager l,RecyclerView.Adapter ada){
		mRecycler.setLayoutManager(l);
		mRecycler.setAdapter(ada);
	}
}
