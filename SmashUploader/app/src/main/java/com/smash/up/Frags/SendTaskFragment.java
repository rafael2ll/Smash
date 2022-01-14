package com.smash.up.Frags;

import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.google.firebase.database.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.up.*;
import com.smash.up.Helpers.*;
import com.smash.up.Models.*;

import com.smash.up.R;
import com.mindorks.butterknifelite.*;
import com.smash.up.Holders.*;
import android.view.View.*;
import java.util.*;

public class SendTaskFragment extends Fragment
{
	@BindView(R.id.sendfragTextViewName) TextView mNome;
	@BindView(R.id.sendfragTextViewArtista) TextView mAlbum;
	@BindView(R.id.sendfragProgressBar) ProgressBar mProgress;
	@BindView(R.id.sendfragImageViewSend) ImageView mProgressText;
	@BindView(R.id.recycler) RecyclerView mRecycler;
	@BindView(R.id.recyclerProgressBar) ProgressBar mProgressRecyer;
	
	EasyRecyclerAdapter mAdapter;
	TaskService mTaskService;
	List<SugarMusicRecord> mItens= new ArrayList<>();
	
	public SendTaskFragment(TaskService mTaskService){
		this.mTaskService= mTaskService;
		mTaskService.setListener(new TaskService.OnSendListener(){

				@Override
				public void onStartSending(final SugarMusicRecord o)
				{
					mNome.setText(o.nome);
					mProgress.setIndeterminate(true);
					mAdapter.notifyDataSetChanged();
					Album.getMainRef().child(o.albumKey).addListenerForSingleValueEvent(new ValueEventListener(){

							@Override
							public void onDataChange(DataSnapshot p1)
							{
								Album album = p1.getValue(Album.class);
								mAlbum.setText(album.nome+" • "+ album.artistaNome);
							}

							@Override
							public void onCancelled(DatabaseError p1)
							{
								// TODO: Implement this method
							}
						});
				}

				@Override
				public void onSendProgress(int max, int sent)
				{
					mProgress.setIndeterminate(false);
					mProgress.setMax(max);mProgress.setProgress(sent);
				}

				@Override
				public void onSent(boolean b)
				{
					mProgress.setIndeterminate(true);
					mItens = SugarMusicRecord.listAll(SugarMusicRecord.class);
					mAdapter.setItens(mItens);
					mAdapter.notifyItemRemoved(0);
				}
			});
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.send_frag, container, false);
		ButterKnifeLite.bind(this, v);
		mItens = SugarMusicRecord.listAll(SugarMusicRecord.class);
		
		mAdapter = new EasyRecyclerAdapter<SugarMusicRecord, TaskHolder>(mItens, SugarMusicRecord.class, R.layout.send_task_item, TaskHolder.class){
			
			
			@Override
			protected void populateViewHolder(TaskHolder viewHolder, SugarMusicRecord model, int position)
			{
				viewHolder.feedIt(model);
				mProgressRecyer.setVisibility(View.GONE);
			}
		};
		mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
		mRecycler.setAdapter(mAdapter);
		
		mProgressText.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					mTaskService.upAll();
				}
			});
		
		return v;
	}
}
