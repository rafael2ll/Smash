package com.smash.up.Adapters;

import android.view.*;
import android.widget.*;
import com.mindorks.butterknifelite.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.up.*;
import com.smash.up.Models.*;

import com.smash.up.R;
import android.support.v7.widget.*;
import java.util.*;
import com.google.firebase.database.*;
import android.view.View.*;

public class GenerosRecyclerAdapter extends RecyclerAdapter<GenerosRecyclerAdapter.VHolder>
{

	List<Genero> mItens = new ArrayList<>();
	List<Genero> mCheckedItens = new ArrayList<>();
	
	public GenerosRecyclerAdapter(){
		Genero.getMainRef().addValueEventListener(new ValueEventListener(){

				@Override
				public void onDataChange(DataSnapshot p1)
				{
					mItens.clear();
					for(DataSnapshot ds : p1.getChildren()){
						mItens.add(ds.getValue(Genero.class));
						notifyDataSetChanged();
					}
				}

				@Override
				public void onCancelled(DatabaseError p1)
				{
					// TODO: Implement this method
				}
			});
	}

	public List<String> getCheckedItens()
	{
		List<String> list = new ArrayList<>();
		for(Genero s : mCheckedItens){
			list.add(s.key);
		}
		return list;
	}
	@Override
	public GenerosRecyclerAdapter.VHolder onCreateViewHolder(ViewGroup group, int type)
	{
		
		return new VHolder(LayoutInflater.from(group.getContext()).inflate(R.layout.generos_card_holder, group, false));
	}

	@Override
	public void onBindViewHolder(final GenerosRecyclerAdapter.VHolder holder, int position)
	{
		final Genero genero= mItens.get(position);
		holder.feedIt(genero);
		holder.setOnClick(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					if(mCheckedItens.contains(genero)){
						mCheckedItens.remove(genero);
						holder.markIt(false);
					}else{
						mCheckedItens.add(genero);
						holder.markIt(true);
					}
				}
			});
	}

	@Override
	public int getItemCount()
	{
		return mItens.size();
	}
	
	 class VHolder extends RecyclerAdapter.Holder{
		@BindView(R.id.generos_card_holderTextView)TextView mText;
		@BindView(R.id.generos_card_holderCardView) CardView mCard;
		Genero mGenero;
		boolean isChecked;
		OnClickListener onClick;
		public VHolder(View v){
			super(v);
			ButterKnifeLite.bind(this,v);
			v.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View p1)
					{
						onClick.onClick(p1);
					}
				});
		}
		
	
		public void feedIt(Genero s){
			this.mGenero=s;
			mText.setText(mGenero.nome);
		}
		
		public void markIt(boolean b){
			this.isChecked=b;
			if(b)mCard.setCardBackgroundColor(mCard.getContext().getResources().getColor(R.color.primary));
			else mCard.setCardBackgroundColor(mCard.getContext().getResources().getColor(R.color.accent));
		}
		
		public void setOnClick(OnClickListener o){
			this.onClick=o;
		}
	}
}
