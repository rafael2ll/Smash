package com.smash.up.Holders;

import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.mindorks.butterknifelite.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.up.*;
import com.smash.up.Models.*;

import com.smash.up.R;
import android.support.design.widget.*;
import android.view.View.*;
import com.google.firebase.database.*;

public class TaskHolder extends RecyclerView.ViewHolder
{
	@BindView(R.id.sendtaskitemTextViewNome) TextView mNome;
	@BindView(R.id.sendtaskitemTextViewOther) TextView mAlbum;
	@BindView(R.id.sendtaskitemImageViewDelete) ImageView mDelete;
	
	SugarMusicRecord mRecord;
	public TaskHolder(View v){
		super(v);
		ButterKnifeLite.bind(this, v);
		mDelete.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					delete(p1);
				}
			});
	}
	public void delete(View v){
		Snackbar.make(v, "Deseja deletar "+mRecord.nome+"?", Snackbar.LENGTH_LONG)
			.setAction("Sim", new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					mRecord.delete();
				}
			}).show();
	}
	public void feedIt(final SugarMusicRecord o){
		this.mRecord = o;
		
		mNome.setText(o.nome);
		Album.getMainRef().child(o.albumKey).child("nome").addListenerForSingleValueEvent(new ValueEventListener(){

				@Override
				public void onDataChange(DataSnapshot p1)
				{
					String name = p1.getValue(String.class);
					mAlbum.setText(name+" • "+android.text.format.Formatter.formatFileSize(MyApplication.getContext(), (long)o.getMax()));
				}

				@Override
				public void onCancelled(DatabaseError p1)
				{
					// TODO: Implement this method
				}
			});
	}
}
