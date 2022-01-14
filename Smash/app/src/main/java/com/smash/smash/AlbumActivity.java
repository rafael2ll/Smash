package com.smash.smash;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.firebase.database.*;
import com.mindorks.butterknifelite.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.smash.Fragments.*;
import com.smash.smash.Helpers.*;
import com.smash.smash.Holders.*;
import com.smash.smash.Models.*;
import java.util.*;

import android.support.v7.widget.Toolbar;

public class AlbumActivity extends AppCompatActivity
{
	@BindView(R.id.albumactivitycollapsing) CollapsingToolbarLayout mColToolbar;
	@BindView(R.id.toolbar) Toolbar mToolbar;
	@BindView(R.id.albumactivityImageViewBackground) ImageView mBackground;
	@BindView(R.id.albumactivityImageViewFront) ImageView mFront;
	@BindView(R.id.recycler) RecyclerView mRecyclerView;
	@BindView(R.id.recyclerProgressBar) ProgressBar mProgress;
	@BindView(R.id.albumactivityPlay) FloatingActionButton mPlayShuffle;
	
	Album mAlbum;
	EasyRecyclerAdapter<Musica, MusicasHolder> mAdapter;
	List<Musica> albumMusicas= new ArrayList<>();

	public static String ALBUM_KEY= "albumKey";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_activity);
		ButterKnifeLite.bind(this);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
		mColToolbar.setExpandedTitleTextAppearance(R.style.CollapsingToolbarTextAppearance);
		mColToolbar.setExpandedTitleColor(Color.WHITE);
		String albumKey= getIntent().getStringExtra(ALBUM_KEY);
		Album.getAlbum(albumKey, new ValueEventListener(){

				@Override
				public void onDataChange(DataSnapshot p1)
				{
					mAlbum = p1.getValue(Album.class);
					loadAll();
				}

				@Override
				public void onCancelled(DatabaseError p1)
				{
					// TODO: Implement this method
				}
			});
		mPlayShuffle.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					MusicMasterHandler.setToPlay(AlbumActivity.this, albumMusicas, true);
					MusicMasterHandler.shuffleToPlay();
					MusicMasterHandler.setToPlayPosition(0);
					startService(new Intent(AlbumActivity.this, MusicService.class).setAction(MusicService.ACTION_PLAY));
					
				}
			});
	}
	
	private void loadAll()
	{
		Album.incrementViews(mAlbum);
		mColToolbar.setTitle(mAlbum.getNome());
		PicassoHelper.get().load(mAlbum.imageUri).transform(new BlurTransform(this))
		.fit().centerCrop().into(mBackground);
		PicassoHelper.get().load(mAlbum.imageUri).transform(new CropCircleTransformation())
		.into(mFront);
		albumMusicas.clear();
		
		mAdapter = new EasyRecyclerAdapter<Musica, MusicasHolder>(albumMusicas, Musica.class, R.layout.my_music_holder, MusicasHolder.class){

			@Override
			protected void populateViewHolder(MusicasHolder viewHolder, final Musica model, int position)
			{
				mProgress.setVisibility(View.GONE);
				viewHolder.feedIt(model, new MusicasHolder.OnItemClickListener(){

						@Override
						public void onClick(View v)
						{
							if(v.getId() == R.id.mymusicholderImageViewMore){
								new MusicaMoreDialog(model).show(getSupportFragmentManager(),"");
							}else{
								MusicMasterHandler.setToPlay(AlbumActivity.this, albumMusicas, true);
								MusicMasterHandler.setToPlayPosition(albumMusicas.indexOf(model));
								AlbumActivity.this.startService(new Intent(AlbumActivity.this,MusicService.class).setAction(MusicService.ACTION_PLAY));
								//ctx.startActivity(new Intent(ctx,PlayerActivity.class));
							}
						}
					});
			}
		};
		Album.getMusicas(mAlbum.key, new Musica.OnFindMusicaListener(){

				@Override
				public void onResult(Musica m, DatabaseError e)
				{
					if(m!=null)albumMusicas.add(m);
					mAdapter.notifyDataSetChanged();
				}
			});
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setAdapter(mAdapter);
	}
}
