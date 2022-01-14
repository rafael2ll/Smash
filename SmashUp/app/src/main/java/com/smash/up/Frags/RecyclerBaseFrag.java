package com.smash.up.Frags;

import android.os.*;
import android.support.v4.app.*;
import android.support.v7.widget.*;
import android.view.*;
import com.smash.up.*;

public class RecyclerBaseFrag extends Fragment
{
	RecyclerView mRecyclerView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v=inflater.inflate(R.layout.simple_recycler,container,false);
		mRecyclerView=(RecyclerView)v.findViewById(R.id.recyclerView);
		return v;
	}
	public void hideLoading(){
		
	}
	public void setRecyclerAdapter(RecyclerView.LayoutManager l,RecyclerView.Adapter ada){
		mRecyclerView.setLayoutManager(l);
		mRecyclerView.setAdapter(ada);
	}
}
