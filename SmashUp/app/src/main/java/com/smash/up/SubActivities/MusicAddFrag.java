package com.smash.up.SubActivities;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.firebase.ui.database.*;
import com.google.common.io.*;
import com.google.firebase.database.*;
import com.smash.up.*;
import com.smash.up.Adapters.*;
import com.smash.up.Itens.*;
import java.io.*;
import java.util.*;
import paul.arian.fileselector.*;

import android.support.v4.app.Fragment;
import com.smash.up.R;
import com.malinskiy.materialicons.*;
import android.view.View.*;
import android.support.v7.app.*;

public class MusicAddFrag extends Fragment
{
	Spinner albuns;
	ImageView addAlbum;
	RecyclerView recyclerView;
	FirebaseListAdapter spinnerAdapter;
	MusicAddAdapter adapter;
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup group,Bundle savedInstanceState)
	{
		View v=inflater.inflate(R.layout.music_upload_ac,group,false);
		addAlbum=(ImageView)v.findViewById(R.id.musicuploadacImageView);
		addAlbum.setImageDrawable(new IconDrawable(getActivity(),Iconify.IconValue.zmdi_plus_circle).colorRes(R.color.deep_orange_a700).sizeDp(30));
		albuns=(Spinner)v.findViewById(R.id.musicuploadAlbuns);
		recyclerView=(RecyclerView)v.findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		adapter=new MusicAddAdapter(getActivity());
		recyclerView.setAdapter(adapter);
		startActivityForResult(new Intent(getActivity(),FileSelectionActivity.class),123);
		populateSpinner();
		addAlbum.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					new AlbumDialog().show(((AppCompatActivity)getActivity()).getSupportFragmentManager(),"");
				}
			});
		adapter.addListener(new MusicAddAdapter.OnDoneListener(){
				@Override
				public void onDone(SugarMusicRecord record)
				{
					record.setProgressDone(0l);
				
					record.setState("stopped");
					record.setAlbumKey(spinnerAdapter.getRef(albuns.getSelectedItemPosition()).getKey());
					record.setArtistaKey("");
					record.save();
					
				}
			});
		return v;
		}

	private void populateSpinner()
	{
		Query q=Commons.mDefaultDatabaseRef.child("albuns");
		spinnerAdapter=new FirebaseListAdapter(getActivity(), Album.class, R.layout.text, q){
			@Override
			protected void populateView(View p1, Object p2, int position)
			{
				((TextView)p1.findViewById(R.id.textTextView)).setText(((Album)p2).getNome());
			}
		
		};
		albuns.setAdapter(spinnerAdapter);
	}

	@TargetApi(Build . VERSION_CODES .JELLY_BEAN )
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 123 && resultCode == Activity .RESULT_OK && Commons.mApp!=null ){
			ArrayList<File> Files = (ArrayList<File>) data.getSerializableExtra(FileSelectionActivity.FILES_TO_UPLOAD); 
			adapter.populate(Files);
		}
	}
}
