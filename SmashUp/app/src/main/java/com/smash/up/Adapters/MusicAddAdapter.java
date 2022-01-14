package com.smash.up.Adapters;

import android.content.*;
import android.media.*;
import android.net.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import com.malinskiy.materialicons.*;
import com.smash.up.*;
import com.smash.up.Itens.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import com.smash.up.R;

public class MusicAddAdapter extends RecyclerAdapter<MusicAddAdapter.Holder>
{
	List<File> itens=new ArrayList<>();
	HashMap<File,SugarMusicRecord> itensToCreate=new HashMap<>();
	Context ctx;
	OnDoneListener listener;
	public MusicAddAdapter(Context c){
		this.ctx=c;
	}
	public void addListener(OnDoneListener o){
		this.listener=o;
	}
	public void populate(List<File> files){
		this.itens=files;
		itensToCreate.clear();
		for(File f : files){
			SugarMusicRecord record=new SugarMusicRecord();
			record.setPath(f.getAbsolutePath());
			MediaPlayer mp=MediaPlayer.create(ctx,Uri.fromFile(f));
			record.setDuration(mp.getDuration());
			record.setMax(f.length());
			itensToCreate.put(f,record);
		}
		notifyDataSetChanged();
	}
	@Override
	public MusicAddAdapter.Holder onCreateViewHolder(ViewGroup group, int type)
	{
		return new Holder(LayoutInflater.from(group.getContext()).inflate(R.layout.music_upload_model,group,false));
	}

	@Override
	public void onBindViewHolder(MusicAddAdapter.Holder holder, int position)
	{
		super.onBindViewHolder(holder, position);
		final File f=itens.get(position);
		
		holder.done.setImageDrawable(new IconDrawable(ctx,Iconify.IconValue.zmdi_save).color(R.color.green_a700).sizeDp(25));
		holder.text.setText(itens.get(position).getName());
		itensToCreate.get(f).setNome(f.getName());
		
		holder.text.addTextChangedListener(new TextWatcher(){
				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TODO: Implement this method
				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					itensToCreate.get(f).setNome(p1.toString());
				}

				@Override
				public void afterTextChanged(Editable p1)
				{
					// TODO: Implement this method
				}
		});
	}

	@Override
	public int getItemCount()
	{
		return itens.size();
	}
	
	public class Holder extends RecyclerAdapter.Holder{
		View v;EditText text;ImageView done;
		public Holder(View v){
			super(v);
			this.v=v;
			text=(EditText)v.findViewById(R.id.musicuploadmodelEditText);
			done=(ImageView)v.findViewById(R.id.musicuploadModelDelete);
			done.setOnClickListener(this);
		}
	
		@Override
		public void onClick(View v)
		{
			SugarMusicRecord record=itensToCreate.get(itens.get(getPosition()));
			listener.onDone(record);
			itensToCreate.remove(itens.get(getPosition()));
			itens.remove(getPosition());
			notifyItemRemoved(getPosition());
			
		}
	}
	public interface OnDoneListener{
		public void onDone(SugarMusicRecord record);
	}
}
