package com.smash.up.Frags;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.up.R;
import com.smash.up.Helpers.*;
import com.mindorks.butterknifelite.*;
import com.smash.up.Holders.*;
import com.smash.up.Models.*;
import android.view.View.*;
import java.util.*;
import com.google.firebase.database.*;

public class LastSentFragment extends Fragment
{
	EasyRecyclerAdapter mAdapter;
	List<Musica> mItens = new ArrayList<>();
	@BindView(R.id.recycler) RecyclerView mRecycler;
	@BindView(R.id.recyclerProgressBar) ProgressBar mProgress;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.last_sent_frag, container, false);
		ButterKnifeLite.bind(this, v);
		
		Musica.getQuery().limitToLast(30).addValueEventListener(new ValueEventListener(){

				@Override
				public void onDataChange(DataSnapshot p1)
				{
					mItens.clear();
					for(DataSnapshot ds : p1.getChildren()){
						mItens.add(ds.getValue(Musica.class));
						Collections.reverse(mItens);
						mAdapter.notifyDataSetChanged();
					}
				}

				@Override
				public void onCancelled(DatabaseError p1)
				{
					// TODO: Implement this method
				}
			});
		mAdapter = new EasyRecyclerAdapter<Musica, LastSentHolder>(mItens, Musica.class, R.layout.last_sent_model, LastSentHolder.class ){

			@Override
			protected void populateViewHolder(LastSentHolder viewHolder, final Musica model, int position)
			{
				viewHolder.feedIt(model, new OnClickListener(){

						@Override
						public void onClick(View p1)
						{
							new BottomPlayerFrag(model).show(getChildFragmentManager(),"");
						}
					});
				mProgress.setVisibility(View.GONE);
			}
		};
		
		mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
		mRecycler.setAdapter(mAdapter);
		return v;
	}
	
	
}
