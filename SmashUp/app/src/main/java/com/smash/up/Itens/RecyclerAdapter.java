package com.smash.up.Itens;

import android.support.v7.widget.*;
import android.view.*;
import android.view.View.*;

public class RecyclerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>
{

	@Override
	public VH onCreateViewHolder(ViewGroup group, int type)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public void onBindViewHolder(VH holder, int position)
	{
		// TODO: Implement this method
	}
	@Override
	public int getItemCount()
	{
		// TODO: Implement this method
		return 0;
	}
	public static class Holder extends RecyclerView.ViewHolder implements OnClickListener{
		public  Holder(View v){
			super(v);
		}
		public void onClick(View v){}
	}
}
