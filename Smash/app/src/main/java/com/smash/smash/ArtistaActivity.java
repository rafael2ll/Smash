package com.smash.smash;
import android.graphics.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.widget.*;
import com.mindorks.butterknifelite.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.smash.*;
import com.smash.smash.Helpers.*;
import com.smash.smash.Holders.*;
import com.smash.smash.Models.*;
import java.util.*;

import android.support.v7.widget.Toolbar;
import com.smash.smash.R;
import android.support.v4.view.*;

public class ArtistaActivity extends AppCompatActivity
{
	@BindView(R.id.albumactivitycollapsing) CollapsingToolbarLayout mColToolbar;
	@BindView(R.id.toolbar) Toolbar mToolbar;
	@BindView(R.id.albumactivityImageViewBackground) ImageView mBackground;
	@BindView(R.id.albumactivityImageViewFront) ImageView mFront;
	@BindView(R.id.view_pager) ViewPager mPager;
	
	Artista mArtista;
	EasyRecyclerAdapter<Musica, MusicasHolder> mAdapter;
	List<Musica> albumMusicas= new ArrayList<>();

	public static String ARTISTA_KEY= "artistaKey";

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
		String artistaKey= getIntent().getStringExtra(ARTISTA_KEY);
		
	}
}
