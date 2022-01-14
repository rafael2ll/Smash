package com.smash.up.Adapters;

import android.content.*;
import android.media.*;
import android.net.*;
import android.support.v7.widget.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import com.smash.up.*;
import com.smash.up.Models.*;
import java.io.*;
import java.util.*;

public class AddMusicaAdapter extends RecyclerView.Adapter<AddMusicaAdapter.Holder>
{
	List<File> itens=new ArrayList<>();
	HashMap<File,SugarMusicRecord> itensToCreate=new HashMap<>();
	Context ctx;
	OnDoneListener listener;
	public AddMusicaAdapter(Context c){
		this.ctx=c;
	}

	public void saveAll()
	{
		for(SugarMusicRecord item :  itensToCreate.values()){
			listener.onDone(item);
			itensToCreate.remove(item);
			itens.remove(item);
		}
		notifyDataSetChanged();
	}
	public void addListener(OnDoneListener o){
		this.listener=o;
	}
	public void populate(List<File> files){
		this.itens=files;
		itensToCreate.clear();
		for(File f : files){
			MediaMetadataRetriever mmr = new MediaMetadataRetriever();
			mmr.setDataSource(f.getAbsolutePath());
			String name = mmr.extractMetadata(mmr.METADATA_KEY_TITLE);
			int duration = Integer.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
			SugarMusicRecord record=new SugarMusicRecord();
			
			record.setPath(f.getAbsolutePath());
			record.setNome(name);
			record.setDuration(duration);
			record.setMax(f.length());
			itensToCreate.put(f,record);
		}
		notifyDataSetChanged();
	}
	@Override
	public Holder onCreateViewHolder(ViewGroup group, int type)
	{
		return new Holder(LayoutInflater.from(group.getContext()).inflate(R.layout.add_musica_holder,group,false));
	}

	@Override
	public void onBindViewHolder(AddMusicaAdapter.Holder holder, int position)
	{
		final File f=itens.get(position);

		holder.text.setText(itensToCreate.get(f).nome);
		
		holder.text.addTextChangedListener(new TextWatcher(){
				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TODO: Implement this method
				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					itensToCreate.get(f).setNome(p1.toString().trim());
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
