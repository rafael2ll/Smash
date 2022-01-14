package com.smash.up.SubActivities;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import com.smash.up.*;
import com.smash.up.Adapters.*;
import com.smash.up.Itens.*;

import com.smash.up.R;
import com.firebase.ui.database.*;

public class SeeArtistasFrag extends Fragment
{
	RecyclerView mRecycler;
	FirebaseRecyclerAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		adapter = new FirebaseRecyclerAdapter<Artista,SeeMusicasHolder>(Artista.class, R.layout.see_musics_item, SeeMusicasHolder.class, Artista.getQuery()){
			@Override
			protected void populateViewHolder(SeeMusicasHolder p1, Artista p2, int p3)
			{
				Log.w("Adapter",p2.getNome());
				p1.feedItWithArtista(getActivity(),p2,adapter.getRef(p3));
			}
		};
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v=inflater.inflate(R.layout.simple_recycler,container,false);
		mRecycler=(RecyclerView)v.findViewById(R.id.recyclerView);
		mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
		mRecycler.setAdapter(adapter);
		return v;
	}
	
}
