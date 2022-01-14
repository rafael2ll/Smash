package com.smash.up.Frags;
import android.os.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.smash.up.*;

public class RecyclerBaseFrag extends FirebaseBaseFrag
{
	public RecyclerView mRecycler, mHorizontalRecycler;
	public ProgressBar mProgress;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v=inflater.inflate(R.layout.recycler, container, false);
		mRecycler = (RecyclerView) v.findViewById(R.id.recycler);
		mHorizontalRecycler = (RecyclerView) v.findViewById(R.id.recyclerViewHorizont);
		mProgress = (ProgressBar) v.findViewById(R.id.recyclerProgressBar);
		return v;
	}
	public  void hideLoading(){
		mProgress.setVisibility(View.GONE);
	}
	public void showHorizontalandSetAdapter(RecyclerView.LayoutManager lm, RecyclerView.Adapter adapter){
		mHorizontalRecycler.setVisibility(View.VISIBLE);
		mHorizontalRecycler.setLayoutManager(lm);
		mHorizontalRecycler.setAdapter(adapter);
	}
	
	public void showLoading(){
		mProgress.setVisibility(View.VISIBLE);
	}
	
	public void setRecyclerAdapter(RecyclerView.LayoutManager l,RecyclerView.Adapter ada){
		mRecycler.setLayoutManager(l);
		mRecycler.setAdapter(ada);
	}
}
