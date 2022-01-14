package com.smash.up.Holders;

import android.content.*;
import android.view.*;
import android.widget.*;
import com.smash.up.*;
import com.smash.up.Adapters.*;
import com.smash.up.Models.*;
import com.squareup.picasso.*;
import com.smash.up.Helpers.*;

public class CircleAlbumHolder extends RecyclerAdapter.Holder
{
	ImageView image;
	OnAlbumClick listener;
	Album album;
	TextView nome;
	View item;Context ctx;
	
	public CircleAlbumHolder(View v){
		super(v);
		this.item=v;
		image=(ImageView)v.findViewById(R.id.albumitemImageView);
		nome=(TextView)v.findViewById(R.id.albumitemTextViewNome);
		item.setOnClickListener(this);
		image.setOnClickListener(this);
		image.setOnLongClickListener(new View.OnLongClickListener(){

				@Override
				public boolean onLongClick(View p1)
				{
					if(listener!=null)listener.onLongClick(album);
					return true;
				}
			});
	}

	@Override
	public void onClick(View v)
	{
		if(listener!=null)listener.onClick(album);
		super.onClick(v);
	}
	
	public void populateHolder(final Context ctx,final Album a,OnAlbumClick o){
		nome.setSelected(true);
		nome.setText(a.getNome());this.ctx=ctx;this.listener=o;this.album=a;
		nome.setTextAppearance(ctx,R.style.TextAppearance_AppCompat);
		Picasso.with(ctx).load(a.imageUri)
					.fit().centerCrop().transform(new CropCircleTransformation())
					.into(image);
	}
	public interface OnAlbumClick{
		public void onClick(Album a);
		public void onLongClick(Album b);
	}
}
