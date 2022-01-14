package com.smash.up.Frags;
import com.smash.up.Itens.*;
import com.smash.up.Adapters.*;
import android.os.*;
import android.view.*;

public class MyAlbunsFrag extends RecyclerBaseFrag
{
	EasyRecyclerAdapter<Album,CircleAlbumHolder> albunsAdapter;
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		//albunsAdapter=new EasyRecyclerAdapter<Album,CircleAlbumHolder>(){};
	}
}
