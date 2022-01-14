package com.smash.smash.Adapters;

import android.content.*;
import android.view.*;
import android.view.View.*;
import com.google.firebase.database.*;
import com.smash.smash.*;
import com.smash.smash.Helpers.*;
import com.smash.smash.Holders.*;
import com.smash.smash.Models.*;
import java.util.*;

import com.smash.smash.R;

public class SearchRecyclerAdapter extends SectionedRecyclerAdapter<RecyclerAdapter.Holder>
{
	enum Type{
		MUSICA,
		ALBUM,
		ARTISTA
	};
 boolean isDoneMusica=false, isDoneAlbuns=false, isDoneArtistas=false;
	HashMap<Type,List<?>> mHashMap = new HashMap<>();
	
	List<Musica> mMusicas = new ArrayList<>();
	List<Album> mAlbuns = new ArrayList<>();
	List<Artista> mArtistas = new ArrayList<>();
	
	public SearchRecyclerAdapter(){
		mHashMap.put(Type.MUSICA, mMusicas);
		mHashMap.put(Type.ALBUM, mAlbuns);
		mHashMap.put(Type.ARTISTA, mArtistas);
	}
	
	public void search(String s, final OnFinishedListener o){
		isDoneMusica=false;
		isDoneAlbuns=false;
		isDoneArtistas=false;
		Musica.getQuery().limitToFirst(5)
			.orderByChild("nome").startAt(s).addListenerForSingleValueEvent(new ValueEventListener(){

				@Override
				public void onDataChange(DataSnapshot p1)
				{
					mMusicas.clear();
					for(DataSnapshot ds : p1.getChildren()){
						mMusicas.add(ds.getValue(Musica.class));
					}
					mHashMap.put(Type.MUSICA, mMusicas);
					isDoneMusica=true;
					o.onFinished(isDoneMusica==isDoneAlbuns==isDoneArtistas);
					notifyDataSetChanged();
				}

				@Override
				public void onCancelled(DatabaseError p1)
				{
					// TODO: Implement this method
				}
			});
		Album.getQuery().limitToFirst(5)
			.orderByChild("nome").startAt(s).addListenerForSingleValueEvent(new ValueEventListener(){

				@Override
				public void onDataChange(DataSnapshot p1)
				{
					mAlbuns.clear();
					for(DataSnapshot ds : p1.getChildren()){
						mAlbuns.add(ds.getValue(Album.class));
					}
					isDoneAlbuns=true;
					o.onFinished(isDoneMusica==isDoneAlbuns==isDoneArtistas);
					
					mHashMap.put(Type.ALBUM, mAlbuns);
					notifyDataSetChanged();
				}

				@Override
				public void onCancelled(DatabaseError p1)
				{
					// TODO: Implement this method
				}
			});
		Artista.getQuery().limitToFirst(5)
			.orderByChild("nome").startAt(s).addListenerForSingleValueEvent(new ValueEventListener(){

				@Override
				public void onDataChange(DataSnapshot p1)
				{
					mArtistas.clear();
					for(DataSnapshot ds : p1.getChildren()){
						mArtistas.add(ds.getValue(Artista.class));
					}
					mHashMap.put(Type.ARTISTA, mArtistas);
					isDoneArtistas=true;
					o.onFinished(isDoneMusica==isDoneAlbuns==isDoneArtistas);
					
					notifyDataSetChanged();
				}

				@Override
				public void onCancelled(DatabaseError p1)
				{
					// TODO: Implement this method
				}
			});
	}
	@Override
	public RecyclerAdapter.Holder onCreateViewHolder(ViewGroup p1, int p2)
	{
		
		if(p2==0)return new SearchSectionHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.search_section, p1, false));
			else return new SearchItemHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.search_holder, p1, false));
		}

	@Override
	public int getSectionCount()
	{
		return mHashMap.size();
	}

	@Override
	public int getHeaderViewType(int section)
	{
		// TODO: Implement this method
		return 0;
	}


	@Override
	public int getItemViewType(int section, int relativePosition, int absolutePosition)
	{
		// TODO: Implement this method
		return 1;
	}

	
	

	@Override
	public int getItemCount(int section)
	{
		if(section==0)return mHashMap.get(Type.MUSICA).size();
		else if(section==1)return mHashMap.get(Type.ALBUM).size();
		else return mHashMap.get(Type.ARTISTA).size();
	}

	@Override
	public void onBindHeaderViewHolder(RecyclerAdapter.Holder holder, int section)
	{

		((SearchSectionHolder)holder).feedIt( section==0 ? "MÚSICAS" : section==1? "ÁLBUNS" : "ARTISTAS");
	}

	@Override
	public void onBindViewHolder(RecyclerAdapter.Holder holder, int section, final int relativePosition, int absolutePosition)
	{
		if(section==0){
			((SearchItemHolder)holder).feedIt(mMusicas.get(relativePosition), new OnClickListener(){

					@Override
					public void onClick(View p1)
					{
						MusicMasterHandler.setToPlay(p1.getContext(), mMusicas, false);
						MusicMasterHandler.setToPlayPosition(relativePosition);
						p1.getContext().startService(new Intent(p1.getContext(), MusicService.class).setAction(MusicService.ACTION_START));
						
					}
				});
		}else if(section==1){
			((SearchItemHolder)holder).feedIt(mAlbuns.get(relativePosition));
		} else if(section==2){
			((SearchItemHolder)holder).feedIt(mArtistas.get(relativePosition));
		}
	}
	public interface OnFinishedListener{
		public void onFinished(boolean isDone);
	}
}
