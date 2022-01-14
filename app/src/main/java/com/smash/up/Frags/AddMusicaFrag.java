package com.smash.up.Frags;

import android.support.v4.app.*;
import android.support.v7.widget.*;
import android.widget.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.up.R;
import com.smash.up.Helpers.*;
import android.view.*;
import android.os.*;
import com.mindorks.butterknifelite.ButterKnifeLite;
import paul.arian.fileselector.*;
import android.content.*;
import java.util.*;
import java.io.*;
import com.smash.up.Holders.*;
import com.smash.up.Adapters.*;
import com.smash.up.Models.*;
import android.view.View.*;

public class AddMusicaFrag extends Fragment
{
	@BindView(R.id.musicaaddfragNewAlbum) ImageView mNewAlbum;
	@BindView(R.id.musicaddfragAlbuns) Spinner mAlbunsSpinner;
	@BindView(R.id.recyclerView) RecyclerView mRecycler;
	
	AddMusicaAdapter mAdapter;
	FirebaseListAdapter<Album> spinnerAdapter;
	int requestCode = 234;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.musica_add_frag, container, false);
		ButterKnifeLite.bind(this, v);
		v.findViewById(R.id.musicaaddfragButtonSendAll).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					mAdapter.saveAll();
				}
			});
		mAdapter = new AddMusicaAdapter(getActivity());
		mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
		mRecycler.setAdapter(mAdapter);
		
		spinnerAdapter = new FirebaseListAdapter<Album>(getActivity(), Album.class, R.layout.text, Album.getQuery()){
			@Override
			protected void populateView(View view, Album album, int position)
			{
				((TextView)view).setText(album.getNome());
			}
		};
		mAlbunsSpinner.setAdapter(spinnerAdapter);
		mAdapter.addListener(new AddMusicaAdapter.OnDoneListener(){
				@Override
				public void onDone(SugarMusicRecord record)
				{
					Album album =spinnerAdapter.getItem(mAlbunsSpinner.getSelectedItemPosition());
					record.setProgressDone(0l);
					record.setState("stopped");
					record.setAlbumKey(album.key);
					record.setArtistaKey(album.artistaKey);
					record.save();

				}
			});
		startActivityForResult(new Intent(getActivity(), FileSelectionActivity.class), requestCode);
		
		return v;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == this.requestCode && resultCode== getActivity().RESULT_OK){
			List<File> mFileList = data.getParcelableArrayListExtra(FileSelectionActivity.FILES_TO_UPLOAD);
			mAdapter.populate(mFileList);
		
			}
	}
}
